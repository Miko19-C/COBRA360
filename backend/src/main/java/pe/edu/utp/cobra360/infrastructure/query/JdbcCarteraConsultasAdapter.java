package pe.edu.utp.cobra360.infrastructure.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.cobra360.domain.BusinessException;
import java.time.*;
import java.util.*;

@Repository @RequiredArgsConstructor @Transactional(readOnly=true)
public class JdbcCarteraConsultasAdapter implements pe.edu.utp.cobra360.domain.ports.ConsultasCarteraPort {
    private final JdbcTemplate jdbc;

    public List<Map<String,Object>> cartera(boolean supervisor,Long asesorId,LocalDate hoy){
        String sql="""
            select v.*,c.id_cliente,c.correo,ase.nombre as asesor,
              (select t.numero from telefono t where t.id_cliente=c.id_cliente and t.es_principal=true limit 1) as telefono
            from v_cartera_dia v
            join deuda d on d.id_deuda=v.id_deuda join cliente c on c.id_cliente=d.id_cliente
            join asesor ase on ase.id_asesor=v.id_asesor
            where v.fecha_asignacion=?
            """;
        return supervisor?jdbc.queryForList(sql+" order by v.estado,v.saldo_actual desc",hoy):
            jdbc.queryForList(sql+" and v.id_asesor=? order by v.estado,v.saldo_actual desc",hoy,asesorId);
    }
    public Map<String,Object> detalle(Long id){
        var rows=jdbc.queryForList("""
            select a.id_asignacion,a.id_asesor,a.estado,d.*,c.nombre,c.tipo_documento,c.nro_documento,c.correo,ase.nombre as asesor
            from asignacion a join deuda d on d.id_deuda=a.id_deuda
            join cliente c on c.id_cliente=d.id_cliente join asesor ase on ase.id_asesor=a.id_asesor
            where a.id_asignacion=?
            """,id);
        if(rows.isEmpty())throw new BusinessException("Asignación no encontrada.");
        var dato=new LinkedHashMap<>(rows.get(0));
        dato.put("telefonos",jdbc.queryForList("select * from telefono where id_cliente=? order by es_principal desc,id_telefono",dato.get("id_cliente")));
        dato.put("historial",jdbc.queryForList("""
            select g.*,r.nombre as resultado,p.id_promesa,p.monto_comprometido,p.fecha_compromiso
            from gestion g join asignacion a on a.id_asignacion=g.id_asignacion
            join tipo_resultado r on r.id_tipo_resultado=g.id_tipo_resultado
            left join promesa_pago p on p.id_gestion=g.id_gestion
            where a.id_deuda=? order by g.fecha_contacto desc,g.id_gestion desc limit 30
            """,dato.get("id_deuda")));
        return dato;
    }
    public List<Map<String,Object>> promesas(boolean supervisor,Long asesorId,LocalDate hoy){
        String sql="""
            select p.id_promesa,p.id_gestion,p.monto_comprometido,p.fecha_compromiso,
              case when coalesce(sum(pa.monto),0)>=p.monto_comprometido then 'CUMPLIDA'
                   when p.fecha_compromiso<? then 'INCUMPLIDA'
                   when coalesce(sum(pa.monto),0)>0 then 'PARCIAL' else 'PENDIENTE' end as estado,
              coalesce(sum(pa.monto),0) as pagado,d.id_deuda,d.saldo_actual,c.nombre as cliente,c.nro_documento,
              ase.nombre as asesor,a.id_asesor,d.tramo_mora
            from promesa_pago p join gestion g on g.id_gestion=p.id_gestion
            join asignacion a on a.id_asignacion=g.id_asignacion
            join deuda d on d.id_deuda=a.id_deuda join cliente c on c.id_cliente=d.id_cliente
            join asesor ase on ase.id_asesor=a.id_asesor left join pago pa on pa.id_promesa=p.id_promesa
            """;
        String scope=supervisor?"":" where a.id_asesor=? ";
        String group="""
            group by p.id_promesa,p.id_gestion,p.monto_comprometido,p.fecha_compromiso,d.id_deuda,d.saldo_actual,
            c.nombre,c.nro_documento,ase.nombre,a.id_asesor,d.tramo_mora
            order by p.fecha_compromiso,p.id_promesa
            """;
        var rows=supervisor?jdbc.queryForList(sql+scope+group,hoy):
            jdbc.queryForList(sql+scope+group,hoy,asesorId);
        Set<Long> carteraActual=new HashSet<>();
        if(!supervisor)jdbc.queryForList("select id_deuda from asignacion where id_asesor=? and fecha_asignacion=?",Long.class,asesorId,hoy).forEach(carteraActual::add);
        for(var row:rows)row.put("puede_registrar_pago",supervisor || carteraActual.contains(((Number)row.get("id_deuda")).longValue()));
        return rows;
    }
    public List<Map<String,Object>> pagos(boolean supervisor,Long asesorId,LocalDate hoy){
        String sql="""
            select p.*,c.nombre as cliente,c.nro_documento,d.producto from pago p
            join deuda d on d.id_deuda=p.id_deuda join cliente c on c.id_cliente=d.id_cliente
            """;
        if(supervisor)return jdbc.queryForList(sql+" order by p.id_pago desc limit 100");
        return jdbc.queryForList(sql+"""
             where exists(select 1 from asignacion a where a.id_deuda=d.id_deuda and a.id_asesor=? and a.fecha_asignacion=?)
             order by p.id_pago desc limit 100
            """,asesorId,hoy);
    }
    public Map<String,Object> catalogos(boolean supervisor,LocalDate hoy){
        return Map.of("resultados",jdbc.queryForList("select * from tipo_resultado order by id_tipo_resultado"),
            "asesores",supervisor?jdbc.queryForList("""
                select a.*,p.tramo_habilitado,p.nombre as perfil from asesor a
                join perfil_riesgo p on p.id_perfil=a.id_perfil where a.estado='A'
                """):List.of(),"hoy",hoy);
    }
}
