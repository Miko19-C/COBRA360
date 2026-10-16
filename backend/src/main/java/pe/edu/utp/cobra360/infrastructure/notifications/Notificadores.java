package pe.edu.utp.cobra360.infrastructure.notifications;

import java.util.function.Consumer;
import pe.edu.utp.cobra360.domain.ports.Notificador;
/** DECORATOR: objetos intercambiables que añaden comportamiento sin modificar la base. */
public final class Notificadores {
    private Notificadores(){}
    public static final class BandejaInterna implements Notificador {
        public String enviar(String destinatario,String mensaje){return destinatario+": "+mensaje;}
    }
    public abstract static class Decorador implements Notificador {
        protected final Notificador envuelto;
        protected Decorador(Notificador envuelto){this.envuelto=envuelto;}
    }
    public static final class ConPieInstitucional extends Decorador {
        public ConPieInstitucional(Notificador envuelto){super(envuelto);}
        public String enviar(String destinatario,String mensaje){
            return envuelto.enviar(destinatario,mensaje+" — COBRA360 · Seguimiento de compromiso");
        }
    }
    public static final class ConAuditoria extends Decorador {
        private final Consumer<String> registrar;
        public ConAuditoria(Notificador envuelto,Consumer<String> registrar){super(envuelto);this.registrar=registrar;}
        public String enviar(String destinatario,String mensaje){
            String resultado=envuelto.enviar(destinatario,mensaje);registrar.accept(resultado);return resultado;
        }
    }
}
