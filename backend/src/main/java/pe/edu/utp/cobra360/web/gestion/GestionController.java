package pe.edu.utp.cobra360.web.gestion;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.gestion.*;
import pe.edu.utp.cobra360.application.command.Requests.RegistrarGestion;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class GestionController {
    private final CobranzaFacade facade;
    private final PlantillaGestionService plantillas;
    @PostMapping("/gestiones") public Object registrar(@Valid @RequestBody RegistrarGestion req){return facade.completarGestion(req);}
    @GetMapping("/plantillas/gestion") public Object plantilla(){return plantillas.copiar();}
}
