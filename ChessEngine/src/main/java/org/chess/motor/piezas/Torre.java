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

public class Torre extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS = {-8, -1, 1, 8};

    public Torre(final Alianza alianzaPieza, final int posicionPieza) {
        super(TipoPieza.TORRE, alianzaPieza, posicionPieza, true);
    }
    public Torre(final Alianza alianzaPieza, final int posicionPieza, boolean esPrimerMovimiento) {
        super(TipoPieza.TORRE, alianzaPieza, posicionPieza, esPrimerMovimiento);
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for(final int offsetCandidatoActual: COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            int coordenadaCandidataDestino = this.posicionPieza;
            while (TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
                if (esExclusionColumna(offsetCandidatoActual, coordenadaCandidataDestino)) {
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
    public Torre moverPieza(final Movimiento movimiento) {
        return new Torre(movimiento.getPiezaMovida().getAlianzaPieza(), movimiento.getCoordenadaDestino());
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    private static boolean esExclusionColumna(final int candidatoActual, final int coordenadaCandidataDestino) {
        return (TableroUtils.PRIMERA_COLUMNA.get(coordenadaCandidataDestino) && candidatoActual == -1)
            || (TableroUtils.OCTAVA_COLUMNA.get(coordenadaCandidataDestino) && candidatoActual == 1);
    }

}
