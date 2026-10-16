package pe.edu.utp.cobra360.application.pagos;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;

import java.time.*;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.cobra360.domain.BusinessException;
import pe.edu.utp.cobra360.domain.model.*;
import pe.edu.utp.cobra360.domain.ports.*;
import pe.edu.utp.cobra360.application.command.Requests.RegistrarPago;

@Service @RequiredArgsConstructor
public class RegistroPagoService {
    private final IDeudaRepository deudas;
    private final IPromesaPagoRepository promesas;
    private final IPagoRepository pagos;
    private final ConsultaSeguimientoPort consultas;
    private final AccesoService acceso;
    private final AuditoriaService auditoria;
    private final Clock clock;

    @Transactional
    public Pago registrarPago(RegistrarPago req,String referencia){
        var deuda=deudas.lockById(req.deudaId()).orElseThrow(()->new BusinessException("Deuda no encontrada."));
        if(!acceso.supervisor()){
            if(!consultas.deudaAsignada(deuda.getId(),acceso.asesorId(),LocalDate.now(clock)))
                throw new pe.edu.utp.cobra360.domain.AccesoDenegadoException("Deuda fuera de tu cartera del día.");
        }
        if(referencia!=null && consultas.referenciaRegistrada(referencia))
            throw new BusinessException("Este abono externo ya fue registrado. No se aplicó otra vez.");
        if(req.monto().signum()<=0 || req.monto().compareTo(deuda.getSaldoActual())>0)
            throw new BusinessException("El pago debe ser mayor que cero y no superar el saldo actual.");
        if(req.fechaPago().isAfter(LocalDate.now(clock)) || req.fechaPago().isBefore(deuda.getFechaVencimiento()))
            throw new BusinessException("El pago debe estar entre el vencimiento de la deuda y hoy.");
        PromesaPago promesa=null;
        if(req.promesaId()!=null){
            promesa=promesas.lockById(req.promesaId()).orElseThrow(()->new BusinessException("Promesa no encontrada."));
            Long deudaPromesa=consultas.deudaDeGestion(promesa.getIdGestion());
            if(!deuda.getId().equals(deudaPromesa))throw new BusinessException("La promesa no pertenece a esta deuda.");
            LocalDate fechaGestion=consultas.fechaDeGestion(promesa.getIdGestion());
            if(req.fechaPago().isBefore(fechaGestion))throw new BusinessException("El pago no puede ser anterior a la gestión que originó la promesa.");
            BigDecimal abonado=pagos.findByIdPromesa(promesa.getId()).stream().map(Pago::getMonto).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(req.monto().compareTo(promesa.getMontoComprometido().subtract(abonado))>0)
                throw new BusinessException("El importe supera lo pendiente de la promesa. Registra el excedente como pago sin promesa.");
            BigDecimal acumulado=abonado.add(req.monto());
            promesa.setEstado(acumulado.compareTo(promesa.getMontoComprometido())>=0?"CUMPLIDA":
                promesa.getFechaCompromiso().isBefore(LocalDate.now(clock))?"INCUMPLIDA":"PARCIAL");
        }
        var pago=new Pago();pago.setIdDeuda(deuda.getId());pago.setIdPromesa(req.promesaId());
        pago.setMonto(req.monto());pago.setFechaPago(req.fechaPago());pago.setReferenciaExterna(referencia);
        pagos.save(pago);deuda.setSaldoActual(deuda.getSaldoActual().subtract(req.monto()));
        auditoria.registrar(referencia==null?"Pago registrado":"Adapter · Abono simulado","Pago #"+pago.getId()+"; deuda #"+deuda.getId()+"; monto "+req.monto());
        return pago;
    }
}
