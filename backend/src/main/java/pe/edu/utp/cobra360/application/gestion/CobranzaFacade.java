package pe.edu.utp.cobra360.application.gestion;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;
import pe.edu.utp.cobra360.application.gestion.RegistroGestionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import pe.edu.utp.cobra360.application.command.Requests.RegistrarGestion;

/** FACADE: una operación pública orquesta registro, promesa, teléfono, notificación y auditoría. */
@Service @RequiredArgsConstructor
public class CobranzaFacade {
    private final RegistroGestionService registro;
    private final AuditoriaService auditoria;
    @Transactional
    public Map<String,Object> completarGestion(RegistrarGestion req){
        var respuesta=registro.registrar(req);
        auditoria.registrar("Facade · Completar gestión","Gestión #"+respuesta.get("gestionId")+" confirmada en una transacción.");
        return respuesta;
    }
}
