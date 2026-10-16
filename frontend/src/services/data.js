import { state } from '../core/state.js';
import { api } from './api.js';
export async function loadData() {
  const session = state.session;
  const [cartera, promesas, pagos, resumen, catalogos] = await Promise.all(['/cartera', '/promesas', '/pagos', '/resumen', '/catalogos'].map(url => api(url)));
  if (state.session !== session || !session?.authenticated) return;
  Object.assign(state, { cartera, promesas, pagos, resumen, catalogos });
}
