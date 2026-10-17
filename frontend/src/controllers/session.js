import { state, isSupervisor, resetWorkspace } from '../core/state.js';
import { api } from '../services/api.js';
import { loadData } from '../services/data.js';
import { $, setMenuOpen } from '../components/ui.js';
import { initials } from '../core/format.js';
import { icon } from '../core/icons.js';
let bootstrapVersion = 0;
export async function bootstrap(render) {
  const version = ++bootstrapVersion;
  try {
    const previousUser = state.session?.user?.usuario;
    const session = await api('/session');
    if (version !== bootstrapVersion) return;
    if (!session.authenticated || previousUser !== session.user?.usuario) {
      resetWorkspace();
      setMenuOpen(false);
      $('#main-content').innerHTML = '';
      $('#dialog-content').innerHTML = '';
      if ($('#detail-dialog').open) $('#detail-dialog').close();
    }
    state.session = session;
    $('.demo-access').hidden = !['localhost', '127.0.0.1', '[::1]'].includes(window.location.hostname);
    $('#startup-error').hidden = true;
    $('#workspace').hidden = !state.session.authenticated;
    $('#login-screen').hidden = state.session.authenticated;
    if (!state.session.authenticated) { if ($('#detail-dialog').open) $('#detail-dialog').close(); return; }
    const user = state.session.user;
    $('#user-name').textContent = user.nombre; $('#user-role').textContent = isSupervisor() ? 'Supervisora de cartera' : 'Asesor de cobranzas';
    $('#user-avatar').textContent = initials(user.nombre); $('#bell-icon').innerHTML = icon('bell');
    $('#today-label').textContent = new Date().toLocaleDateString('es-PE', { timeZone: 'America/Lima', weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
    if (!isSupervisor() && state.view === 'tecnico') state.view = 'resumen';
    await loadData();
    if (version === bootstrapVersion) await render();
  } catch (error) { if (version !== bootstrapVersion) return; $('#startup-error').hidden = false; $('#workspace').hidden = true; $('#login-screen').hidden = true; console.error(error); }
}
