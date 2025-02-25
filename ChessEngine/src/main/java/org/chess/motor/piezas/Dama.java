package org.chess.motor.piezas;

import org.chess.motor.Alianza;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Movimiento.MovimientoPrincipal;
import org.chess.motor.tablero.Movimiento.MovimientoAtaquePrincipal;
import org.chess.motor.tablero.Tablero;
import org.chess.motor.tablero.TableroUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Dama extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS =  { -9, -8, -7, -1, 1, 7, 8, 9};

    public Dama(final Alianza alianzaPieza, final int posicionPieza) {
        super(TipoPieza.DAMA, alianzaPieza, posicionPieza, true);
    }

    public Dama(final Alianza alianzaPieza, final int posicionPieza, final boolean esPrimerMovimiento) {
        super(TipoPieza.DAMA, alianzaPieza, posicionPieza, esPrimerMovimiento);
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for(final int offsetCandidatoActual: COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            int coordenadaCandidataDestino = this.posicionPieza;
            while(true) {
                if(esExclusionPrimeraColumna(offsetCandidatoActual, coordenadaCandidataDestino)
                        || esExclusionOctavaColumna(offsetCandidatoActual, coordenadaCandidataDestino)) {
                    break;
                }
                coordenadaCandidataDestino += offsetCandidatoActual;
                if(!TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
                    break;
                } else {
                    final Pieza piezaEnDestino = tablero.getCasilla(coordenadaCandidataDestino).getPieza();
                    if(piezaEnDestino == null) {
                        movimientosLegales.add(
                                new MovimientoPrincipal(
                                        tablero,
                                        this,
                                        coordenadaCandidataDestino));
                    } else {
                        final Alianza alianzaPiezaEnDestino = piezaEnDestino.getAlianzaPieza();
                        if (this.alianzaPieza != alianzaPiezaEnDestino) {
                            movimientosLegales.add(
                                    new MovimientoAtaquePrincipal(
                                       tablero,
                                       this,
                                       coordenadaCandidataDestino,
                                       piezaEnDestino));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(movimientosLegales);
    }

    @Override
    public Dama moverPieza(final Movimiento movimiento) {
        return new Dama(movimiento.getPiezaMovida().getAlianzaPieza(), movimiento.getCoordenadaDestino());
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    private static boolean esExclusionPrimeraColumna(final int posicionActual, final int posicionCandidata) {
        return TableroUtils.PRIMERA_COLUMNA.get(posicionCandidata)
                && (posicionActual == -9 || posicionActual == -1 || posicionActual == 7);
    }

    private static boolean esExclusionOctavaColumna(final int posicionActual, final int posicionCandidata) {
        return TableroUtils.OCTAVA_COLUMNA.get(posicionCandidata)
                && (posicionActual == -7 || posicionActual == 1 || posicionActual == 9);
    }
}
