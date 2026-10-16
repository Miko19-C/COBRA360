package pe.edu.utp.cobra360.infrastructure.audit;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.cobra360.domain.ports.RegistroAuditoriaPort;
@Repository @RequiredArgsConstructor
public class JdbcAuditoriaAdapter implements RegistroAuditoriaPort {
    private final JdbcTemplate jdbc;
    public void guardar(String usuario,String accion,String detalle,LocalDateTime fecha){
        jdbc.update("insert into auditoria(usuario,accion,detalle,fecha) values(?,?,?,?)",usuario,accion,detalle,fecha);
    }
    public List<Map<String,Object>> ultimos(){return jdbc.queryForList("select * from auditoria order by id desc limit 100");}
}
