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

public class Alfil extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS =  { -9, -7, 7, 9};

    public Alfil(final Alianza alianzaPieza, final int posicionPieza) {
        super(TipoPieza.ALFIL, alianzaPieza, posicionPieza, true);
    }

    public Alfil(final Alianza alianzaPieza, final int posicionPieza, final boolean esPrimerMovimiento) {
        super(TipoPieza.ALFIL, alianzaPieza, posicionPieza, esPrimerMovimiento);
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for (final int offsetCandidatoActual: COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            int coordenadaCandidataDestino = this.posicionPieza;
            while (TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
                if (esExclusionPrimeraColumna(offsetCandidatoActual, coordenadaCandidataDestino)
                    || esExclusionOctavaColumna(offsetCandidatoActual, coordenadaCandidataDestino)) {
                    break;
                }
                coordenadaCandidataDestino += offsetCandidatoActual;
                if (TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
                    final Pieza piezaEnDestino = tablero.getCasilla(coordenadaCandidataDestino).getPieza();
                    if (piezaEnDestino == null) {
                        movimientosLegales.add(
                                new MovimientoPrincipal(
                                        tablero,
                                        this,
                                        coordenadaCandidataDestino));
                    } else {
                        final Alianza alianzaPiezaEnDestino = piezaEnDestino.getAlianzaPieza();
                        if(this.alianzaPieza != alianzaPiezaEnDestino) {
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
    public Alfil moverPieza(final Movimiento movimiento) {
        return new Alfil(movimiento.getPiezaMovida().getAlianzaPieza(), movimiento.getCoordenadaDestino());
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    private static boolean esExclusionPrimeraColumna(final int candidatoActual, final int coordenadaCandidataDestino) {
        return TableroUtils.PRIMERA_COLUMNA.get(coordenadaCandidataDestino)
                && (candidatoActual == -9 || candidatoActual == 7);
    }

    private static boolean esExclusionOctavaColumna(final int candidatoActual, final int coordenadaCandidataDestino) {
        return TableroUtils.OCTAVA_COLUMNA.get(coordenadaCandidataDestino)
                && (candidatoActual == -7 || candidatoActual == 9);
    }

}
