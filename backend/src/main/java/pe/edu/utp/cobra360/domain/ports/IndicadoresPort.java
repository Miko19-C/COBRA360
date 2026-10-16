package pe.edu.utp.cobra360.domain.ports;
import java.time.LocalDate;
import java.math.BigDecimal;
public interface IndicadoresPort {
    BigDecimal recuperado(LocalDate fecha,boolean supervisor,Long asesorId,LocalDate fechaCartera);
}
