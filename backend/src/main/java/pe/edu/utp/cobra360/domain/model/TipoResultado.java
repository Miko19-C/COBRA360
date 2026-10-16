package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="tipo_resultado")
@Getter @Setter @NoArgsConstructor
public class TipoResultado {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_tipo_resultado") private Long id;
    private String nombre;
    private Boolean esContactoEfectivo;
    private Boolean generaPromesa;
}
