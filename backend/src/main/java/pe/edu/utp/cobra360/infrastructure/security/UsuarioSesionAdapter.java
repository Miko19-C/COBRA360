package pe.edu.utp.cobra360.infrastructure.security;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import pe.edu.utp.cobra360.domain.ports.UsuarioSesionPort;
@Component @RequiredArgsConstructor
public class UsuarioSesionAdapter implements UsuarioSesionPort {
    private final JdbcTemplate jdbc;
    public String usuario(){return SecurityContextHolder.getContext().getAuthentication().getName();}
    public Map<String,Object> actual(){
        return jdbc.queryForMap("select usuario,nombre,rol,id_asesor as asesorId from usuario where usuario=?",usuario());
    }
}
