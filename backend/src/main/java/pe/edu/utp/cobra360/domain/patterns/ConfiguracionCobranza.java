package pe.edu.utp.cobra360.domain.patterns;

import java.time.ZoneId;
/** SINGLETON GoF: enum garantiza una instancia incluso con serialización. No gestiona conexiones JDBC. */
public enum ConfiguracionCobranza {
    INSTANCE;
    public ZoneId zona() { return ZoneId.of("America/Lima"); }
    public int maxDiasCompromiso() { return 90; }
    public String moneda() { return "PEN"; }
}
