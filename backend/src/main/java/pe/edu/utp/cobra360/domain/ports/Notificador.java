package pe.edu.utp.cobra360.domain.ports;
/** Componente común de los decoradores. La implementación local registra en bandeja interna. */
public interface Notificador {
    String enviar(String destinatario, String mensaje);
}
