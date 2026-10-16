import { state } from '../core/state.js';
import { esc, date, initials } from '../core/format.js';
import { icon } from '../core/icons.js';
export const $ = (s,root=document)=>root.querySelector(s);
export function toast(message, error = false) {
  const node = document.createElement('div'); node.className = 'toast' + (error ? ' error' : ''); node.textContent = message;
  $('#toast-region').append(node); setTimeout(() => node.remove(), error ? 8500 : 5000);
}
export function heading(title, subtitle, actions = '') {
  return `<div class="page-heading"><div><h1>${title}</h1><p>${subtitle}</p></div><div class="heading-actions">${actions}</div></div>`;
}
export const datePill = () => `<span class="date-pill">${icon('calendar')}${date(state.catalogos.hoy)}</span>`;
export const exportButton = () => `<a class="btn" href="/api/reportes/cartera.csv" download>${icon('download')} Exportar cartera</a>`;
export const empty = (title, text) => `<div class="empty-state">${icon('briefcase')}<strong>${esc(title)}</strong>${esc(text)}</div>`;
export function stat(title, value, glyph, footer) {
  return `<article class="stat-card"><div class="stat-top"><span>${title}</span><span class="stat-icon">${icon(glyph)}</span></div><div class="stat-value">${value}</div><div class="stat-foot">${footer}</div></article>`;
}
export function moneyValue(value) { return `<span class="currency">S/</span>${new Intl.NumberFormat('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(Number(value || 0))}`; }
export function clientCell(name, document = '') { return `<div class="client-cell"><span class="client-avatar">${esc(initials(name))}</span><div><strong>${esc(name)}</strong><small>${document ? 'DNI ' + esc(document) : 'Cliente de cartera'}</small></div></div>`; }
export const badge = value => {
  const map = { PENDIENTE: ['orange', 'Pendiente'], GESTIONADA: ['green', 'Gestionada'], CUMPLIDA: ['green', 'Cumplida'], PARCIAL: ['blue', 'Parcial'], INCUMPLIDA: ['red', 'Incumplida'], TEMPRANA: ['blue', 'Temprana'], TARDIA: ['orange', 'Tardía'], CASTIGO: ['purple', 'Castigo'] };
  const [color, text] = map[value] || ['', value];
  return `<span class="badge ${color}">${esc(text)}</span>`;
};

export function setMenuOpen(open, restoreFocus = false) {
  $('#sidebar').classList.toggle('open', open);
  const toggle = $('[data-action="menu"]');
  toggle.setAttribute('aria-expanded', String(open));
  toggle.setAttribute('aria-label', open ? 'Cerrar menú de navegación' : 'Abrir menú');
  if (!open && restoreFocus) toggle.focus();
}
