package org.chess.motor.jugador;

import org.chess.motor.Alianza;
import org.chess.motor.piezas.Pieza;
import org.chess.motor.piezas.Torre;
import org.chess.motor.tablero.Casilla;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Tablero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JugadorNegro extends Jugador {
    public JugadorNegro( final Tablero tablero,
                        final Collection<Movimiento> movimientosEstandarLegalesBlancas,
                        final Collection<Movimiento> movimientosEstardarLegalesNegras) {
        super(tablero, movimientosEstardarLegalesNegras, movimientosEstandarLegalesBlancas);
    }

    @Override
    public Collection<Pieza> getPiezasActivas() {
        return this.tablero.getPiezasNegras();
    }

    @Override
    public Alianza getAlianza() {
        return Alianza.NEGRA;
    }

    @Override
    public Jugador getOponente() {
        return this.tablero.jugadorBlanco();
    }

    @Override
    protected Collection<Movimiento> calcularEnroques
            (final Collection<Movimiento> legalesJugador, final Collection<Movimiento> legalesOponente) {

        final List<Movimiento> enroques = new ArrayList<>();

        if(this.reyJugador.esPrimerMovimiento() && !this.estaEnJaque()) {
            // Enroque negras lado del Rey
            if(!this.tablero.getCasilla(5).esCasillaOcupada()
                    && !this.tablero.getCasilla(6).esCasillaOcupada()) {
                final Casilla casillaTorre = this.tablero.getCasilla(7);

                if(casillaTorre.esCasillaOcupada() && casillaTorre.getPieza().esPrimerMovimiento()) {
                    if(Jugador.calcularAtaquesEnCasilla(5, legalesOponente).isEmpty() &&
                            Jugador.calcularAtaquesEnCasilla(6, legalesOponente).isEmpty() &&
                            casillaTorre.getPieza().getTipoPieza().esTorre()) {
                        enroques.add(new Movimiento.MovimientoEnroqueLadoRey(this.tablero,
                                                                                this.reyJugador,
                                                                                6,
                                                                                (Torre) casillaTorre.getPieza(),
                                                                                casillaTorre.getCoordenadaCasilla(),
                                                                                5));
                    }
                }
            }

            if(!this.tablero.getCasilla(1).esCasillaOcupada() &&
                    !this.tablero.getCasilla(2).esCasillaOcupada() &&
                    !this.tablero.getCasilla(3).esCasillaOcupada()) {
                final Casilla casillaTorre = this.tablero.getCasilla(0);
                if(casillaTorre.esCasillaOcupada() && casillaTorre.getPieza().esPrimerMovimiento() &&
                Jugador.calcularAtaquesEnCasilla(2, legalesOponente).isEmpty() &&
                Jugador.calcularAtaquesEnCasilla(3, legalesOponente).isEmpty() &&
                        casillaTorre.getPieza().getTipoPieza().esTorre()) {
                    enroques.add(new Movimiento.MovimientoEnroqueLadoDama(this.tablero,
                                                                           this.reyJugador,
                                                                            2,
                                                                            (Torre) casillaTorre.getPieza(),
                                                                            casillaTorre.getCoordenadaCasilla(),
                                                                            3));
                }
            }
        }
        return Collections.unmodifiableList(enroques);
    }
}
