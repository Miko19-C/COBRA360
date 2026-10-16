export const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
export const money = value => new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(Number(value || 0));
export const num = value => new Intl.NumberFormat('es-PE', { maximumFractionDigits: 0 }).format(Number(value || 0));
export const date = value => value ? new Date(String(value).slice(0, 10) + 'T12:00:00').toLocaleDateString('es-PE', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';
export const initials = value => String(value || '').split(' ').filter(Boolean).slice(0, 2).map(x => x[0]).join('');
