package pe.edu.utp.cobra360.domain.ports;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
public interface RegistroAuditoriaPort {
    void guardar(String usuario,String accion,String detalle,LocalDateTime fecha);
    List<Map<String,Object>> ultimos();
}
