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

public class JugadorBlanco extends Jugador {
    public JugadorBlanco(final Tablero tablero,
                         final Collection<Movimiento> movimientosEstandarLegalesBlancas,
                         final Collection<Movimiento> movimientosEstardarLegalesNegras) {
        super(tablero, movimientosEstandarLegalesBlancas, movimientosEstardarLegalesNegras);
    }

    @Override
    public Collection<Pieza> getPiezasActivas() {
        return this.tablero.getPiezasBlancas();
    }

    @Override
    public Alianza getAlianza() {
        return Alianza.BLANCA;
    }

    @Override
    public Jugador getOponente() {
        return this.tablero.jugadorNegro();
    }

    @Override
    protected Collection<Movimiento> calcularEnroques
            (final Collection<Movimiento> legalesJugador, final Collection<Movimiento> legalesOponente) {

        final List<Movimiento> enroques = new ArrayList<>();
        if(this.reyJugador.esPrimerMovimiento() && !this.estaEnJaque()) {
            // Enroque blancas lado del Rey
            if(!this.tablero.getCasilla(61).esCasillaOcupada()
                    && !this.tablero.getCasilla(62).esCasillaOcupada()) {
                final Casilla casillaTorre = this.tablero.getCasilla(63);

                if(casillaTorre.esCasillaOcupada() && casillaTorre.getPieza().esPrimerMovimiento()) {
                    if(Jugador.calcularAtaquesEnCasilla(61, legalesOponente).isEmpty() &&
                        Jugador.calcularAtaquesEnCasilla(62, legalesOponente).isEmpty() &&
                        casillaTorre.getPieza().getTipoPieza().esTorre()) {
                        enroques.add(new Movimiento.MovimientoEnroqueLadoRey(this.tablero,
                                                                             this.reyJugador,
                                                                62,
                                                                               (Torre) casillaTorre.getPieza(),
                                                                               casillaTorre.getCoordenadaCasilla(),
                                                                61));
                    }
                }
            }

            if(!this.tablero.getCasilla(59).esCasillaOcupada() &&
                    !this.tablero.getCasilla(58).esCasillaOcupada() &&
                    !this.tablero.getCasilla(57).esCasillaOcupada()) {
                    final Casilla casillaTorre = this.tablero.getCasilla(56);
                    if(casillaTorre.esCasillaOcupada() && casillaTorre.getPieza().esPrimerMovimiento() &&
                        Jugador.calcularAtaquesEnCasilla(58, legalesOponente).isEmpty() &&
                        Jugador.calcularAtaquesEnCasilla(59, legalesOponente).isEmpty() &&
                        casillaTorre.getPieza().getTipoPieza().esTorre()) {
                        enroques.add(new Movimiento.MovimientoEnroqueLadoDama(this.tablero,
                                                                                this.reyJugador,
                                                                 58,
                                                                                (Torre)casillaTorre.getPieza(),
                                                                                casillaTorre.getCoordenadaCasilla(),
                                                                59));
                    }
            }
        }
        return Collections.unmodifiableList(enroques);
    }
}

