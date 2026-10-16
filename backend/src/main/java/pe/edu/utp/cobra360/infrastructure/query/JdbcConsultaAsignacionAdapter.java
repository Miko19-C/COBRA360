package pe.edu.utp.cobra360.infrastructure.query;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.cobra360.domain.ports.ConsultaAsignacionPort;
@Repository @RequiredArgsConstructor
public class JdbcConsultaAsignacionAdapter implements ConsultaAsignacionPort {
    private final JdbcTemplate jdbc;
    public List<Candidato> candidatos(LocalDate fecha){
        return jdbc.query("""
            select a.id_asesor,p.tramo_habilitado,
            (select count(*) from asignacion x where x.id_asesor=a.id_asesor and x.fecha_asignacion=?) as carga
            from asesor a join perfil_riesgo p on p.id_perfil=a.id_perfil
            where a.estado='A' and p.tramo_habilitado<>'TODOS' order by a.id_asesor
            """,(rs,i)->new Candidato(rs.getLong(1),rs.getString(2),rs.getLong(3)),fecha);
    }
}
