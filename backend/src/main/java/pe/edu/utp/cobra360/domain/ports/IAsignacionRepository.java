package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.Asignacion;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface IAsignacionRepository {
    Optional<Asignacion> findById(Long id);
    Optional<Asignacion> lockById(Long id);
    boolean existsByIdDeudaAndFechaAsignacion(Long idDeuda, LocalDate fecha);
    <S extends Asignacion> S saveAndFlush(S entidad);
}
