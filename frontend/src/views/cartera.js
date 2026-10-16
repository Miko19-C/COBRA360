import { state, isSupervisor } from '../core/state.js';
import { api } from '../services/api.js';
import { icon } from '../core/icons.js';
import { esc, money, num, date, initials } from '../core/format.js';
import { $, badge, heading, datePill, exportButton, empty, stat, moneyValue, clientCell } from '../components/ui.js';
import { carteraTable, promisesTable, filtered, filters, pagination, sliced } from '../components/tables.js';
export function carteraPage() {
  const rows = filtered(state.cartera);
  return heading('Mi cartera del día', 'Cada contacto es una oportunidad para avanzar. Elige una deuda para gestionarla.', exportButton() + (isSupervisor() ? `<button class="btn primary" data-action="assign">${icon('refresh')} Asignar cartera</button>` : '')) +
    `<section class="panel"><div class="table-toolbar"><div class="tabs">${[['', 'Todas'], ['PENDIENTE', 'Pendientes'], ['GESTIONADA', 'Gestionadas']].map(([s, text]) => `<button class="tab ${state.status === s ? 'active' : ''}" data-action="status" data-status="${s}">${text}</button>`).join('')}</div>${filters()}</div>${carteraTable(sliced(rows))}${pagination(rows.length)}</section><p class="scope-note">${isSupervisor() ? 'Vista de supervisión: cartera de todos los asesores.' : 'Solo se muestran las deudas que te asignaron para hoy.'} Las gestiones se guardan con fecha y usuario.</p>`;
}
