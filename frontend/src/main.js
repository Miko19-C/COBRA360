import { bootstrap } from './controllers/session.js';
import { render, navigate } from './controllers/router.js';
import { bindEvents } from './controllers/events.js';
import { toast } from './components/ui.js';

const start = () => bootstrap(render);
bindEvents({ bootstrap: start, render, navigate });
document.addEventListener('session:expired', () => {
  toast('Tu sesión finalizó. Ingresa nuevamente para continuar.', true);
  start();
});
start();
