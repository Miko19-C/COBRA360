package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.PromesaPago;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface PromesaPagoRepository extends JpaRepository<PromesaPago, Long>, pe.edu.utp.cobra360.domain.ports.IPromesaPagoRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from PromesaPago e where e.id = :id")
    Optional<PromesaPago> lockById(@Param("id") Long id);
    List<PromesaPago> findByEstadoNotAndFechaCompromisoBefore(String estado, LocalDate fecha);
}
