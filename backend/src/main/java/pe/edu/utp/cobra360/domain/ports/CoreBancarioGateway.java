package pe.edu.utp.cobra360.domain.ports;
import java.math.BigDecimal;
import java.time.LocalDate;
public interface CoreBancarioGateway {
    record PagoExterno(String referencia, Long idDeuda, BigDecimal monto, LocalDate fecha) {}
    PagoExterno consultarPago(Long idDeuda, BigDecimal monto, LocalDate fecha);
}
