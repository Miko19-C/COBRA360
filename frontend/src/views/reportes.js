import { state, isSupervisor } from '../core/state.js';
import { api } from '../services/api.js';
import { icon } from '../core/icons.js';
import { esc, money, num, date, initials } from '../core/format.js';
import { $, badge, heading, datePill, exportButton, empty, stat, moneyValue, clientCell } from '../components/ui.js';
import { carteraTable, promisesTable, filtered, filters, pagination, sliced } from '../components/tables.js';
export async function reportsPage() {
  const data = await api('/reportes'); const s = data.indicadores;
  return heading('La gestión, en perspectiva.', 'Indicadores operativos calculados desde los registros del sistema.', exportButton()) +
    `<div class="report-layout"><section class="panel"><div class="panel-header"><div><h2>Cartera por tramo de mora</h2><p>Saldo agregado por estructura jerárquica</p></div><span class="badge blue">Composite</span></div><div class="report-body">${data.arbol.hijos.map(g => `<div class="tree-group"><div class="tree-title"><div><strong>${esc(g.nombre)}</strong><small>${g.cantidad} deudas en cartera</small></div><strong>${money(g.saldo)}</strong></div><div class="tree-bar"><span style="width:${data.arbol.saldo ? Number(g.saldo) / Number(data.arbol.saldo) * 100 : 0}%"></span></div></div>`).join('')}<div class="report-metric"><span>Saldo total del grupo</span><strong>${money(data.arbol.saldo)}</strong></div></div></section><section class="panel"><div class="panel-header"><div><h2>${esc(data.reporte.titulo)}</h2><p>Periodo: ${date(data.reporte.periodo)}</p></div><span class="badge purple">Builder</span></div><div class="report-body">${[['Deudas gestionadas', `${s.gestionadas} / ${s.asignadas}`], ['Promesas registradas', s.promesas], ['Promesas cumplidas', s.cumplidas], ['Promesas incumplidas', s.incumplidas], ['Recuperado hoy', money(s.recuperadoHoy)]].map(([a, b]) => `<div class="report-metric"><span>${a}</span><strong>${b}</strong></div>`).join('')}<p class="scope-note">Secciones incluidas: ${data.reporte.secciones.map(esc).join(' · ')}.</p></div></section></div>`;
}
