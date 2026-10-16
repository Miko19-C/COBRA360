package pe.edu.utp.cobra360.application.reportes;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;
import pe.edu.utp.cobra360.application.cartera.CarteraQueryService;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.cobra360.domain.ports.IndicadoresPort;
import pe.edu.utp.cobra360.domain.patterns.*;
import pe.edu.utp.cobra360.domain.patterns.NodoCartera.*;

@Service @RequiredArgsConstructor @org.springframework.transaction.annotation.Transactional(readOnly=true)
public class ReporteService {
    private final CarteraQueryService consultas;
    private final IndicadoresPort indicadores;
    private final AccesoService acceso;
    private final Clock clock;

    public Map<String,Object> resumen(){
        var cartera=consultas.cartera();var promesas=consultas.promesas();
        BigDecimal saldo=cartera.stream().map(r->(BigDecimal)r.get("saldo_actual")).reduce(BigDecimal.ZERO,BigDecimal::add);
        long gestionadas=cartera.stream().filter(r->"GESTIONADA".equals(r.get("estado"))).count();
        long cumplidas=promesas.stream().filter(r->"CUMPLIDA".equals(r.get("estado"))).count();
        long vencidas=promesas.stream().filter(r->"INCUMPLIDA".equals(r.get("estado"))).count();
        var hoy=LocalDate.now(clock);
        BigDecimal recuperado=indicadores.recuperado(hoy,acceso.supervisor(),acceso.asesorId(),hoy);
        List<Map<String,Object>> semana=new ArrayList<>();
        for(int i=6;i>=0;i--){
            var fecha=hoy.minusDays(i);
            BigDecimal valor=indicadores.recuperado(fecha,acceso.supervisor(),acceso.asesorId(),hoy);
            semana.add(Map.of("fecha",fecha,"monto",valor));
        }
        return Map.of("asignadas",cartera.size(),"gestionadas",gestionadas,"saldo",saldo,"recuperadoHoy",recuperado,
            "promesas",promesas.size(),"cumplidas",cumplidas,"incumplidas",vencidas,"semana",semana,
            "hoy",hoy);
    }
    public Map<String,Object> construir(){
        var raiz=new GrupoCartera("Cartera del día");
        for(String tramo:List.of("TEMPRANA","TARDIA","CASTIGO")){
            var grupo=new GrupoCartera(tramo);
            consultas.cartera().stream().filter(r->tramo.equals(r.get("tramo_mora")))
                .forEach(r->grupo.agregar(new DeudaHoja((String)r.get("cliente"),(BigDecimal)r.get("saldo_actual"))));
            raiz.agregar(grupo);
        }
        var reporte=new ReporteCobranza.Builder().titulo("Reporte operativo COBRA360")
            .periodo(LocalDate.now(clock).toString()).saldo(raiz.saldo())
            .agregarSeccion("Cartera por tramo de mora").agregarSeccion("Seguimiento de promesas")
            .agregarSeccion("Pagos registrados").build();
        return Map.of("reporte",reporte,"arbol",convertir(raiz),"indicadores",resumen());
    }
    private Map<String,Object> convertir(NodoCartera n){
        return Map.of("nombre",n.nombre(),"saldo",n.saldo(),"cantidad",n.cantidad(),"hijos",n.hijos().stream().map(this::convertir).toList());
    }
    public String csv(){
        StringBuilder s=new StringBuilder("\uFEFFCliente;Documento;Producto;Tramo;Saldo;Estado\r\n");
        for(var fila:consultas.cartera()){
            var columnas=List.of("cliente","nro_documento","producto","tramo_mora","saldo_actual","estado");
            s.append(String.join(";",columnas.stream().map(k->celda(fila.get(k))).toList())).append("\r\n");
        }
        return s.toString();
    }
    private String celda(Object valor){
        String t=String.valueOf(valor==null?"":valor);
        if(t.matches("^[=+\\-@].*"))t="'"+t;
        return "\""+t.replace("\"","\"\"")+"\"";
    }
}
