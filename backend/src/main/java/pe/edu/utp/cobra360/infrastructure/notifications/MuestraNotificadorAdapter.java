package pe.edu.utp.cobra360.infrastructure.notifications;
import java.util.*;
import org.springframework.stereotype.Component;
import pe.edu.utp.cobra360.domain.ports.MuestraNotificadorPort;
import pe.edu.utp.cobra360.infrastructure.notifications.Notificadores.*;
@Component
public class MuestraNotificadorAdapter implements MuestraNotificadorPort {
    public Map<String,Object> ejemplo(){
        List<String> eventos=new ArrayList<>();
        var notificador=new ConAuditoria(new ConPieInstitucional(new BandejaInterna()),eventos::add);
        String salida=notificador.enviar("Cliente de prueba","Recordatorio de compromiso");
        return Map.of("salida",salida,"eventos",eventos);
    }
}
