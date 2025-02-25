package org.chess.motor.piezas;

import org.chess.motor.Alianza;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Tablero;

import java.util.Collection;

public abstract class Pieza {

    protected final TipoPieza tipoPieza;
    protected final int posicionPieza;
    protected final Alianza alianzaPieza;
    protected final boolean esPrimerMovimiento;
    private final int codigoHashCacheado;

    public Pieza(final TipoPieza tipoPieza,
                 final Alianza alianzaPieza,
                 final int posicionPieza,
                 final boolean esPrimerMovimiento) {
        this.tipoPieza = tipoPieza;
        this.posicionPieza = posicionPieza;
        this.alianzaPieza = alianzaPieza;
        this.esPrimerMovimiento = esPrimerMovimiento;
        this.codigoHashCacheado = computeHashCode();
    }

    private int computeHashCode() {
        int result = tipoPieza.hashCode();
        result = 31 * result + alianzaPieza.hashCode();
        result = 31 * result + posicionPieza;
        result = 31 * result + (esPrimerMovimiento ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Pieza)) {
            return false;
        }
        final Pieza otraPieza = (Pieza) otro;
        return posicionPieza == otraPieza.getPosicionPieza() && tipoPieza == otraPieza.getTipoPieza() &&
                alianzaPieza == otraPieza.getAlianzaPieza() && esPrimerMovimiento == otraPieza.esPrimerMovimiento;
    }

    @Override
    public int hashCode() {
        return this.codigoHashCacheado;
    }

    public int getPosicionPieza() {
        return this.posicionPieza;
    }

    public Alianza getAlianzaPieza() {
        return this.alianzaPieza;
    }

    public boolean esPrimerMovimiento() {
        return this.esPrimerMovimiento;
    }

    public TipoPieza getTipoPieza() {
        return this.tipoPieza;
    }

    public int getValorPieza() {
        return this.tipoPieza.getValorPieza();
    }

    public String getCodigoPieza() {
        return getAlianzaPieza().toString() + getTipoPieza().toString();
    }

    public abstract Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero);

    public abstract Pieza moverPieza(Movimiento movimiento);

    public enum TipoPieza {

        PEON(100, "P") {
            @Override
            public boolean esRey() {
                return false;
            }

            @Override
            public boolean esTorre() {
                return false;
            }
        },
        CABALLO(300,"N") {
            @Override
            public boolean esRey() {
                return false;
            }

            @Override
            public boolean esTorre() {
                return false;
            }
        },
        ALFIL(300,"B") {
            @Override
            public boolean esRey() {
                return false;
            }

            @Override
            public boolean esTorre() {
                return false;
            }
        },
        TORRE(500, "R") {
            @Override
            public boolean esRey() {
                return false;
            }

            @Override
            public boolean esTorre() {
                return true;
            }
        },
        DAMA(900, "Q") {
            @Override
            public boolean esRey() {
                return false;
            }

            @Override
            public boolean esTorre() {
                return false;
            }
        },
        REY(10000, "K") {

            @Override
            public boolean esRey() {
                return true;
            }

            @Override
            public boolean esTorre() {
                return false;
            }
        };

        private final String nombrePieza;
        private final int valorPieza;

        TipoPieza( final int valorPieza, final String nombrePieza) {
            this.nombrePieza = nombrePieza;
            this.valorPieza = valorPieza;
        }

        @Override
        public String toString() {
            return this.nombrePieza;
        }

        public int getValorPieza() {
            return this.valorPieza;
        }

        public abstract boolean esRey();
        public abstract boolean esTorre();
    }

}
