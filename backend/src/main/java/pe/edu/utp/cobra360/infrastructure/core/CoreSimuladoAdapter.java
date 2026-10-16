package pe.edu.utp.cobra360.infrastructure.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import pe.edu.utp.cobra360.domain.ports.CoreBancarioGateway;

/** ADAPTER: traduce centavos y nombres de un contrato legado. No conecta con BBVA real. */
@Component
public class CoreSimuladoAdapter implements CoreBancarioGateway {
    public record AbonoLegado(String codigoOperacion, long obligacion, long importeCentavos, String diaISO) {}
    public static final class CoreLegadoSimulado {
        public AbonoLegado obtener(long deuda, long centavos, String fecha) {
            return new AbonoLegado("SIM-"+deuda+"-"+fecha+"-"+centavos,deuda,centavos,fecha);
        }
    }
    private final CoreLegadoSimulado legado=new CoreLegadoSimulado();
    public PagoExterno consultarPago(Long idDeuda, BigDecimal monto, LocalDate fecha) {
        var abono=legado.obtener(idDeuda,monto.movePointRight(2).longValueExact(),fecha.toString());
        return new PagoExterno(abono.codigoOperacion(),abono.obligacion(),
            BigDecimal.valueOf(abono.importeCentavos(),2),LocalDate.parse(abono.diaISO()));
    }
}
