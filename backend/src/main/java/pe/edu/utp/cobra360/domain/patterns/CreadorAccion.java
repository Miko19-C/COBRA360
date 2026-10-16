package pe.edu.utp.cobra360.domain.patterns;

/** FACTORY METHOD: el creador declara el método fábrica; subclases deciden el producto. */
public abstract class CreadorAccion {
    public interface AccionResultado {
        boolean requierePromesa();
        String siguientePaso();
    }
    protected abstract AccionResultado crearAccion();
    public final AccionResultado preparar() { return crearAccion(); }
    public final String ejecutar() { return crearAccion().siguientePaso(); }
    public static final class CreadorPromesa extends CreadorAccion {
        protected AccionResultado crearAccion() { return new AccionPromesa(); }
    }
    public static final class CreadorSeguimiento extends CreadorAccion {
        protected AccionResultado crearAccion() { return new AccionSeguimiento(); }
    }
    public static final class AccionPromesa implements AccionResultado {
        public boolean requierePromesa() { return true; }
        public String siguientePaso() { return "Compromiso registrado. Revisar el pago en la fecha acordada."; }
    }
    public static final class AccionSeguimiento implements AccionResultado {
        public boolean requierePromesa() { return false; }
        public String siguientePaso() { return "Gestión registrada. Continuar el seguimiento de la cartera."; }
    }
}
