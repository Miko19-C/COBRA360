package pe.edu.utp.cobra360.domain.ports;
import java.util.Map;
/** Identidad actual sin acoplar los casos de uso al contexto HTTP o a Spring Security. */
public interface UsuarioSesionPort {
    String usuario();
    Map<String,Object> actual();
}
