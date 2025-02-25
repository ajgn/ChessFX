package org.chess.motor.tablero;

public enum EstadoMovimiento {
    HECHO {
        @Override
        public boolean estaHecho() {
            return true;
        }
    },
    MOVIMIENTO_ILEGAL {

        @Override
        public boolean estaHecho() {
            return false;
        }
    }, DEJA_JUGADOR_EN_JAQUE {
        @Override
        public boolean estaHecho() {
            return false;
        }
    };
    public abstract boolean  estaHecho();
}

