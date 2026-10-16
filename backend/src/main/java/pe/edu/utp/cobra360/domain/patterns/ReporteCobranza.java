package pe.edu.utp.cobra360.domain.patterns;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
/** BUILDER manual: crea un reporte inmutable con secciones opcionales. */
public record ReporteCobranza(String titulo, String periodo, BigDecimal saldo, List<String> secciones) {
    public ReporteCobranza { secciones=List.copyOf(secciones); }
    public static class Builder {
        private String titulo="Resumen de cartera";
        private String periodo="";
        private BigDecimal saldo=BigDecimal.ZERO;
        private final List<String> secciones=new ArrayList<>();
        public Builder titulo(String valor){titulo=valor;return this;}
        public Builder periodo(String valor){periodo=valor;return this;}
        public Builder saldo(BigDecimal valor){saldo=valor;return this;}
        public Builder agregarSeccion(String valor){secciones.add(valor);return this;}
        public ReporteCobranza build(){
            if(titulo==null || titulo.isBlank())throw new IllegalArgumentException("El reporte requiere título.");
            return new ReporteCobranza(titulo,periodo,saldo,secciones);
        }
    }
}
