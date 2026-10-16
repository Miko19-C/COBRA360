package pe.edu.utp.cobra360.domain.ports;

import java.util.*;
import java.time.LocalDate;
import pe.edu.utp.cobra360.domain.model.Gestion;

/** Puerto de persistencia del dominio: no conoce Spring Data ni MySQL. */
public interface IGestionRepository {
    <S extends Gestion> S save(S entidad);
}
