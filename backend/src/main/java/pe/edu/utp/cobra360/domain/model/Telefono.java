package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="telefono")
@Getter @Setter @NoArgsConstructor
public class Telefono {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_telefono") private Long id;
    private Long idCliente;
    private String numero;
    private String estado;
    private Boolean esPrincipal;
}
