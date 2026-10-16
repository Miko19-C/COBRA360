import { state } from '../core/state.js';
import { esc, money, num, date } from '../core/format.js';
import { icon } from '../core/icons.js';
import { badge, empty, clientCell } from './ui.js';
export function carteraTable(rows) {
  if (!rows.length) return empty('No hay deudas para mostrar', 'Prueba otros filtros o solicita la asignación de cartera.');
  return `<div class="table-wrap"><table><thead><tr><th>CLIENTE</th><th>PRODUCTO</th><th>SALDO PENDIENTE</th><th>TRAMO DE MORA</th><th>DÍAS DE ATRASO</th><th>ESTADO</th><th></th></tr></thead><tbody>${rows.map(r => `<tr><td>${clientCell(r.cliente, r.nro_documento)}</td><td>${esc(r.producto)}</td><td class="money">${money(r.saldo_actual)}</td><td>${badge(r.tramo_mora)}</td><td>${num(r.dias_atraso)} días</td><td>${badge(r.estado)}</td><td><button class="table-action" data-action="detail" data-id="${r.id_asignacion}">Gestionar ${icon('arrow')}</button></td></tr>`).join('')}</tbody></table></div>`;
}
export function promisesTable(rows, compact = false) {
  if (!rows.length) return empty('Sin promesas en esta selección', 'Los compromisos registrados aparecerán aquí.');
  return `<div class="table-wrap"><table><thead><tr><th>CLIENTE</th><th>COMPROMISO</th><th>FECHA ACORDADA</th><th>PAGO REGISTRADO</th><th>ESTADO</th><th></th></tr></thead><tbody>${rows.map(r => `<tr><td>${clientCell(r.cliente, r.nro_documento)}</td><td class="money">${money(r.monto_comprometido)}</td><td>${date(r.fecha_compromiso)}${String(r.fecha_compromiso).slice(0, 10) === state.catalogos.hoy ? '<span class="badge orange">Hoy</span>' : ''}</td><td><span class="money">${money(r.pagado)}</span>${!compact ? `<div class="progress-small"><span style="width:${Math.min(100, Number(r.pagado) / Number(r.monto_comprometido) * 100)}%"></span></div>` : ''}</td><td>${badge(r.estado)}</td><td>${r.estado !== 'CUMPLIDA' && Number(r.saldo_actual) > 0 && r.puede_registrar_pago !== false ? `<button class="table-action" data-action="pay-promise" data-id="${r.id_promesa}">Registrar pago ${icon('arrow')}</button>` : (r.estado === 'CUMPLIDA' ? `<span class="positive">${icon('check')} Completa</span>` : `<span class="muted">${Number(r.saldo_actual) <= 0 ? 'Deuda sin saldo' : 'Sin asignación vigente'}</span>`)}</td></tr>`).join('')}</tbody></table></div>`;
}
export function filtered(rows, nameKey = 'cliente') {
  const q = state.q.toLocaleLowerCase('es');
  return rows.filter(r => (!q || `${r[nameKey] || ''} ${r.nro_documento || ''} ${r.producto || ''}`.toLocaleLowerCase('es').includes(q)) && (!state.tramo || r.tramo_mora === state.tramo) && (!state.status || r.estado === state.status));
}
export function filters(includeTramo = true) {
  return `<div class="filter-group"><div class="search-box">${icon('search')}<input id="table-search" type="search" placeholder="Buscar cliente o documento…" aria-label="Buscar cliente o documento" value="${esc(state.q)}"></div>${includeTramo ? `<select id="tramo-filter" aria-label="Filtrar por tramo"><option value="">Todos los tramos</option>${['TEMPRANA', 'TARDIA', 'CASTIGO'].map(t => `<option value="${t}" ${state.tramo === t ? 'selected' : ''}>${{ TEMPRANA: 'Temprana', TARDIA: 'Tardía', CASTIGO: 'Castigo' }[t]}</option>`).join('')}</select>` : ''}</div>`;
}
export function pagination(total) {
  const pages = Math.max(1, Math.ceil(total / 8)); state.page = Math.min(state.page, pages);
  return `<div class="pagination"><span>${total ? (state.page - 1) * 8 + 1 : 0}–${Math.min(state.page * 8, total)} de ${total} registros</span><div class="page-buttons"><button class="page-button" data-action="page" data-page="${Math.max(1, state.page - 1)}" aria-label="Página anterior">‹</button>${Array.from({ length: pages }, (_, i) => `<button class="page-button ${state.page === i + 1 ? 'current' : ''}" data-action="page" data-page="${i + 1}" aria-label="Página ${i + 1}">${i + 1}</button>`).join('')}<button class="page-button" data-action="page" data-page="${Math.min(pages, state.page + 1)}" aria-label="Página siguiente">›</button></div></div>`;
}
export function sliced(rows) { const pages = Math.max(1, Math.ceil(rows.length / 8)); state.page = Math.min(state.page, pages); return rows.slice((state.page - 1) * 8, state.page * 8); }
