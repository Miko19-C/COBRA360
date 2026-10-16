package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="deuda")
@Getter @Setter @NoArgsConstructor
public class Deuda {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_deuda") private Long id;
    private Long idCliente;
    private String producto;
    @Column(precision=12, scale=2) private BigDecimal montoTotal;
    @Column(precision=12, scale=2) private BigDecimal saldoActual;
    private LocalDate fechaVencimiento;
    private String tramoMora;
}
