package pe.edu.utp.cobra360.web.pagos;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.pagos.*;
import pe.edu.utp.cobra360.application.cartera.CarteraQueryService;
import pe.edu.utp.cobra360.application.command.Requests.RegistrarPago;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class PagoController {
    private final RegistroPagoService registro;
    private final ConciliacionPagoService conciliacion;
    private final CarteraQueryService consultas;
    @GetMapping("/pagos") public Object listar(){return consultas.pagos();}
    @PostMapping("/pagos") public Object registrar(@Valid @RequestBody RegistrarPago req){
        var pago=registro.registrarPago(req,null);
        return Map.of("id",pago.getId(),"message","Pago registrado y saldo actualizado.");
    }
    @PostMapping("/core/simular") public Object simular(@Valid @RequestBody RegistrarPago req){return conciliacion.simular(req);}
}
