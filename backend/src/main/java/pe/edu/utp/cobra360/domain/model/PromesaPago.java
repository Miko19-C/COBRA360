package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="promesa_pago")
@Getter @Setter @NoArgsConstructor
public class PromesaPago {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_promesa") private Long id;
    private Long idGestion;
    @Column(precision=12, scale=2) private BigDecimal montoComprometido;
    private LocalDate fechaCompromiso;
    private String estado;
}
