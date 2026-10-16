package pe.edu.utp.cobra360.infrastructure.scheduling;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.promesas.SeguimientoPromesaService;
@Component @RequiredArgsConstructor
public class PromesasVencidasJob {
    private final SeguimientoPromesaService seguimiento;
    @Scheduled(cron="0 5 0 * * *",zone="America/Lima")
    public void ejecutar(){seguimiento.actualizarVencidas();}
}
