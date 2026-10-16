package pe.edu.utp.cobra360.application.gestion;
import java.util.*;
import org.springframework.stereotype.Service;
import pe.edu.utp.cobra360.domain.patterns.PlantillaGestion;
@Service
public class PlantillaGestionService {
    public Map<String,Object> copiar(){
        var original=new PlantillaGestion("Guion base",List.of("Validar identidad del titular","Revisar la obligación pendiente","Registrar el resultado del contacto"));
        var copia=original.clone();copia.getPasos().add("Confirmar monto y fecha si acepta pagar");
        return Map.of("nombre",copia.getNombre(),"pasos",copia.getPasos(),"originalSinCambios",original.getPasos().size()==3);
    }
}
