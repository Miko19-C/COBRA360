package pe.edu.utp.cobra360.web.cartera;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.cartera.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class CarteraController {
    private final CarteraQueryService consultas;
    private final AsignacionService asignaciones;
    @GetMapping("/cartera") public Object cartera(){return consultas.cartera();}
    @GetMapping("/cartera/{id}") public Object detalle(@PathVariable Long id){return consultas.detalle(id);}
    @GetMapping("/catalogos") public Object catalogos(){return consultas.catalogos();}
    @PostMapping("/asignaciones/automaticas") public Object asignar(){return asignaciones.asignar();}
}
