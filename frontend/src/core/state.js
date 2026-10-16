export const state = { session: null, view: 'resumen', cartera: [], promesas: [], pagos: [], resumen: {}, catalogos: {}, q: '', tramo: '', status: '', page: 1, tech: 'patrones' };
export function resetWorkspace() {
  Object.assign(state, { view: 'resumen', cartera: [], promesas: [], pagos: [], resumen: {}, catalogos: {}, q: '', tramo: '', status: '', page: 1, tech: 'patrones', detail: null, paymentPromise: null });
}
export const isSupervisor = () => state.session?.user?.rol === 'SUPERVISOR';
export const labels = { resumen: 'Resumen', cartera: 'Mi cartera', promesas: 'Promesas de pago', pagos: 'Pagos registrados', reportes: 'Reportes', tecnico: 'Centro técnico' };
