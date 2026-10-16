package pe.edu.utp.cobra360.application.shared.audit;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;

import java.time.Clock;
import java.time.LocalDateTime;
import pe.edu.utp.cobra360.domain.ports.RegistroAuditoriaPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @RequiredArgsConstructor @Slf4j
public class AuditoriaService {
    private final RegistroAuditoriaPort registros;
    private final AccesoService acceso;
    private final Clock clock;
    public void registrar(String accion,String detalle){
        registros.guardar(
            acceso.usuario(),accion,detalle.length()>1000?detalle.substring(0,1000):detalle,LocalDateTime.now(clock));
        log.info("accion={} usuario={}",accion,acceso.usuario());
    }
}
