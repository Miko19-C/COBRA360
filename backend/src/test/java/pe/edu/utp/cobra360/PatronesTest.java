package pe.edu.utp.cobra360;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import pe.edu.utp.cobra360.domain.patterns.*;
import pe.edu.utp.cobra360.domain.patterns.NodoCartera.*;
import pe.edu.utp.cobra360.infrastructure.core.CoreSimuladoAdapter;
import pe.edu.utp.cobra360.infrastructure.notifications.Notificadores.*;

class PatronesTest {
    @Test void prototypeNoComparteColeccionMutable(){
        var base=new PlantillaGestion("Base",List.of("Verificar"));var copia=base.clone();
        copia.getPasos().add("Confirmar");assertThat(base.getPasos()).containsExactly("Verificar");
    }
    @Test void compositeSumaGruposAnidadosYHojas(){
        var raiz=new GrupoCartera("Todo").agregar(new DeudaHoja("A",new BigDecimal("25")))
            .agregar(new GrupoCartera("Grupo").agregar(new DeudaHoja("B",new BigDecimal("75"))));
        assertThat(raiz.saldo()).isEqualByComparingTo("100");assertThat(raiz.cantidad()).isEqualTo(2);
    }
    @Test void builderCongelaElReporte(){
        var builder=new ReporteCobranza.Builder().agregarSeccion("Cartera");var r=builder.build();builder.agregarSeccion("Pagos");
        assertThat(r.secciones()).containsExactly("Cartera");assertThatThrownBy(()->r.secciones().add("Otra")).isInstanceOf(UnsupportedOperationException.class);
    }
    @Test void adapterConvierteCentavosSinPerderPrecision(){
        var pago=new CoreSimuladoAdapter().consultarPago(1L,new BigDecimal("123.45"),LocalDate.of(2026,10,16));
        assertThat(pago.monto()).isEqualByComparingTo("123.45");assertThat(pago.referencia()).isEqualTo("SIM-1-2026-10-16-12345");
    }
    @Test void decoratorConservaContratoYAgregaAuditoria(){
        List<String> eventos=new ArrayList<>();
        var n=new ConAuditoria(new ConPieInstitucional(new BandejaInterna()),eventos::add);
        String resultado=n.enviar("Cliente","Mensaje");assertThat(resultado).contains("COBRA360");assertThat(eventos).containsExactly(resultado);
    }
    @Test void familiasProducenProductosCompatibles(){
        var hoy=LocalDate.of(2026,10,16);
        for(String tramo:List.of("TEMPRANA","TARDIA","CASTIGO")){
            var f=FamiliaCobranza.paraTramo(tramo);assertThat(f.crearMensaje().texto()).isNotBlank();
            assertThatCode(()->f.crearPolitica().validar(new BigDecimal("10"),new BigDecimal("100"),hoy,hoy)).doesNotThrowAnyException();
        }
    }
    @Test void factoryMethodSeleccionaProductoMedianteCreador(){
        assertThat(new CreadorAccion.CreadorPromesa().ejecutar()).contains("Compromiso");
        assertThat(new CreadorAccion.CreadorSeguimiento().ejecutar()).contains("Gestión");
    }
}
