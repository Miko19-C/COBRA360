package pe.edu.utp.cobra360.application.command;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class Requests {
    private Requests(){}
    public record RegistrarGestion(
        @NotNull @Positive Long asignacionId,
        @NotNull @Positive Long tipoResultadoId,
        @Positive Long telefonoId,
        @NotBlank @Size(max=1000) String observacion,
        @DecimalMin("0.01") @Digits(integer=10,fraction=2) BigDecimal montoPromesa,
        LocalDate fechaCompromiso) {}
    public record RegistrarPago(
        @NotNull @Positive Long deudaId,
        @Positive Long promesaId,
        @NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) BigDecimal monto,
        @NotNull LocalDate fechaPago) {}
}
