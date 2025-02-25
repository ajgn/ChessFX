package org.chess.motor.tablero;

import org.chess.motor.Alianza;
import org.chess.motor.jugador.Jugador;
import org.chess.motor.jugador.JugadorBlanco;
import org.chess.motor.jugador.JugadorNegro;
import org.chess.motor.piezas.*;

import java.util.*;

import static org.chess.motor.Alianza.BLANCA;
import static org.chess.motor.Alianza.NEGRA;

public class Tablero {
    private final List<Casilla> tableroJuego;
    private final Collection<Pieza> piezasBlancas;
    private final Collection<Pieza> piezasNegras;

    private final JugadorBlanco jugadorBlanco;
    private final JugadorNegro jugadorNegro;
    private final Jugador jugadorActual;

    private final Peon peonAlPaso;

    private Tablero(final Builder builder) {
        this.tableroJuego = crearTableroJuego(builder);
        this.piezasBlancas = calcularPiezasActivas(this.tableroJuego, BLANCA);
        this.piezasNegras = calcularPiezasActivas(this.tableroJuego, NEGRA);
        this.peonAlPaso = builder.peonAlPaso;
        final Collection<Movimiento> movimientosEstandarLegalesBlancas = calcularMovimientosLegales(this.piezasBlancas);
        final Collection<Movimiento> movimientosEstardarLegalesNegras = calcularMovimientosLegales(this.piezasNegras);

        this.jugadorBlanco = new JugadorBlanco(this, movimientosEstandarLegalesBlancas, movimientosEstardarLegalesNegras);
        this.jugadorNegro = new JugadorNegro(this, movimientosEstandarLegalesBlancas, movimientosEstardarLegalesNegras);
        this.jugadorActual = builder.creadorSiguienteMovimiento.seleccionarJugador(this.jugadorBlanco, this.jugadorNegro);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TableroUtils.NUM_CASILLAS; i++) {
            final String textoCasilla = this.tableroJuego.get(i).toString();
            sb.append(String.format("%3s", textoCasilla));
            if ((i + 1) % TableroUtils.NUM_CASILLAS_POR_FILA == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public Jugador jugadorBlanco() {
        return this.jugadorBlanco;
    }

    public Jugador jugadorNegro() {
        return this.jugadorNegro;
    }

    public Jugador jugadorActual() {
        return this.jugadorActual;
    }

    public Peon getPeonAlPaso() {
        return this.peonAlPaso;
    }
    public Collection<Pieza> getPiezasNegras() {
        return this.piezasNegras;
    }

    public Collection<Pieza> getPiezasBlancas() {
        return this.piezasBlancas;
    }

    private Collection<Movimiento> calcularMovimientosLegales(final Collection<Pieza> piezas) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for (final Pieza pieza : piezas) {
            movimientosLegales.addAll(pieza.calcularMovimientosLegales(this));
        }
        return Collections.unmodifiableList(movimientosLegales);
    }

    private Collection<Pieza> calcularPiezasActivas(final List<Casilla> tableroJuego, final Alianza alianza) {
        final List<Pieza> piezasActivas = new ArrayList<>();
        for(final Casilla casilla : tableroJuego) {
            if(casilla.esCasillaOcupada()) {
                final Pieza pieza = casilla.getPieza();
                if(pieza.getAlianzaPieza() == alianza) {
                    piezasActivas.add(pieza);
                }
            }
        }
        return Collections.unmodifiableList(piezasActivas);
    }

    public Casilla getCasilla(final int coordenadaCasilla) {
        return tableroJuego.get(coordenadaCasilla);
    }

    private static List<Casilla> crearTableroJuego(final Builder builder) {
        final Casilla[] casillas = new Casilla[TableroUtils.NUM_CASILLAS];
        for(int i = 0; i < TableroUtils.NUM_CASILLAS; i++) {
            casillas[i] = Casilla.crearCasilla(i, builder.configTablero.get(i));
        }
        return List.of(casillas);
    }

    public static Tablero crearTableroEstandar() {
        final Builder builder = new Builder();
        // Disposición negras
        builder.setPieza(new Torre(NEGRA, 0));
        builder.setPieza(new Caballo(NEGRA, 1));
        builder.setPieza(new Alfil(NEGRA, 2));
        builder.setPieza(new Dama(NEGRA, 3));
        builder.setPieza(new Rey(NEGRA, 4, true, true));
        builder.setPieza(new Alfil(NEGRA, 5));
        builder.setPieza(new Caballo(NEGRA, 6));
        builder.setPieza(new Torre(NEGRA, 7));
        builder.setPieza(new Peon(NEGRA, 8));
        builder.setPieza(new Peon(NEGRA, 9));
        builder.setPieza(new Peon(NEGRA, 10));
        builder.setPieza(new Peon(NEGRA, 11));
        builder.setPieza(new Peon(NEGRA, 12));
        builder.setPieza(new Peon(NEGRA, 13));
        builder.setPieza(new Peon(NEGRA, 14));
        builder.setPieza(new Peon(NEGRA, 15));
        // Disposición blancas
        builder.setPieza(new Peon(BLANCA, 48));
        builder.setPieza(new Peon(BLANCA, 49));
        builder.setPieza(new Peon(BLANCA, 50));
        builder.setPieza(new Peon(BLANCA, 51));
        builder.setPieza(new Peon(BLANCA, 52));
        builder.setPieza(new Peon(BLANCA, 53));
        builder.setPieza(new Peon(BLANCA, 54));
        builder.setPieza(new Peon(BLANCA, 55));
        builder.setPieza(new Torre(BLANCA, 56));
        builder.setPieza(new Caballo(BLANCA, 57));
        builder.setPieza(new Alfil(BLANCA, 58));
        builder.setPieza(new Dama(BLANCA, 59));
        builder.setPieza(new Rey(BLANCA, 60, true, true));
        builder.setPieza(new Alfil(BLANCA, 61));
        builder.setPieza(new Caballo(BLANCA, 62));
        builder.setPieza(new Torre(BLANCA, 63));
        // Mueven blancas
        builder.setCreadorMovimiento(BLANCA);
        // Construimos el tablero
        return builder.build();
    }

    public Iterable<Movimiento> getTodosMovimientosLegales() {
        Collection<Movimiento> movimientosLegales = new ArrayList<>();
        movimientosLegales.addAll(this.jugadorBlanco.getMovimientosLegales());
        movimientosLegales.addAll( this.jugadorNegro.getMovimientosLegales());
        return movimientosLegales;
    }

    public static class Builder {
        Map<Integer, Pieza> configTablero;
        Alianza creadorSiguienteMovimiento;
        Peon peonAlPaso;

        public Builder() {
            this.configTablero = new HashMap<>();
        }

        public void setPieza(final Pieza pieza) {
            this.configTablero.put(pieza.getPosicionPieza(), pieza);
        }

        public Builder setCreadorMovimiento(final Alianza creadorSiguienteMovimiento) {
            this.creadorSiguienteMovimiento = creadorSiguienteMovimiento;
            return this;
        }

        public Tablero build() {
            return new Tablero(this);
        }

        public void setPeonAlPaso(Peon peonAlPaso) {
            this.peonAlPaso = peonAlPaso;
        }
    }
}
