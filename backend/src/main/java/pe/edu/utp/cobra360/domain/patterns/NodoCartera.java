package pe.edu.utp.cobra360.domain.patterns;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
/** COMPOSITE: mismo contrato para una deuda hoja y una agrupación de cartera. */
public interface NodoCartera {
    String nombre();
    BigDecimal saldo();
    int cantidad();
    List<NodoCartera> hijos();
    record DeudaHoja(String nombre, BigDecimal saldo) implements NodoCartera {
        public int cantidad(){return 1;}
        public List<NodoCartera> hijos(){return List.of();}
    }
    final class GrupoCartera implements NodoCartera {
        private final String nombre;
        private final List<NodoCartera> hijos=new ArrayList<>();
        public GrupoCartera(String nombre){this.nombre=nombre;}
        public GrupoCartera agregar(NodoCartera nodo){hijos.add(nodo);return this;}
        public String nombre(){return nombre;}
        public BigDecimal saldo(){return hijos.stream().map(NodoCartera::saldo).reduce(BigDecimal.ZERO,BigDecimal::add);}
        public int cantidad(){return hijos.stream().mapToInt(NodoCartera::cantidad).sum();}
        public List<NodoCartera> hijos(){return List.copyOf(hijos);}
    }
}
