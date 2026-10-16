package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.Deuda;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface IDeudaRepository {
    Optional<Deuda> findById(Long id);
    Optional<Deuda> lockById(Long id);
    List<Deuda> findAll();
}
