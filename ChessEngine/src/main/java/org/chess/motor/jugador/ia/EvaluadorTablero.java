package org.chess.motor.jugador.ia;

import org.chess.motor.tablero.Tablero;

public interface EvaluadorTablero {
    int evaluar(Tablero tablero, int profundidad);
}
