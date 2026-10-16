package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="pago")
@Getter @Setter @NoArgsConstructor
public class Pago {
    private String referenciaExterna;
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_pago") private Long id;
    private Long idDeuda;
    private Long idPromesa;
    @Column(precision=12, scale=2) private BigDecimal monto;
    private LocalDate fechaPago;
}
