package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.Pago;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface IPagoRepository {
    <S extends Pago> S save(S entidad);
    List<Pago> findByIdPromesa(Long idPromesa);
}
