package pe.edu.utp.cobra360.domain.ports;
import java.util.List;
import java.util.Map;
public interface MigracionesPort {
    List<Map<String,Object>> listar();
}
