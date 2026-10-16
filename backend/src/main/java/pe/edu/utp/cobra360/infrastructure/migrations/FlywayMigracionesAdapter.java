package pe.edu.utp.cobra360.infrastructure.migrations;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.flywaydb.core.Flyway;
import pe.edu.utp.cobra360.domain.ports.MigracionesPort;
@Component @RequiredArgsConstructor
public class FlywayMigracionesAdapter implements MigracionesPort {
    private final Flyway flyway;
    public List<Map<String,Object>> listar(){
        return Arrays.stream(flyway.info().all()).map(m->{
            Map<String,Object> item=new LinkedHashMap<>();
            item.put("version",m.getVersion().toString());item.put("descripcion",m.getDescription());
            item.put("estado",m.getState().getDisplayName());item.put("script",m.getScript());return item;
        }).toList();
    }
}
