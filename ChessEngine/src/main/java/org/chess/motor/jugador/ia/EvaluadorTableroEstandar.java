package org.chess.motor.jugador.ia;

import org.chess.motor.jugador.Jugador;
import org.chess.motor.piezas.Pieza;
import org.chess.motor.tablero.Tablero;

public final class EvaluadorTableroEstandar implements EvaluadorTablero {

    private static final int CHECK_BONUS = 50;
    private static final int BONUS_JAQUE_MATE = 10000;
    private static final int BONUS_PROFUNDIDAD = 100;
    private static final int BONUS_ENROCADO = 60;

    @Override
    public int evaluar(final Tablero tablero, final int profundidad) {
        return puntuarJugador(tablero, tablero.jugadorBlanco(), profundidad)
                - puntuarJugador(tablero, tablero.jugadorNegro(), profundidad);
    }

    private int puntuarJugador(final Tablero tablero, final Jugador jugador, final int profundidad) {
        return valorPieza(jugador)
                + movilidad(jugador)
                + jaque(jugador)
                + jaqueMate(jugador, profundidad)
                + enrocado(jugador);
    }

    private static int enrocado(Jugador jugador) {
        return jugador.estaEnrocado() ? BONUS_ENROCADO : 0;
    }

    private static int jaqueMate(Jugador jugador, int profundidad) {
        return jugador.getOponente().esJaqueMate() ? BONUS_JAQUE_MATE * bonusProfundidad(profundidad) : 0;
    }

    private static int bonusProfundidad(int profundidad) {
        return profundidad == 0 ? 1 : BONUS_PROFUNDIDAD * profundidad;
    }

    private static int jaque(final Jugador jugador) {
        return jugador.getOponente().estaEnJaque() ? CHECK_BONUS : 0;
    }

    private static int movilidad(final Jugador jugador) {
        return jugador.getMovimientosLegales().size();
    }

    private static int valorPieza(final Jugador jugador) {
        int valorPuntuacionPieza = 0;
        for (final Pieza pieza : jugador.getPiezasActivas()) {
            valorPuntuacionPieza += pieza.getValorPieza();
        }
        return valorPuntuacionPieza;
    }
}
