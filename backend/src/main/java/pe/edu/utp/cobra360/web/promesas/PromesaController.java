package pe.edu.utp.cobra360.web.promesas;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.cartera.CarteraQueryService;
@RestController @RequestMapping("/api/promesas") @RequiredArgsConstructor
public class PromesaController {
    private final CarteraQueryService consultas;
    @GetMapping public Object listar(){return consultas.promesas();}
}
