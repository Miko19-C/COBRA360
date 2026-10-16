package pe.edu.utp.cobra360.infrastructure.repository;

import pe.edu.utp.cobra360.domain.model.Telefono;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;

public interface TelefonoRepository extends JpaRepository<Telefono, Long>, pe.edu.utp.cobra360.domain.ports.ITelefonoRepository {
    List<Telefono> findByIdCliente(Long idCliente);
}
