import { test, afterEach } from 'node:test';
import assert from 'node:assert/strict';
import { state, resetWorkspace } from '../src/core/state.js';
import { promisesTable } from '../src/components/tables.js';
import { detail } from '../src/components/dialogs.js';
import { filtered } from '../src/components/tables.js';
import { loadData } from '../src/services/data.js';
const originalFetch = globalThis.fetch;
afterEach(() => { globalThis.fetch = originalFetch; resetWorkspace(); state.session = null; });
const promise = { id_promesa: 1, id_deuda: 1, cliente: 'Cliente', monto_comprometido: 100, pagado: 20, saldo_actual: 80, estado: 'PARCIAL', fecha_compromiso: '2026-10-16', puede_registrar_pago: true };
test('deuda cancelada sin pago vinculado no se anuncia como promesa completa', () => {
  const html = promisesTable([{ ...promise, saldo_actual: 0 }]);
  assert.match(html, /Deuda sin saldo/);
  assert.doesNotMatch(html, /Completa|data-action="pay-promise"/);
});
test('una promesa cumplida sí se muestra completa', () => {
  const html = promisesTable([{ ...promise, estado: 'CUMPLIDA', pagado: 100, saldo_actual: 0 }]);
  assert.match(html, /Completa/);
  assert.doesNotMatch(html, /data-action="pay-promise"/);
});
test('sin asignación vigente no se ofrece un pago que el backend rechazará', () => {
  const html = promisesTable([{ ...promise, puede_registrar_pago: false }]);
  assert.match(html, /Sin asignación vigente/);
  assert.doesNotMatch(html, /data-action="pay-promise"|Completa/);
});
test('con saldo y autorización el botón permite registrar pago', () => {
  assert.match(promisesTable([promise]), /data-action="pay-promise"/);
});
test('cambiar de usuario elimina los datos y filtros del usuario previo', () => {
  Object.assign(state, { cartera: [{ cliente: 'Privado' }], promesas: [promise], pagos: [{}], detail: {}, paymentPromise: promise, q: 'Privado', page: 4, view: 'tecnico' });
  resetWorkspace();
  assert.deepEqual(state.cartera, []); assert.deepEqual(state.promesas, []); assert.deepEqual(state.pagos, []);
  assert.equal(state.detail, null); assert.equal(state.paymentPromise, null);
  assert.equal(state.q, ''); assert.equal(state.page, 1); assert.equal(state.view, 'resumen');
});
test('respuestas pendientes de una sesión anterior no restauran su cartera', async () => {
  state.session = { authenticated: true, user: { usuario: 'supervisor' } };
  let release;
  const waiting = new Promise(resolve => { release = resolve; });
  globalThis.fetch = async () => { await waiting; return { ok: true, status: 200, json: async () => [{ cliente: 'Usuario anterior' }] }; };
  const pending = loadData();
  state.session = { authenticated: true, user: { usuario: 'mirko' } };
  resetWorkspace(); release(); await pending;
  assert.deepEqual(state.cartera, []);
});
test('carga normal mantiene los datos de la sesión vigente', async () => {
  state.session = { authenticated: true };
  globalThis.fetch = async () => ({ ok: true, status: 200, json: async () => [{ cliente: 'Vigente' }] });
  await loadData(); assert.equal(state.cartera[0].cliente, 'Vigente');
});

test('detalle pendiente no abre datos del usuario anterior tras cambiar la sesión', async () => {
  state.session = { authenticated: true, user: { usuario: 'supervisor' } };
  let release;
  const waiting = new Promise(resolve => { release = resolve; });
  globalThis.fetch = async () => { await waiting; return { ok: true, status: 200, json: async () => ({ nombre: 'Usuario anterior' }) }; };
  const pending = detail(1);
  state.session = { authenticated: false }; resetWorkspace(); release(); await pending;
  assert.equal(state.detail, null);
});
test('búsqueda de pagos reconoce el documento devuelto por la API', () => {
  state.q = '90000001';
  assert.equal(filtered([{ cliente: 'Valeria Rojas', nro_documento: '90000001' }]).length, 1);
  assert.equal(filtered([{ cliente: 'Otro', nro_documento: '90000002' }]).length, 0);
});
