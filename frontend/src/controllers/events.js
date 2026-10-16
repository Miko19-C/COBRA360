import { state } from '../core/state.js';
import { api } from '../services/api.js';
import { loadData } from '../services/data.js';
import { $, toast, setMenuOpen } from '../components/ui.js';
import { detail, paymentDialog } from '../components/dialogs.js';

/** Registra los eventos una sola vez; recibe la navegación como dependencia. */
export function bindEvents({ bootstrap, render, navigate }) {
document.addEventListener('click', async event => {
  const button = event.target.closest('[data-action],[data-view]'); if (!button) return;
  if (button.disabled) return;
  if (button.dataset.view) { await navigate(button.dataset.view); return; }
  const action = button.dataset.action;
  try {
    if (action === 'demo-login') { const form = $('#login-form'); form.username.value = button.dataset.user; form.password.value = 'Cobra360!2026'; form.requestSubmit(); }
    if (action === 'password') { const input = $('#login-form [name=password]'); input.type = input.type === 'password' ? 'text' : 'password'; button.textContent = input.type === 'password' ? 'Ver' : 'Ocultar'; }
    if (action === 'reload') await bootstrap();
    if (action === 'menu') setMenuOpen(!$('#sidebar').classList.contains('open'));
    if (action === 'close-menu') setMenuOpen(false, true);
    if (action === 'logout') { await api('/logout', { method: 'POST' }); await bootstrap(); }
    if (action === 'notifications') { await navigate('promesas'); state.status = 'INCUMPLIDA'; await render(); }
    if (action === 'detail') await detail(button.dataset.id);
    if (action === 'close-dialog') $('#detail-dialog').close();
    if (action === 'status') { state.status = button.dataset.status; state.page = 1; await render(); }
    if (action === 'page') { state.page = Number(button.dataset.page); await render(); }
    if (action === 'pay-promise') paymentDialog(state.promesas.find(p => p.id_promesa === Number(button.dataset.id)));
    if (action === 'pay-debt') paymentDialog(null, button.dataset.id);
    if (action === 'new-payment') { if (!state.cartera.some(d => Number(d.saldo_actual) > 0)) return toast('No hay deudas con saldo en la cartera del día.'); paymentDialog(); }
    if (action === 'assign') { button.disabled = true; const data = await api('/asignaciones/automaticas', { method: 'POST' }); toast(data.message); await loadData(); await render(); }
    if (action === 'refresh') { await loadData(); await render(); toast('Información actualizada.'); }
    if (action === 'tech-tab') { state.tech = button.dataset.tab; await render(); }
    if (action === 'clone-template') { const data = await api('/plantillas/gestion'); $('#gestion-form [name=observacion]').value = data.pasos.map(p => '• ' + p).join('\n'); toast('Copia independiente del guion lista para editar.'); }
  } catch (error) { toast(error.message, true); button.disabled = false; }
});

document.addEventListener('keydown', event => {
  if (event.key === 'Escape' && $('#sidebar').classList.contains('open')) setMenuOpen(false, true);
});

let searchTimer;
document.addEventListener('input', event => {
  if (event.target.id !== 'table-search') return;
  const position = event.target.selectionStart; state.q = event.target.value; state.page = 1;
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    render().then(() => { const input = $('#table-search'); if (input) { input.focus(); try { input.setSelectionRange(position, position); } catch {} } });
  }, 180);
});
document.addEventListener('change', async event => {
  if (event.target.id === 'tramo-filter') { state.tramo = event.target.value; state.page = 1; await render(); }
  if (event.target.id === 'resultado-select') {
    const result = state.catalogos.resultados.find(r => r.id_tipo_resultado === Number(event.target.value));
    const show = Boolean(result?.genera_promesa); $('#promise-fields').hidden = !show;
    for (const input of $('#promise-fields').querySelectorAll('input')) { input.required = show; if (!show) input.value = ''; }
  }
});

document.addEventListener('submit', async event => {
  const form = event.target; if (!['login-form', 'gestion-form', 'payment-form'].includes(form.id)) return;
  event.preventDefault(); const submit = $('button[type=submit]', form); submit.disabled = true;
  const data = new FormData(form);
  const errorNode = form.id === 'login-form' ? $('#login-error') : form.id === 'gestion-form' ? $('#gestion-error') : $('#payment-error');
  errorNode.textContent = '';
  try {
    if (form.id === 'login-form') {
      await api('/login', { method: 'POST', body: new URLSearchParams(data) });
      form.password.value = ''; await bootstrap();
    } else if (form.id === 'gestion-form') {
      const body = { asignacionId: Number(data.get('asignacionId')), tipoResultadoId: Number(data.get('tipoResultadoId')), telefonoId: data.get('telefonoId') ? Number(data.get('telefonoId')) : null, observacion: data.get('observacion'), montoPromesa: data.get('montoPromesa') ? Number(data.get('montoPromesa')) : null, fechaCompromiso: data.get('fechaCompromiso') || null };
      const result = await api('/gestiones', { method: 'POST', body: JSON.stringify(body) });
      $('#detail-dialog').close(); toast(result.message); await loadData(); await render();
    } else {
      const promise = state.paymentPromise;
      const body = { deudaId: promise?.id_deuda || Number(data.get('deudaId')), promesaId: promise?.id_promesa || null, monto: Number(data.get('monto')), fechaPago: data.get('fechaPago') };
      const result = await api(data.get('core') ? '/core/simular' : '/pagos', { method: 'POST', body: JSON.stringify(body) });
      $('#detail-dialog').close(); toast(result.message); await loadData(); await render();
    }
  } catch (error) { errorNode.textContent = error.message; }
  finally { submit.disabled = false; }
});

$('#detail-dialog').addEventListener('click', event => { if (event.target === $('#detail-dialog')) { const r = event.target.getBoundingClientRect(); if (event.clientX < r.left || event.clientX > r.right || event.clientY < r.top || event.clientY > r.bottom) event.target.close(); } });

}
