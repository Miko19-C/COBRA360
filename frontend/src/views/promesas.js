import { state, isSupervisor } from '../core/state.js';
import { api } from '../services/api.js';
import { icon } from '../core/icons.js';
import { esc, money, num, date, initials } from '../core/format.js';
import { $, badge, heading, datePill, exportButton, empty, stat, moneyValue, clientCell } from '../components/ui.js';
import { carteraTable, promisesTable, filtered, filters, pagination, sliced } from '../components/tables.js';
export function promisesPage() {
  const rows = filtered(state.promesas);
  const vencidas = state.promesas.filter(r => r.estado === 'INCUMPLIDA').length;
  return heading('Compromisos con seguimiento', 'Monto, fecha y cumplimiento. Toda la trazabilidad de cada promesa de pago.', datePill()) +
    (vencidas ? `<div class="alert-banner">${icon('clock')}<span><strong>${vencidas} promesas incumplidas.</strong> Prioriza el recontacto con estos clientes.</span><button class="btn small" data-action="status" data-status="INCUMPLIDA">Ver incumplidas</button></div>` : '') +
    `<section class="panel"><div class="table-toolbar"><div class="tabs">${[['', 'Todas'], ['PENDIENTE', 'Pendientes'], ['PARCIAL', 'Parciales'], ['CUMPLIDA', 'Cumplidas'], ['INCUMPLIDA', 'Incumplidas']].map(([s, text]) => `<button class="tab ${state.status === s ? 'active' : ''}" data-action="status" data-status="${s}">${text}</button>`).join('')}</div>${filters(false)}</div>${promisesTable(sliced(rows))}${pagination(rows.length)}</section>`;
}
