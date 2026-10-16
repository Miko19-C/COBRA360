package pe.edu.utp.cobra360.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad del modelo relacional aprobado en PC1. */
@Entity @Table(name="asesor")
@Getter @Setter @NoArgsConstructor
public class Asesor {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_asesor") private Long id;
    private Long idPerfil;
    private String nombre;
    private String usuario;
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
    @Column(length=1) private String estado;
}
