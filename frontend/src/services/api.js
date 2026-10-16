import { state } from '../core/state.js';
export async function api(url, options = {}) {
  const headers = { ...(options.headers || {}) };
  if (options.body && !(options.body instanceof URLSearchParams)) headers['Content-Type'] = 'application/json';
  if (options.method && options.method !== 'GET') headers[state.session?.csrfHeader || 'X-CSRF-TOKEN'] = state.session?.csrf || '';
  const response = await fetch('/api' + url, { ...options, headers, credentials: 'same-origin' });
  const data = response.status === 204 ? null : await response.json().catch(() => ({}));
  if (!response.ok) {
    if (response.status === 403 && data?.code === 'CSRF_INVALID') {
      state.session = await api('/session');
      if (!state.session.authenticated) { document.dispatchEvent(new CustomEvent('session:expired'));  }
      throw new Error('Se actualizó la sesión de seguridad. Revisa los datos y vuelve a confirmar.');
    }
    if (response.status === 401 && url !== '/login') { document.dispatchEvent(new CustomEvent('session:expired'));  }
    throw new Error(data?.message || `No se pudo completar la solicitud (${response.status}).`);
  }
  return data;
}
