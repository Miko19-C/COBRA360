package pe.edu.utp.cobra360.application.tecnico;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.cobra360.domain.ports.*;
@Service @RequiredArgsConstructor
public class VerificacionTecnicaService {
    private final RegistroAuditoriaPort auditoria;
    private final MigracionesPort migraciones;
    public List<Map<String,Object>> auditoria(){return auditoria.ultimos();}
    public List<Map<String,Object>> migraciones(){return migraciones.listar();}
}
