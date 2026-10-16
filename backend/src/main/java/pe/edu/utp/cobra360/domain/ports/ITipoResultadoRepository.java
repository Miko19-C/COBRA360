package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.TipoResultado;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface ITipoResultadoRepository {
    Optional<TipoResultado> findById(Long id);
}
