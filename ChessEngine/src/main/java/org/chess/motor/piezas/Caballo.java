package org.chess.motor.piezas;

import org.chess.motor.Alianza;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Movimiento.MovimientoAtaquePrincipal;
import org.chess.motor.tablero.Tablero;
import org.chess.motor.tablero.TableroUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.chess.motor.tablero.Movimiento.*;

public class Caballo extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS = { -17, -15, -10, -6, 6, 10, 15, 17};
    public Caballo(final Alianza alianzaPieza, final int posicionPieza) {
        super(TipoPieza.CABALLO, alianzaPieza, posicionPieza, true);
    }

    public Caballo(final Alianza alianzaPieza, final int posicionPieza, final boolean esPrimerMovimiento) {
        super(TipoPieza.CABALLO, alianzaPieza, posicionPieza, esPrimerMovimiento);
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for (final int offsetCandidatoActual: COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            if(esExclusionPrimeraColumna(this.posicionPieza, offsetCandidatoActual)
                || esExclusionSegundaColumna(this.posicionPieza, offsetCandidatoActual)
                || esExclusionSeptimaColumna(this.posicionPieza, offsetCandidatoActual)
                || esExclusionOctavaColumna(this.posicionPieza, offsetCandidatoActual)) {
                    continue;
            }
            final int coordenadaCandidataDestino = this.posicionPieza + offsetCandidatoActual;
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
                }
            }
        }
        return Collections.unmodifiableList(movimientosLegales);
    }

    @Override
    public Caballo moverPieza(final Movimiento movimiento) {
        return new Caballo(movimiento.getPiezaMovida().getAlianzaPieza(), movimiento.getCoordenadaDestino());
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    private static boolean esExclusionPrimeraColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.PRIMERA_COLUMNA.get(posicionActual) && ((offsetCandidato == -17 || offsetCandidato == -10 ||
                offsetCandidato == 6 || offsetCandidato == 15));
    }

    private static boolean esExclusionSegundaColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.SEGUNDA_COLUMNA.get(posicionActual) && ((offsetCandidato == -10 || offsetCandidato == 6));
    }

    private static boolean esExclusionSeptimaColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.SEPTIMA_COLUMNA.get(posicionActual) && ((offsetCandidato == -6 || offsetCandidato == 10));
    }

    private static boolean esExclusionOctavaColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.OCTAVA_COLUMNA.get(posicionActual)
                && (offsetCandidato == -15 || offsetCandidato == -6 || offsetCandidato == 10 || offsetCandidato == 17);
    }

}
