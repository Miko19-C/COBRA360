package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="gestion")
@Getter @Setter @NoArgsConstructor
public class Gestion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_gestion") private Long id;
    private Long idAsignacion;
    private Long idTipoResultado;
    private Long idTelefono;
    private LocalDateTime fechaContacto;
    private String observacion;
}
