package pe.edu.utp.cobra360.application.promesas;
import java.time.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.cobra360.domain.ports.ConsultaSeguimientoPort;
/** Caso de uso de seguimiento; la planificación temporal es responsabilidad de infraestructura. */
@Service @RequiredArgsConstructor
public class SeguimientoPromesaService {
    private final ConsultaSeguimientoPort consultas;
    private final Clock clock;
    @Transactional public void actualizarVencidas(){consultas.marcarVencidas(LocalDate.now(clock));}
}
