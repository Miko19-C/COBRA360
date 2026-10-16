package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.Telefono;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface ITelefonoRepository {
    Optional<Telefono> findById(Long id);
}
