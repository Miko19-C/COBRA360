package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.Pago;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface PagoRepository extends JpaRepository<Pago, Long>, pe.edu.utp.cobra360.domain.ports.IPagoRepository {
    List<Pago> findByIdPromesa(Long idPromesa);
}
