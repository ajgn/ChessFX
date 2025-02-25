package org.chess.motor.tablero;

import org.chess.motor.piezas.Pieza;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Casilla {
    protected final int coordenadaCasilla; // 0 to 63
    private static final Map<Integer, CasillaVacia> CACHE_CASILLAS_VACIAS = crearTodasCasillasVaciasPosibles();
    private static Map<Integer, CasillaVacia> crearTodasCasillasVaciasPosibles() {
        final Map<Integer, CasillaVacia> mapaCasillasVacias = new HashMap<>();
        for (int i = 0; i < TableroUtils.NUM_CASILLAS; i++) {
            mapaCasillasVacias.put(i, new CasillaVacia(i));
        }
        return Collections.unmodifiableMap(mapaCasillasVacias);
    }

    public static Casilla crearCasilla(final int coordenadaCasilla, final Pieza pieza) {
        return pieza != null ? new CasillaOcupada(coordenadaCasilla, pieza)
                : CACHE_CASILLAS_VACIAS.get(coordenadaCasilla);
    }

    private Casilla(final int coordenadaFicha) {
        this.coordenadaCasilla = coordenadaFicha;
    }
    public abstract boolean esCasillaOcupada();
    public abstract Pieza getPieza();

    public int getCoordenadaCasilla() {
        return this.coordenadaCasilla;
    }

    public static final class CasillaVacia extends Casilla {
        private CasillaVacia(final int coordenada) {
            super(coordenada);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean esCasillaOcupada() {
            return false;
        }

        @Override
        public Pieza getPieza() {
            return null;
        }
    }

    public static final class CasillaOcupada extends Casilla {
        private final Pieza piezaEnCasilla;

        private CasillaOcupada(int coordenadaFicha, Pieza piezaEnCasilla) {
            super(coordenadaFicha);
            this.piezaEnCasilla = piezaEnCasilla;
        }

        @Override
        public String toString() {
            return this.getPieza().getAlianzaPieza().esNegra()
                    ? this.getPieza().toString().toLowerCase()
                    : this.getPieza().toString();
        }

        @Override
        public boolean esCasillaOcupada() {
            return true;
        }

        @Override
        public Pieza getPieza() {
            return this.piezaEnCasilla;
        }
    }

}
