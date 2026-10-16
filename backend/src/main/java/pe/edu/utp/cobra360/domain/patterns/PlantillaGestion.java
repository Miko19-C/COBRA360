package pe.edu.utp.cobra360.domain.patterns;

import java.util.ArrayList;
import java.util.List;
/** PROTOTYPE: copia profunda de la colección mutable; nunca clona registros persistidos ni sus IDs. */
public final class PlantillaGestion implements Cloneable {
    private final String nombre;
    private final List<String> pasos;
    public PlantillaGestion(String nombre, List<String> pasos) {
        this.nombre=nombre; this.pasos=new ArrayList<>(pasos);
    }
    public String getNombre() { return nombre; }
    public List<String> getPasos() { return pasos; }
    @Override public PlantillaGestion clone() { return new PlantillaGestion(nombre, pasos); }
}
