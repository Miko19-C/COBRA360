package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.Gestion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface GestionRepository extends JpaRepository<Gestion, Long>, pe.edu.utp.cobra360.domain.ports.IGestionRepository {
}
