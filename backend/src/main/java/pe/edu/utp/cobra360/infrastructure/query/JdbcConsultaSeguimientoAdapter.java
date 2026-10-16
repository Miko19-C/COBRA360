package pe.edu.utp.cobra360.infrastructure.query;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.cobra360.domain.ports.ConsultaSeguimientoPort;
@Repository @RequiredArgsConstructor
public class JdbcConsultaSeguimientoAdapter implements ConsultaSeguimientoPort {
    private final JdbcTemplate jdbc;
    public boolean deudaAsignada(Long deuda,Long asesor,LocalDate fecha){
        Long count=jdbc.queryForObject("select count(*) from asignacion where id_deuda=? and id_asesor=? and fecha_asignacion=?",Long.class,deuda,asesor,fecha);
        return count!=null && count>0;
    }
    public boolean referenciaRegistrada(String referencia){
        return Boolean.TRUE.equals(jdbc.queryForObject("select count(*)>0 from pago where referencia_externa=?",Boolean.class,referencia));
    }
    public Long deudaDeGestion(Long gestion){
        return jdbc.queryForObject("select a.id_deuda from gestion g join asignacion a on a.id_asignacion=g.id_asignacion where g.id_gestion=?",Long.class,gestion);
    }
    public LocalDate fechaDeGestion(Long gestion){
        return jdbc.queryForObject("select date(fecha_contacto) from gestion where id_gestion=?",LocalDate.class,gestion);
    }
    public void marcarVencidas(LocalDate hoy){
        jdbc.update("""
            update promesa_pago p set estado='INCUMPLIDA'
            where p.fecha_compromiso<? and p.estado in ('PENDIENTE','PARCIAL')
            and coalesce((select sum(pa.monto) from pago pa where pa.id_promesa=p.id_promesa),0)<p.monto_comprometido
            """,hoy);
    }
}
