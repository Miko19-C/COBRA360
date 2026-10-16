package pe.edu.utp.cobra360.domain.ports;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/** Proyecciones de lectura separadas de las entidades de escritura. */
public interface ConsultasCarteraPort {
    List<Map<String,Object>> cartera(boolean supervisor,Long asesorId,LocalDate hoy);
    Map<String,Object> detalle(Long id);
    List<Map<String,Object>> promesas(boolean supervisor,Long asesorId,LocalDate hoy);
    List<Map<String,Object>> pagos(boolean supervisor,Long asesorId,LocalDate hoy);
    Map<String,Object> catalogos(boolean supervisor,LocalDate hoy);
}
