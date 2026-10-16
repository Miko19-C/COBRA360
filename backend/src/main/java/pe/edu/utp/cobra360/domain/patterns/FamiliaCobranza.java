package pe.edu.utp.cobra360.domain.patterns;

import java.math.BigDecimal;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.BusinessException;

/** ABSTRACT FACTORY: familias de dos productos relacionados para cada tramo. */
public interface FamiliaCobranza {
    interface Mensaje { String texto(); }
    interface PoliticaCompromiso { void validar(BigDecimal monto, BigDecimal saldo, LocalDate fecha, LocalDate hoy); }
    Mensaje crearMensaje();
    PoliticaCompromiso crearPolitica();

    static FamiliaCobranza paraTramo(String tramo) {
        return switch(tramo) {
            case "TEMPRANA" -> new FamiliaTemprana();
            case "TARDIA" -> new FamiliaTardia();
            case "CASTIGO" -> new FamiliaCastigo();
            default -> throw new BusinessException("Tramo de mora desconocido.");
        };
    }
    class PoliticaBase implements PoliticaCompromiso {
        public void validar(BigDecimal monto, BigDecimal saldo, LocalDate fecha, LocalDate hoy) {
            if (monto==null || monto.signum()<=0 || monto.compareTo(saldo)>0)
                throw new BusinessException("La promesa debe ser mayor que cero y no superar el saldo.");
            if(fecha==null || fecha.isBefore(hoy) || fecha.isAfter(hoy.plusDays(ConfiguracionCobranza.INSTANCE.maxDiasCompromiso())))
                throw new BusinessException("La fecha de compromiso debe estar entre hoy y los próximos 90 días.");
        }
    }
    final class PoliticaTemprana extends PoliticaBase {}
    final class PoliticaTardia extends PoliticaBase {}
    final class PoliticaCastigo extends PoliticaBase {}
    final class MensajeTemprano implements Mensaje { public String texto() { return "Recordatorio preventivo: acordar una fecha de regularización."; } }
    final class MensajeTardio implements Mensaje { public String texto() { return "Seguimiento de deuda vencida: registrar un compromiso verificable."; } }
    final class MensajeCastigo implements Mensaje { public String texto() { return "Gestión especializada: cualquier descuento requiere autorización externa al prototipo."; } }
    final class FamiliaTemprana implements FamiliaCobranza {
        public Mensaje crearMensaje(){return new MensajeTemprano();}
        public PoliticaCompromiso crearPolitica(){return new PoliticaTemprana();}
    }
    final class FamiliaTardia implements FamiliaCobranza {
        public Mensaje crearMensaje(){return new MensajeTardio();}
        public PoliticaCompromiso crearPolitica(){return new PoliticaTardia();}
    }
    final class FamiliaCastigo implements FamiliaCobranza {
        public Mensaje crearMensaje(){return new MensajeCastigo();}
        public PoliticaCompromiso crearPolitica(){return new PoliticaCastigo();}
    }
}
