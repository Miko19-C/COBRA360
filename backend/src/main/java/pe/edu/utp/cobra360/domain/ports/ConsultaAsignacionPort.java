package pe.edu.utp.cobra360.domain.ports;
import java.time.LocalDate;
import java.util.List;
public interface ConsultaAsignacionPort {
    record Candidato(Long id,String tramo,Long carga) {}
    List<Candidato> candidatos(LocalDate fecha);
}
