package pe.edu.utp.cobra360.application.cartera;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.cobra360.domain.ports.ConsultasCarteraPort;
import pe.edu.utp.cobra360.domain.patterns.FamiliaCobranza;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CarteraQueryService {
    private final ConsultasCarteraPort consultas;
    private final AccesoService acceso;
    private final Clock clock;
    public List<Map<String,Object>> cartera(){return consultas.cartera(acceso.supervisor(),acceso.asesorId(),LocalDate.now(clock));}
    public List<Map<String,Object>> promesas(){return consultas.promesas(acceso.supervisor(),acceso.asesorId(),LocalDate.now(clock));}
    public List<Map<String,Object>> pagos(){return consultas.pagos(acceso.supervisor(),acceso.asesorId(),LocalDate.now(clock));}
    public Map<String,Object> catalogos(){return consultas.catalogos(acceso.supervisor(),LocalDate.now(clock));}
    public Map<String,Object> detalle(Long id){
        var dato=consultas.detalle(id);
        acceso.verificarAsesor(((Number)dato.get("id_asesor")).longValue());
        dato.put("fechaMaximaCompromiso",LocalDate.now(clock).plusDays(pe.edu.utp.cobra360.domain.patterns.ConfiguracionCobranza.INSTANCE.maxDiasCompromiso()));
        dato.put("guion",FamiliaCobranza.paraTramo((String)dato.get("tramo_mora")).crearMensaje().texto());
        return dato;
    }
}
