package pe.edu.utp.cobra360.domain.ports;
import java.time.LocalDate;
public interface ConsultaSeguimientoPort {
    boolean deudaAsignada(Long deuda,Long asesor,LocalDate fecha);
    boolean referenciaRegistrada(String referencia);
    Long deudaDeGestion(Long gestion);
    LocalDate fechaDeGestion(Long gestion);
    void marcarVencidas(LocalDate hoy);
}
