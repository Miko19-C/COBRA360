package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.PerfilRiesgo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface PerfilRiesgoRepository extends JpaRepository<PerfilRiesgo, Long> {
}
