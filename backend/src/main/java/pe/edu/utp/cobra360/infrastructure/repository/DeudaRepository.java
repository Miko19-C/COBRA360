package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.Deuda;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface DeudaRepository extends JpaRepository<Deuda, Long>, pe.edu.utp.cobra360.domain.ports.IDeudaRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Deuda e where e.id = :id")
    Optional<Deuda> lockById(@Param("id") Long id);
}
