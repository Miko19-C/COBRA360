import { state, isSupervisor } from '../core/state.js';
import { api } from '../services/api.js';
import { icon } from '../core/icons.js';
import { esc, money, num, date, initials } from '../core/format.js';
import { $, badge, heading, datePill, exportButton, empty, stat, moneyValue, clientCell } from '../components/ui.js';
import { carteraTable, promisesTable, filtered, filters, pagination, sliced } from '../components/tables.js';
export function paymentsPage() {
  const rows = filtered(state.pagos);
  return heading('Cada pago, un avance.', 'Consulta los abonos y su vínculo con las deudas y promesas.', `<button class="btn primary" data-action="new-payment">${icon('plus')} Registrar pago</button>`) +
    `<section class="panel"><div class="table-toolbar"><div><strong class="section-label">ÚLTIMOS PAGOS REGISTRADOS</strong></div>${filters(false)}</div>${rows.length ? `<div class="table-wrap"><table><thead><tr><th>OPERACIÓN</th><th>CLIENTE</th><th>MONTO ABONADO</th><th>FECHA DE PAGO</th><th>PROMESA</th><th>ORIGEN</th></tr></thead><tbody>${sliced(rows).map(p => `<tr><td>#${p.id_pago}</td><td>${clientCell(p.cliente, p.nro_documento)}<small>${esc(p.producto)}</small></td><td class="money positive">${money(p.monto)}</td><td>${date(p.fecha_pago)}</td><td>${p.id_promesa ? `<span class="badge blue">Promesa #${p.id_promesa}</span>` : '<span class="muted">Pago sin promesa</span>'}</td><td>${p.referencia_externa ? `<span class="badge purple">Core simulado</span>` : '<span class="badge green">Registro local</span>'}</td></tr>`).join('')}</tbody></table></div>` : empty('Todavía no hay pagos', 'Registra un abono para actualizar el saldo de una deuda.')}${pagination(rows.length)}</section><p class="scope-note">Se muestran hasta los 100 pagos más recientes. Los pagos actualizan el saldo dentro de una transacción.</p>`;
}
