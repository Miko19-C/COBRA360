package pe.edu.utp.cobra360.application.shared.security;

import org.springframework.stereotype.Service;
import pe.edu.utp.cobra360.domain.ports.UsuarioSesionPort;

import pe.edu.utp.cobra360.domain.AccesoDenegadoException;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class AccesoService {
    private final UsuarioSesionPort sesion;
    public String usuario(){return sesion.usuario();}
    public Map<String,Object> actual(){
        return sesion.actual();
    }
    public boolean supervisor(){return "SUPERVISOR".equals(actual().get("rol"));}
    public Long asesorId(){Object id=actual().get("asesorId");return id==null?null:((Number)id).longValue();}
    public void verificarAsesor(Long id){
        if(!supervisor() && !id.equals(asesorId()))throw new AccesoDenegadoException("Esta cartera pertenece a otro asesor.");
    }
}
