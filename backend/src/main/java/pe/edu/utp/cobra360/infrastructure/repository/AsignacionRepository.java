package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.Asignacion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long>, pe.edu.utp.cobra360.domain.ports.IAsignacionRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Asignacion e where e.id = :id")
    Optional<Asignacion> lockById(@Param("id") Long id);
    boolean existsByIdDeudaAndFechaAsignacion(Long idDeuda, LocalDate fecha);
}
