package pe.edu.utp.cobra360.application.tecnico;
import pe.edu.utp.cobra360.application.reportes.ReporteService;

import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.domain.patterns.*;
import pe.edu.utp.cobra360.domain.ports.*;


@Service @RequiredArgsConstructor
public class PatronesService {
    private final CoreBancarioGateway core;
    private final ReporteService reportes;
    private final Clock clock;
    private final MuestraNotificadorPort muestraNotificador;
    public List<Map<String,Object>> evidencias(){
        var original=new PlantillaGestion("Contacto inicial",List.of("Validar identidad","Consultar disponibilidad","Registrar resultado"));
        var copia=original.clone();copia.getPasos().add("Acordar seguimiento");
        var familia=FamiliaCobranza.paraTramo("TARDIA");
        var lista=new ArrayList<Map<String,Object>>();
        lista.add(item("Singleton","Creacional","Configuración única", "ConfiguracionCobranza.java",
            Map.of("mismaInstancia",ConfiguracionCobranza.INSTANCE==ConfiguracionCobranza.INSTANCE,"moneda",ConfiguracionCobranza.INSTANCE.moneda(),"maxDias",ConfiguracionCobranza.INSTANCE.maxDiasCompromiso())));
        lista.add(item("Prototype","Creacional","Copiar guion sin modificar el original","PlantillaGestion.java",
            Map.of("original",original.getPasos(),"copia",copia.getPasos(),"listasIndependientes",original.getPasos()!=copia.getPasos())));
        lista.add(item("Factory Method","Creacional","Crear la acción del resultado","CreadorAccion.java",
            Map.of("promesa",new CreadorAccion.CreadorPromesa().ejecutar(),"seguimiento",new CreadorAccion.CreadorSeguimiento().ejecutar())));
        lista.add(item("Abstract Factory","Creacional","Familias de mensaje y política por tramo","FamiliaCobranza.java",
            Map.of("mensaje",familia.crearMensaje().texto(),"politica",familia.crearPolitica().getClass().getSimpleName(),"familia",familia.getClass().getSimpleName())));
        var reporte=reportes.construir();
        lista.add(item("Builder","Creacional","Construcción del reporte operativo","ReporteCobranza.java",reporte.get("reporte")));
        lista.add(item("Adapter","Estructural","Contrato legado de abonos a contrato del dominio","CoreSimuladoAdapter.java",
            core.consultarPago(1L,new BigDecimal("125.50"),LocalDate.now(clock))));
        lista.add(item("Facade","Estructural","Registrar gestión y sus subsistemas en una transacción","CobranzaFacade.java",
            Map.of("operacion","POST /api/gestiones","subsistemas",List.of("Gestión","Teléfono","Promesa","Notificación interna","Auditoría"),
                "demostracion","Registrar una gestión desde la cartera y revisar Auditoría.")));
        lista.add(item("Decorator","Estructural","Envolver notificación con pie y auditoría","Notificadores.java",muestraNotificador.ejemplo()));
        lista.add(item("Composite","Estructural","Sumar hojas y grupos con el mismo contrato","NodoCartera.java",reporte.get("arbol")));
        return lista;
    }
    private Map<String,Object> item(String nombre,String tipo,String uso,String archivo,Object evidencia){
        return Map.of("nombre",nombre,"tipo",tipo,"uso",uso,"archivo",archivo,"evidencia",evidencia);
    }
}
