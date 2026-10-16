package pe.edu.utp.cobra360.web.tecnico;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.tecnico.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class TecnicoController {
    private final PatronesService patrones;
    private final VerificacionTecnicaService verificacion;
    @GetMapping("/patrones") public Object patrones(){return patrones.evidencias();}
    @GetMapping("/auditoria") public Object auditoria(){return verificacion.auditoria();}
    @GetMapping("/migraciones") public Object migraciones(){return verificacion.migraciones();}
}
