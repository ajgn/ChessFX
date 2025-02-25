package org.chess.motor.jugador.ia;

import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Tablero;

public interface EstrategiaMovimiento {
    Movimiento ejecutar(Tablero tablero);
}
