package pe.edu.utp.cobra360.web.reportes;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.reportes.ReporteService;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ReporteController {
    private final ReporteService reportes;
    @GetMapping("/resumen") public Object resumen(){return reportes.resumen();}
    @GetMapping("/reportes") public Object reporte(){return reportes.construir();}
    @GetMapping("/reportes/cartera.csv") public ResponseEntity<byte[]> csv(){
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=cartera-cobra360.csv")
            .contentType(new MediaType("text","csv",StandardCharsets.UTF_8)).body(reportes.csv().getBytes(StandardCharsets.UTF_8));
    }
}
