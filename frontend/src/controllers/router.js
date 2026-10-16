import { state, isSupervisor, labels } from '../core/state.js';
import { $ ,toast, setMenuOpen } from '../components/ui.js';
import { icon } from '../core/icons.js';
import { dashboard } from '../views/dashboard.js';
import { carteraPage } from '../views/cartera.js';
import { promisesPage } from '../views/promesas.js';
import { paymentsPage } from '../views/pagos.js';
import { reportsPage } from '../views/reportes.js';
import { technicalPage } from '../views/tecnico.js';
function renderNavigation() {
  const items = [['resumen', 'grid'], ['cartera', 'briefcase'], ['promesas', 'calendar'], ['pagos', 'wallet'], ['reportes', 'chart']];
  if (isSupervisor()) items.push(['tecnico', 'code']);
  $('#navigation').innerHTML = items.map(([key, glyph]) => `${key === 'tecnico' ? '<div class="nav-separator"></div><span class="nav-caption">PROYECTO Y EVIDENCIAS</span>' : ''}<button class="nav-item ${state.view === key ? 'active' : ''}" data-view="${key}" ${state.view === key ? 'aria-current="page"' : ''}>${icon(glyph)}<span>${labels[key]}</span>${key === 'cartera' ? `<span class="nav-count">${state.cartera.length}</span>` : ''}</button>`).join('');
  $('#breadcrumb').textContent = labels[state.view];
}
let renderVersion = 0;
export async function render() {
  const version = ++renderVersion; const session = state.session; renderNavigation();
  try {
    let html;
    if (state.view === 'resumen') html = dashboard();
    else if (state.view === 'cartera') html = carteraPage();
    else if (state.view === 'promesas') html = promisesPage();
    else if (state.view === 'pagos') html = paymentsPage();
    else if (state.view === 'reportes') html = await reportsPage();
    else html = await technicalPage();
    if (version === renderVersion && session === state.session && session?.authenticated) $('#main-content').innerHTML = html;
  } catch (error) { toast(error.message, true); }
}
export async function navigate(view) {
  state.view = view; state.q = ''; state.tramo = ''; state.status = ''; state.page = 1;
  setMenuOpen(false); await render(); window.scrollTo({ top: 0 });
}
