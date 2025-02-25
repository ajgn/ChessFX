package org.chess.motor;

import org.chess.motor.jugador.Jugador;
import org.chess.motor.jugador.JugadorBlanco;
import org.chess.motor.jugador.JugadorNegro;
import org.chess.motor.tablero.TableroUtils;

public enum Alianza {
    BLANCA {
        @Override
        public int getDireccion() {
            return -1;
        }

        @Override
        public int getDireccionOpuesta() {
            return 1;
        }

        @Override
        public boolean esBlanca() {
            return true;
        }

        @Override
        public boolean esNegra() {
            return false;
        }

        @Override
        public boolean esCasillaPromocionPeon(int posicion) {
            return TableroUtils.PRIMERA_FILA.get(posicion);
        }

        @Override
        public Jugador seleccionarJugador(final JugadorBlanco jugadorBlanco, final JugadorNegro jugadorNegro) {
            return jugadorBlanco;
        }
    },
    NEGRA {
        @Override
        public int getDireccion() {
            return 1;
        }

        @Override
        public int getDireccionOpuesta() {
            return -1;
        }

        @Override
        public boolean esBlanca() {
            return false;
        }

        @Override
        public boolean esNegra() {
            return true;
        }

        @Override
        public boolean esCasillaPromocionPeon(int posicion) {
            return TableroUtils.OCTAVA_FILA.get(posicion);
        }

        @Override
        public Jugador seleccionarJugador(final JugadorBlanco jugadorBlanco, final JugadorNegro jugadorNegro) {
            return jugadorNegro;
        }
    };

    public abstract int getDireccion();
    public abstract  int getDireccionOpuesta();
    public abstract boolean esBlanca();
    public abstract boolean esNegra();
    public abstract boolean esCasillaPromocionPeon(int posicion);
    public abstract Jugador seleccionarJugador(JugadorBlanco jugadorBlanco, JugadorNegro jugadorNegro);
    public String toString() {
        return this.esNegra() ? "B" : "W";
    }


}
