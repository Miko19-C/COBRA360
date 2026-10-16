package pe.edu.utp.cobra360.application.cartera;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;

import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.cobra360.domain.model.Asignacion;
import pe.edu.utp.cobra360.domain.ports.*;

@Service @RequiredArgsConstructor
public class AsignacionService {
    private final IDeudaRepository deudas;
    private final IAsignacionRepository asignaciones;
    private final ConsultaAsignacionPort consultas;
    private final AuditoriaService auditoria;
    private final Clock clock;

    @Transactional
    public Map<String,Object> asignar(){
        var hoy=LocalDate.now(clock);
        var asesores=consultas.candidatos(hoy);
        Map<Long,Long> cargas=new HashMap<>();
        for(var candidato:asesores)cargas.put(candidato.id(),candidato.carga());
        int creadas=0,sinPerfil=0;
        for(var item:deudas.findAll().stream().sorted(Comparator.comparing(pe.edu.utp.cobra360.domain.model.Deuda::getId)).toList()){
            var d=deudas.lockById(item.getId()).orElseThrow();
            if(d.getSaldoActual().signum()<=0 || !d.getFechaVencimiento().isBefore(hoy) || asignaciones.existsByIdDeudaAndFechaAsignacion(d.getId(),hoy))continue;
            var elegido=asesores.stream().filter(a->a.tramo().equals(d.getTramoMora()))
                .min(Comparator.comparingLong(a->cargas.get(a.id())));
            if(elegido.isEmpty()){sinPerfil++;continue;}
            Long asesor=elegido.get().id();
            var a=new Asignacion();a.setIdDeuda(d.getId());a.setIdAsesor(asesor);a.setFechaAsignacion(hoy);a.setEstado("PENDIENTE");
            asignaciones.saveAndFlush(a);cargas.put(asesor,cargas.get(asesor)+1);creadas++;
        }
        auditoria.registrar("Asignación automática","Nuevas: "+creadas+"; sin perfil habilitado: "+sinPerfil);
        return Map.of("asignadas",creadas,"sinPerfil",sinPerfil,"message",creadas+" deudas asignadas; "+sinPerfil+" sin asesor habilitado.");
    }
}
