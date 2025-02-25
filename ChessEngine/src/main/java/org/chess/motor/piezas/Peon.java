package org.chess.motor.piezas;

import org.chess.motor.Alianza;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.Tablero;
import org.chess.motor.tablero.TableroUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.chess.motor.tablero.Movimiento.*;

public class Peon extends Pieza {

    private final static int[] COORDENADAS_MOVIMIENTOS_CANDIDATOS = {8, 16, 7, 9};
    public Peon(final Alianza alianzaPieza, final int posicionPieza) {
        super(TipoPieza.PEON, alianzaPieza, posicionPieza, true);
    }

    public Peon(final Alianza alianzaPieza, final int posicionPieza, boolean esPrimerMovimiento) {
        super(TipoPieza.PEON, alianzaPieza, posicionPieza, esPrimerMovimiento);
    }

    @Override
    public Collection<Movimiento> calcularMovimientosLegales(final Tablero tablero) {
        final List<Movimiento> movimientosLegales = new ArrayList<>();
        for (final int offsetCandidatoActual : COORDENADAS_MOVIMIENTOS_CANDIDATOS) {
            int coordenadaCandidataDestino
                    = this.posicionPieza + (this.alianzaPieza.getDireccion() * offsetCandidatoActual);
            if (!TableroUtils.esCoordenadaCasillaValida(coordenadaCandidataDestino)) {
                continue;
            }
            if (offsetCandidatoActual == 8 && tablero.getCasilla(coordenadaCandidataDestino).getPieza() == null) {
                if (this.alianzaPieza.esCasillaPromocionPeon(coordenadaCandidataDestino)) {
                    movimientosLegales.add(
                            new PromocionPeon(
                                    new MovimientoPeon(tablero, this, coordenadaCandidataDestino)));
                } else {
                    movimientosLegales.add(new MovimientoPeon(tablero, this, coordenadaCandidataDestino));
                }
            } else if (offsetCandidatoActual == 16 && this.esPrimerMovimiento()
                    && ((TableroUtils.SEGUNDA_FILA.get(this.posicionPieza) && this.getAlianzaPieza().esNegra())
                    || (TableroUtils.SEPTIMA_FILA.get(this.posicionPieza) && this.getAlianzaPieza().esBlanca()))) {
                final int coordenadaDetrasCandidataDestino = this.posicionPieza + (this.alianzaPieza.getDireccion() * 8);
                if (tablero.getCasilla(coordenadaCandidataDestino).getPieza() == null
                        && tablero.getCasilla(coordenadaDetrasCandidataDestino).getPieza() == null) {
                    movimientosLegales.add(new MovimientoSaltoPeon(tablero, this, coordenadaCandidataDestino));
                }
            } else if (offsetCandidatoActual == 7
                 && !((TableroUtils.OCTAVA_COLUMNA.get(this.posicionPieza) && this.alianzaPieza.esBlanca())
                        || (TableroUtils.PRIMERA_COLUMNA.get(this.posicionPieza) && this.alianzaPieza.esNegra()))) {
                if (tablero.getCasilla(coordenadaCandidataDestino).getPieza() != null) {
                    final Pieza piezaEnCandidata = tablero.getCasilla(coordenadaCandidataDestino).getPieza();
                    if(this.alianzaPieza != piezaEnCandidata.getAlianzaPieza()) {
                        if (this.alianzaPieza.esCasillaPromocionPeon(coordenadaCandidataDestino)) {
                            movimientosLegales.add(new PromocionPeon( new MovimientoAtaquePeon(
                                    tablero,
                                    this,
                                    coordenadaCandidataDestino,
                                    piezaEnCandidata)));
                        } else {
                            movimientosLegales.add(
                                    new MovimientoAtaquePeon(
                                            tablero,
                                            this,
                                            coordenadaCandidataDestino,
                                            piezaEnCandidata));
                        }
                    }
                } else if (tablero.getPeonAlPaso() != null) {
                    if (tablero.getPeonAlPaso().getPosicionPieza()
                            == (this.posicionPieza + (this.alianzaPieza.getDireccionOpuesta()))) {
                        final Pieza piezaEnCandidata = tablero.getPeonAlPaso();
                        if (this.alianzaPieza != piezaEnCandidata.getAlianzaPieza()) {
                            movimientosLegales.add(
                                    new MovimientoAtaqueAlPaso(
                                            tablero,
                                            this,
                                            coordenadaCandidataDestino,
                                            piezaEnCandidata));
                        }
                    }
                }
            } else if (offsetCandidatoActual == 9
                    && !((TableroUtils.PRIMERA_COLUMNA.get(this.posicionPieza) && this.alianzaPieza.esBlanca()
                            || (TableroUtils.OCTAVA_COLUMNA.get(this.posicionPieza) && this.alianzaPieza.esNegra())))) {
                if(tablero.getCasilla(coordenadaCandidataDestino).getPieza() != null) {
                    if(this.alianzaPieza != tablero.getCasilla(coordenadaCandidataDestino).getPieza().getAlianzaPieza()) {
                        if (this.alianzaPieza.esCasillaPromocionPeon(coordenadaCandidataDestino)) {
                            movimientosLegales.add(new PromocionPeon(new MovimientoAtaquePeon(
                                    tablero,
                                    this,
                                    coordenadaCandidataDestino,
                                    tablero.getCasilla(coordenadaCandidataDestino).getPieza())));
                        } else {
                            movimientosLegales.add(
                                    new MovimientoAtaquePeon(
                                            tablero,
                                            this,
                                            coordenadaCandidataDestino,
                                            tablero.getCasilla(coordenadaCandidataDestino).getPieza()));
                        }
                    }
                } else if (tablero.getPeonAlPaso() != null) {
                    if (tablero.getPeonAlPaso().getPosicionPieza()
                            == (this.posicionPieza - (this.alianzaPieza.getDireccionOpuesta()))) {
                        final Pieza piezaEnCandidata = tablero.getPeonAlPaso();
                        if (this.alianzaPieza != piezaEnCandidata.getAlianzaPieza()) {
                            movimientosLegales.add(
                                    new MovimientoAtaqueAlPaso(
                                            tablero,
                                            this,
                                            coordenadaCandidataDestino,
                                            piezaEnCandidata));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(movimientosLegales);
    }

    @Override
    public Peon moverPieza(final Movimiento movimiento) {
        return new Peon(movimiento.getPiezaMovida().getAlianzaPieza(), movimiento.getCoordenadaDestino());
    }

    @Override
    public String toString() {
        return this.tipoPieza.toString();
    }

    public Pieza getPiezaPromocionada() {
        return new Dama(this.alianzaPieza, this.posicionPieza, false);
    }

}
