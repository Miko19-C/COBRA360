package pe.edu.utp.cobra360.infrastructure.notifications;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.utp.cobra360.domain.ports.Notificador;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;
import pe.edu.utp.cobra360.infrastructure.notifications.Notificadores.*;
/** Raíz de composición de los decoradores: la aplicación recibe solo Notificador. */
@Configuration
public class NotificacionConfig {
    @Bean public Notificador notificador(AuditoriaService auditoria){
        return new ConAuditoria(new ConPieInstitucional(new BandejaInterna()),
            texto->auditoria.registrar("Decorator · Notificación interna",texto));
    }
}
