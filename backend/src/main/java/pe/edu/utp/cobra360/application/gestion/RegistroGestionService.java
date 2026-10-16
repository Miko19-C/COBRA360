package pe.edu.utp.cobra360.application.gestion;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;

import java.time.*;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.cobra360.domain.BusinessException;
import pe.edu.utp.cobra360.domain.model.*;
import pe.edu.utp.cobra360.domain.patterns.*;
import pe.edu.utp.cobra360.domain.ports.*;

import pe.edu.utp.cobra360.application.command.Requests.RegistrarGestion;

@Service @RequiredArgsConstructor
public class RegistroGestionService {
    private final IAsignacionRepository asignaciones;
    private final IDeudaRepository deudas;
    private final ITipoResultadoRepository resultados;
    private final ITelefonoRepository telefonos;
    private final IGestionRepository gestiones;
    private final IPromesaPagoRepository promesas;
    private final AccesoService acceso;
    private final AuditoriaService auditoria;
    private final Clock clock;
    private final Notificador notificador;

    @Transactional
    public Map<String,Object> registrar(RegistrarGestion req){
        var inicial=asignaciones.findById(req.asignacionId()).orElseThrow(()->new BusinessException("Asignación inexistente."));
        acceso.verificarAsesor(inicial.getIdAsesor());
        // Orden global de bloqueos: deuda -> asignación/promesa. Evita sobregiros y carreras.
        var deuda=deudas.lockById(inicial.getIdDeuda()).orElseThrow();
        var asignacion=asignaciones.lockById(inicial.getId()).orElseThrow();
        if(!asignacion.getFechaAsignacion().equals(LocalDate.now(clock)))
            throw new BusinessException("Solo puedes registrar gestiones de la cartera del día.");
        if(deuda.getSaldoActual().signum()<=0)throw new BusinessException("La deuda ya está cancelada.");
        var tipo=resultados.findById(req.tipoResultadoId()).orElseThrow(()->new BusinessException("Resultado inexistente."));
        CreadorAccion creador=Boolean.TRUE.equals(tipo.getGeneraPromesa())?
            new CreadorAccion.CreadorPromesa():new CreadorAccion.CreadorSeguimiento();
        var accion=creador.preparar();
        Telefono telefono=null;
        if(req.telefonoId()!=null){
            telefono=telefonos.findById(req.telefonoId()).orElseThrow(()->new BusinessException("Teléfono inexistente."));
            if(!telefono.getIdCliente().equals(deuda.getIdCliente()))throw new BusinessException("El teléfono no pertenece al cliente.");
            if("ERRADO".equals(telefono.getEstado()) || "FUERA_SERVICIO".equals(telefono.getEstado()))
                throw new BusinessException("Selecciona un teléfono habilitado.");
        }
        if(accion.requierePromesa()){
            FamiliaCobranza.paraTramo(deuda.getTramoMora()).crearPolitica()
                .validar(req.montoPromesa(),deuda.getSaldoActual(),req.fechaCompromiso(),LocalDate.now(clock));
        }else if(req.montoPromesa()!=null || req.fechaCompromiso()!=null){
            throw new BusinessException("Este resultado no permite registrar una promesa.");
        }
        var gestion=new Gestion();
        gestion.setIdAsignacion(asignacion.getId());gestion.setIdTipoResultado(tipo.getId());
        gestion.setIdTelefono(req.telefonoId());gestion.setObservacion(req.observacion().trim());
        gestion.setFechaContacto(LocalDateTime.now(clock));gestiones.save(gestion);
        if(telefono!=null){
            switch(tipo.getNombre()){
                case "Número errado" -> telefono.setEstado("ERRADO");
                case "Teléfono fuera de servicio" -> telefono.setEstado("FUERA_SERVICIO");
                case "No contesta" -> telefono.setEstado("NO_CONTESTA");
                default -> telefono.setEstado("ACTIVO");
            }
        }
        asignacion.setEstado("GESTIONADA");
        Long promesaId=0L;
        if(accion.requierePromesa()){
            var p=new PromesaPago();p.setIdGestion(gestion.getId());p.setMontoComprometido(req.montoPromesa());
            p.setFechaCompromiso(req.fechaCompromiso());p.setEstado("PENDIENTE");promesas.save(p);promesaId=p.getId();
            notificador.enviar("Cliente #"+deuda.getIdCliente(),"Compromiso de S/ "+req.montoPromesa()+" para "+req.fechaCompromiso());
        }
        auditoria.registrar("Factory Method · Gestión #"+gestion.getId(),"Resultado: "+tipo.getNombre()+"; Abstract Factory: "+deuda.getTramoMora());
        return Map.of("gestionId",gestion.getId(),"promesaId",promesaId,"message",accion.siguientePaso());
    }
}
