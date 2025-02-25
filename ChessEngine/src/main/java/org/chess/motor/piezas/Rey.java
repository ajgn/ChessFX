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

public class Rey extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean estaEnrocado;
    private final boolean capazEnroqueDeLadoRey;
    private final boolean capazEnroqueDeLadoDama;

    public Rey(final Alianza alianzaPieza,
               final int posicionPieza,
               final boolean capazEnroqueDeLadoRey,
               final boolean capazEnroqueDeLadoDama) {
        super(TipoPieza.REY, alianzaPieza, posicionPieza, true);
        this.estaEnrocado = false;
        this.capazEnroqueDeLadoRey = capazEnroqueDeLadoRey;
        this.capazEnroqueDeLadoDama = capazEnroqueDeLadoDama;
    }

    public Rey(final Alianza alianzaPieza,
               final int posicionPieza,
               boolean esPrimerMovimiento,
               final boolean estaEnrocado,
               final boolean capazEnroqueDeLadoRey,
               final boolean capazEnroqueDeLadoDama) {
        super(TipoPieza.REY, alianzaPieza, posicionPieza, esPrimerMovimiento);
        this.estaEnrocado = estaEnrocado;
        this.capazEnroqueDeLadoRey = capazEnroqueDeLadoRey;
        this.capazEnroqueDeLadoDama = capazEnroqueDeLadoDama;
    }

    public boolean estaEnrocado() {
        return this.estaEnrocado;
    }

    public boolean esCapazDeLadoRey() {
        return this.capazEnroqueDeLadoRey;
    }

    public boolean esCapazDeLadoDama() {
        return capazEnroqueDeLadoDama;
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for (final int offsetCandidatoActual: COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            if (esExclusionPrimeraColumna(this.posicionPieza, offsetCandidatoActual)
                    || esExclusionOctavaColumna(this.posicionPieza, offsetCandidatoActual)) {
                continue;
            }
            final int coordenadaCandidataDestino = this.posicionPieza + offsetCandidatoActual;
            if(TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
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
    public Rey moverPieza(final Movimiento movimiento) {
        return new Rey(movimiento.getPiezaMovida().getAlianzaPieza(),
                movimiento.getCoordenadaDestino(),
                false,
                movimiento.esEnroque(),
                false,
                false);
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    private static boolean esExclusionPrimeraColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.PRIMERA_COLUMNA.get(posicionActual)
                && (offsetCandidato == -9 || offsetCandidato == -1 || offsetCandidato == 7);
    }

    private static boolean esExclusionOctavaColumna(final int posicionActual, final int offsetCandidato) {
        return TableroUtils.OCTAVA_COLUMNA.get(posicionActual)
                && (offsetCandidato == -7 || offsetCandidato == 1 || offsetCandidato == 9);
    }

}
