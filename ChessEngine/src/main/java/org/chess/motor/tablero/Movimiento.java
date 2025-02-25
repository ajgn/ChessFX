package org.chess.motor.tablero;

import org.chess.motor.piezas.Peon;
import org.chess.motor.piezas.Pieza;
import org.chess.motor.piezas.Torre;

import static org.chess.motor.tablero.Tablero.*;

public abstract class Movimiento {
    protected final Tablero tablero;
    protected final Pieza piezaMovida;
    protected final int coordenadaDestino;
    protected final boolean esPrimerMovimiento;

    public static final Movimiento MOVIMIENTO_NULO = new MovimientoNulo();

    private Movimiento(final Tablero tablero, final Pieza piezaMovida, final int coordenadaDestino) {
        this.tablero = tablero;
        this.piezaMovida = piezaMovida;
        this.coordenadaDestino = coordenadaDestino;
        this.esPrimerMovimiento = piezaMovida.esPrimerMovimiento();
    }

    private Movimiento(final Tablero tablero, final int coordenadaDestino) {
        this.tablero = tablero;
        this.coordenadaDestino = coordenadaDestino;
        this.piezaMovida = null;
        this.esPrimerMovimiento = false;
    }

    @Override
    public int hashCode() {
        final int primo = 31;
        int resultado = 1;
        resultado = primo * resultado + this.coordenadaDestino;
        resultado = primo * resultado + this.piezaMovida.hashCode();
        resultado = primo * resultado + this.piezaMovida.getPosicionPieza();
        return resultado;
    }

    @Override
    public boolean equals(final Object otro) {
        if(this == otro) {
            return true;
        }
        if(!(otro instanceof final Movimiento otroMovimiento)) {
            return false;
        }
        return  getCoordenadaActual() == otroMovimiento.getCoordenadaActual()
                && getCoordenadaDestino() == otroMovimiento.getCoordenadaDestino()
                && getPiezaMovida().equals(otroMovimiento.getPiezaMovida());
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    @Override
    public String toString() {
        if (piezaMovida != null) {
            return piezaMovida.getTipoPieza().toString() + TableroUtils.getPosicionEnCoordenada(this.coordenadaDestino);
        }
        return "";
    }

    public int getCoordenadaActual() {
        return this.getPiezaMovida().getPosicionPieza();
    }

    public int getCoordenadaDestino() {
        return this.coordenadaDestino;
    }

    public Pieza getPiezaMovida() {
        return this.piezaMovida;
    }

    public boolean esAtaque() {
        return false;
    }

    public boolean esEnroque() {
        return false;
    }

    public Pieza getPiezaAtacada() {
        return null;
    }

    public Tablero ejecutar() {
        final Builder builder = new Builder();
        for (final Pieza pieza : this.tablero.jugadorActual().getPiezasActivas()) {
            if(!this.piezaMovida.equals(pieza)) {
                 builder.setPieza(pieza);
            }
        }
        for (final Pieza pieza : this.tablero.jugadorActual().getOponente().getPiezasActivas()) {
            builder.setPieza(pieza);
        }
        // mover la pieza marcada
        builder.setPieza(this.piezaMovida.moverPieza(this));
        builder.setCreadorMovimiento(this.tablero.jugadorActual().getOponente().getAlianza());
        return builder.build();
    }

    public static final class MovimientoPrincipal extends Movimiento {
        public MovimientoPrincipal(final Tablero tablero, final Pieza piezaMovida, final int coordenadaDestino) {
            super(tablero, piezaMovida, coordenadaDestino);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoPrincipal && super.equals(otro);
        }

    }

    public static class MovimientoAtaquePrincipal extends MovimientoAtaque {
        public MovimientoAtaquePrincipal(Tablero tablero,
                                         Pieza piezaMovida,
                                         int coordenadaDestino,
                                         Pieza piezaAtacada) {
            super(tablero, piezaMovida, coordenadaDestino, piezaAtacada);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoAtaquePrincipal
                    && super.equals(otro);
        }

        @Override
        public String toString() {
            return piezaMovida.getTipoPieza() + "x" + TableroUtils.getPosicionEnCoordenada(this.coordenadaDestino);
        }
    }

    public static class MovimientoAtaque extends Movimiento {
        private final Pieza piezaAtacada;

        public MovimientoAtaque(final Tablero tablero, final Pieza piezaMovida, final int coordenadaDestino,
                                final Pieza piezaAtacada) {
            super(tablero, piezaMovida, coordenadaDestino);
            this.piezaAtacada = piezaAtacada;
        }

        @Override
        public int hashCode() {
            return this.piezaAtacada.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object otro) {
            if(this == otro) {
                return true;
            }
            if(!(otro instanceof final MovimientoAtaque otroMovimientoAtaque)) {
                return false;
            }
            return super.equals(otroMovimientoAtaque) && getPiezaAtacada().equals(otroMovimientoAtaque.getPiezaAtacada());
        }

        @Override
        public boolean esAtaque() {
            return true;
        }

        @Override
        public Pieza getPiezaAtacada() {
            return this.piezaAtacada;
        }
    }

    public static class MovimientoPeon extends Movimiento {
        public MovimientoPeon(final Tablero tablero, final Pieza piezaMovida, final int coordenadaDestino) {
            super(tablero, piezaMovida, coordenadaDestino);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoPeon && super.equals(otro);
        }

        @Override
        public String toString() {
            return TableroUtils.getPosicionEnCoordenada(this.coordenadaDestino);
        }
    }

    public static class MovimientoAtaquePeon extends MovimientoAtaque {
        public MovimientoAtaquePeon(final Tablero tablero, final Pieza piezaMovida,
                                    final int coordenadaDestino, final Pieza piezaAtacada) {
            super(tablero, piezaMovida, coordenadaDestino, piezaAtacada);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoAtaquePeon && super.equals(otro);
        }

        @Override
        public String toString() {
            return TableroUtils.getPosicionEnCoordenada(this.piezaMovida.getPosicionPieza())
                    + "x" + TableroUtils.getPosicionEnCoordenada(this.coordenadaDestino);
        }

    }

    public static final class MovimientoAtaqueAlPaso extends MovimientoAtaque {
        public MovimientoAtaqueAlPaso(
                final Tablero tablero,
                final Pieza piezaMovida,
                final int coordenadaDestino,
                final Pieza piezaAtacada) {
            super(tablero, piezaMovida, coordenadaDestino, piezaAtacada);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoAtaqueAlPaso && super.equals(otro);
        }

        @Override
        public Tablero ejecutar() {
            final Builder builder = new Builder();
            for (final Pieza pieza : this.tablero.jugadorActual().getPiezasActivas()) {
                if (!this.piezaMovida.equals(pieza)) {
                    builder.setPieza(pieza);
                }
            }
            for (final Pieza pieza : this.tablero.jugadorActual().getOponente().getPiezasActivas()) {
                if(!pieza.equals(this.getPiezaAtacada())) {
                    builder.setPieza(pieza);
                }
            }
            builder.setPieza(this.piezaMovida.moverPieza(this));
            builder.setCreadorMovimiento(this.tablero.jugadorActual().getOponente().getAlianza());
            return builder.build();
        }
    }

    public static class PromocionPeon extends MovimientoPeon {
        final Movimiento movimientoDecorado;
        final Peon peonPromocionado;

        public PromocionPeon(final Movimiento movimientoDecorado) {
            super(
                    movimientoDecorado.getTablero(),
                    movimientoDecorado.getPiezaMovida(),
                    movimientoDecorado.getCoordenadaDestino());
            this.movimientoDecorado = movimientoDecorado;
            this.peonPromocionado = (Peon) movimientoDecorado.getPiezaMovida();
        }

        @Override
        public int hashCode() {
            return movimientoDecorado.hashCode() + (31 * peonPromocionado.hashCode());
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof PromocionPeon && super.equals(otro);
        }

        @Override
        public Tablero ejecutar() {
            final Tablero tableroPeonMovido = this.movimientoDecorado.ejecutar();
            final Tablero.Builder builder = new Builder();
            for (final Pieza pieza : tableroPeonMovido.jugadorActual().getPiezasActivas()) {
                if (!this.peonPromocionado.equals(pieza)) {
                    builder.setPieza(pieza);
                }
            }
            for (final Pieza pieza : tableroPeonMovido.jugadorActual().getOponente().getPiezasActivas()) {
                builder.setPieza(pieza);
            }
            builder.setPieza(this.peonPromocionado.getPiezaPromocionada().moverPieza(this));
            builder.setCreadorMovimiento(tableroPeonMovido.jugadorActual().getAlianza());
            return builder.build();
        }

        @Override
        public boolean esAtaque() {
            return this.movimientoDecorado.esAtaque();
        }

        @Override
        public Pieza getPiezaAtacada() {
            return this.movimientoDecorado.getPiezaAtacada();
        }

        @Override
        public String toString() {
            return "";
        }
    }

    public static final class MovimientoSaltoPeon extends Movimiento {
        public MovimientoSaltoPeon(final Tablero tablero, final Pieza piezaMovida, final int coordenadaDestino) {
            super(tablero, piezaMovida, coordenadaDestino);
        }
        @Override
        public Tablero ejecutar() {
            final Builder builder = new Builder();
            for(final Pieza pieza : this.tablero.jugadorActual().getPiezasActivas()) {
                if(!this.piezaMovida.equals(pieza)) {
                    builder.setPieza(pieza);
                }
            }
            for (final Pieza pieza : this.tablero.jugadorActual().getOponente().getPiezasActivas()) {
                builder.setPieza(pieza);
            }
            final Peon peonMovido = (Peon) this.piezaMovida.moverPieza(this);
            builder.setPieza(peonMovido);
            builder.setPeonAlPaso(peonMovido);
            builder.setCreadorMovimiento(this.tablero.jugadorActual().getOponente().getAlianza());
            return builder.build();
        }
    }

    static abstract class MovimientoEnroque extends Movimiento {
        protected final Torre torreEnroque;
        protected final int inicioTorreEnroque;
        protected final int destinoTorreEnroque;

        public MovimientoEnroque(final Tablero tablero,
                                 final Pieza piezaMovida,
                                 final int coordenadaDestino,
                                 final Torre torreEnroque,
                                 final int inicioTorreEnroque,
                                 final int destinoTorreEnroque) {
            super(tablero, piezaMovida, coordenadaDestino);
            this.torreEnroque = torreEnroque;
            this.inicioTorreEnroque = inicioTorreEnroque;
            this.destinoTorreEnroque = destinoTorreEnroque;
        }

        public Torre getTorreEnroque() {
            return this.torreEnroque;
        }

        public boolean esMovimientoEnroque() {
            return true;
        }

        public Tablero ejecutar() {
            final Builder builder = new Builder();
            for(final Pieza pieza : this.tablero.jugadorActual().getPiezasActivas()) {
                if(!this.piezaMovida.equals(pieza) && !this.torreEnroque.equals(pieza)) {
                    builder.setPieza(pieza);
                }
            }
            for (final Pieza pieza : this.tablero.jugadorActual().getOponente().getPiezasActivas()) {
                builder.setPieza(pieza);
            }
            builder.setPieza(this.piezaMovida.moverPieza(this));
            // TODO investigar primer movimiento en piezas normales
            builder.setPieza(new Torre(this.torreEnroque.getAlianzaPieza(), this.destinoTorreEnroque));
            builder.setCreadorMovimiento((this.tablero.jugadorActual().getOponente().getAlianza()));
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int primo = 31;
            int resultado = super.hashCode();
            resultado = primo * resultado + this.torreEnroque.hashCode();
            resultado = primo * resultado + this.destinoTorreEnroque;
            return resultado;
        }

        @Override
        public boolean equals(final Object otro) {
            if (this == otro) {
                return true;
            }
            if (!(otro instanceof final MovimientoEnroque otroMovimientoEnroque)) {
                return false;
            }
            return super.equals(otroMovimientoEnroque)
                    && this.torreEnroque.equals(otroMovimientoEnroque.getTorreEnroque());
        }

    }

    public static final class MovimientoEnroqueLadoRey extends MovimientoEnroque {
        public MovimientoEnroqueLadoRey(final Tablero tablero,
                                        final Pieza piezaMovida,
                                        final int coordenadaDestino,
                                        final Torre torreEnroque,
                                        final int inicioTorreEnroque,
                                        final int destinoTorreEnroque) {
            super(tablero, piezaMovida, coordenadaDestino, torreEnroque, inicioTorreEnroque, destinoTorreEnroque);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoEnroqueLadoRey && super.equals(otro);

        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class MovimientoEnroqueLadoDama extends MovimientoEnroque {
        public MovimientoEnroqueLadoDama(final Tablero tablero,
                                         final Pieza piezaMovida,
                                         final int coordenadaDestino,
                                         final Torre torreEnroque,
                                         final int inicioTorreEnroque,
                                         final int destinoTorreEnroque) {
            super(tablero, piezaMovida, coordenadaDestino, torreEnroque, inicioTorreEnroque, destinoTorreEnroque);
        }

        @Override
        public boolean equals(final Object otro) {
            return this == otro || otro instanceof MovimientoEnroqueLadoDama && super.equals(otro);

        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static final class MovimientoNulo extends Movimiento {
        public MovimientoNulo() {
            super(null,65);
        }

        @Override
        public Tablero ejecutar() {
            throw new RuntimeException("¡no se puede ejecutar movimiento nulo!");
        }

        @Override
        public int getCoordenadaActual() {
            return -1;
        }
    }

    public static class FabricaMovimiento {
        private FabricaMovimiento() {
            throw new RuntimeException("¡No instanciable!");
        }
        public static Movimiento crearMovimiento(final Tablero tablero,
                                                 final int coordenadaActual,
                                                 final int coordenadaDestino) {
            for (final Movimiento movimiento : tablero.getTodosMovimientosLegales()) {
                if(movimiento.getCoordenadaActual() == coordenadaActual &&
                    movimiento.getCoordenadaDestino() == coordenadaDestino) {
                    return movimiento;
                }
            }
            return MOVIMIENTO_NULO;
        }
    }


}
