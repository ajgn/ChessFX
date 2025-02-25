package org.chess.motor.jugador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.chess.motor.Alianza;
import org.chess.motor.piezas.Pieza;
import org.chess.motor.piezas.Rey;
import org.chess.motor.tablero.EstadoMovimiento;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.MovimientoTransicion;
import org.chess.motor.tablero.Tablero;

public abstract class Jugador {

    protected final Tablero tablero;
    protected final Rey reyJugador;
    protected final Collection<Movimiento> movimientosLegales;
    private final boolean estaEnJaque;

    Jugador(final Tablero tablero,
           final Collection<Movimiento> movimientosLegales,
           final Collection<Movimiento> movimientosOponente) {
        this.tablero = tablero;
        this.reyJugador = establecerRey();
        this.movimientosLegales = new ArrayList<>();
        this.movimientosLegales.addAll(movimientosLegales);
        this.movimientosLegales.addAll(calcularEnroques(movimientosLegales, movimientosOponente));
        this.estaEnJaque =
                !Jugador.calcularAtaquesEnCasilla(this.reyJugador.getPosicionPieza(), movimientosOponente).isEmpty();
    }

    public Rey getReyJugador() {
        return this.reyJugador;
    }

    public Collection<Movimiento> getMovimientosLegales() {
        return this.movimientosLegales;
    }

    protected static Collection<Movimiento> calcularAtaquesEnCasilla
            (int posicionPieza, Collection<Movimiento> movimientos) {
        final List<Movimiento> movimientosAtaque = new ArrayList<>();
        for (final Movimiento movimiento : movimientos) {
            if (posicionPieza == movimiento.getCoordenadaDestino()) {
                movimientosAtaque.add(movimiento);
            }
        }
        return Collections.unmodifiableList(movimientosAtaque);
    }

    private Rey establecerRey() {
        for (final Pieza pieza : getPiezasActivas()) {
            if(pieza.getTipoPieza().esRey()) {
                return (Rey) pieza;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!!");
    }

    public boolean esMovimientoLegal(final Movimiento movimiento) {
        return this.movimientosLegales.contains(movimiento);
    }

    public boolean estaEnJaque() {
        return this.estaEnJaque;
    }

    public boolean esJaqueMate() {
        return this.estaEnJaque && !tieneMovimientosEscape();
    }

    protected boolean tieneMovimientosEscape() {
        for (final Movimiento movimiento : this.movimientosLegales) {
            final MovimientoTransicion transicion = realizarMovimiento(movimiento);
            if (transicion.getEstadoMovimiento().estaHecho()) {
                return true;
            }
        }
        return false;
    }

    public boolean esReyAhogado() {
        return !this.estaEnJaque && !tieneMovimientosEscape();
    }

    public boolean esCapazDeLadoRey() {
        return this.reyJugador.esCapazDeLadoRey();
    }

    public boolean esCapazDeLadoDama() {
        return this.reyJugador.esCapazDeLadoDama();
    }

    public boolean estaEnrocado() {
        return false;
    }

    public MovimientoTransicion realizarMovimiento(final Movimiento movimiento) {

        if(!esMovimientoLegal(movimiento)) {
            return new MovimientoTransicion(this.tablero, movimiento, EstadoMovimiento.MOVIMIENTO_ILEGAL);
        }

        final Tablero tableroTransicion = movimiento.ejecutar();

        final Collection<Movimiento> ataquesRey = Jugador
                .calcularAtaquesEnCasilla(
                        tableroTransicion
                                .jugadorActual()
                                .getOponente()
                                .getReyJugador()
                                .getPosicionPieza(),
                tableroTransicion.jugadorActual().getMovimientosLegales());

        if(!ataquesRey.isEmpty()) {
            return new MovimientoTransicion(this.tablero, movimiento, EstadoMovimiento.DEJA_JUGADOR_EN_JAQUE);
        }

        return new MovimientoTransicion(tableroTransicion, movimiento, EstadoMovimiento.HECHO);
    }

    public String toString() {
        return (getAlianza().toString().equals("W") ? "Blancas" : "Negras");
    }

    public abstract Collection<Pieza> getPiezasActivas();
    public abstract Alianza getAlianza();
    public abstract Jugador getOponente();
    protected abstract Collection<Movimiento> calcularEnroques
            (Collection<Movimiento> legalesJugador, Collection<Movimiento> legalesOponente);

}
