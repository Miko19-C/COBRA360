package pe.edu.utp.cobra360.infrastructure.query;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.cobra360.domain.ports.IndicadoresPort;
@Repository @RequiredArgsConstructor
public class JdbcIndicadoresAdapter implements IndicadoresPort {
    private final JdbcTemplate jdbc;
    public BigDecimal recuperado(LocalDate fecha,boolean supervisor,Long asesorId,LocalDate fechaCartera){
        String sql="select coalesce(sum(monto),0) from pago p where fecha_pago=?";
        if(supervisor)return jdbc.queryForObject(sql,BigDecimal.class,fecha);
        return jdbc.queryForObject(sql+" "+"""
            and exists(select 1 from asignacion a where a.id_deuda=p.id_deuda and a.id_asesor=? and a.fecha_asignacion=?)
            """,BigDecimal.class,fecha,asesorId,fechaCartera);
    }
}
