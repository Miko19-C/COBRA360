package pe.edu.utp.cobra360.application.pagos;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.cobra360.application.command.Requests.RegistrarPago;
import pe.edu.utp.cobra360.domain.ports.CoreBancarioGateway;
@Service @RequiredArgsConstructor
public class ConciliacionPagoService {
    private final CoreBancarioGateway core;
    private final RegistroPagoService pagos;
    public Map<String,Object> simular(RegistrarPago req){
        var externo=core.consultarPago(req.deudaId(),req.monto(),req.fechaPago());
        var pago=pagos.registrarPago(new RegistrarPago(externo.idDeuda(),req.promesaId(),externo.monto(),externo.fecha()),externo.referencia());
        return Map.of("message","Abono del core SIMULADO aplicado.","referencia",externo.referencia(),"pagoId",pago.getId());
    }
}
