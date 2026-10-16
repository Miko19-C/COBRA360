package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.PromesaPago;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface IPromesaPagoRepository {
    Optional<PromesaPago> lockById(Long id);
    <S extends PromesaPago> S save(S entidad);
}
