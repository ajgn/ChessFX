package org.chess.motor.jugador.ia;

import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.MovimientoTransicion;
import org.chess.motor.tablero.Tablero;

public class MiniMax implements EstrategiaMovimiento {
    private final EvaluadorTablero evaluadorTablero;
    private final int profundidadBusqueda;
    public MiniMax(final int profundidad) {
        this.evaluadorTablero = new EvaluadorTableroEstandar();
        this.profundidadBusqueda = profundidad;
    }
    @Override
    public String toString() {
        return "MiniMax";
    }
    @Override
    public Movimiento ejecutar(Tablero tablero) {
        final long tiempoInicial = System.currentTimeMillis();
        Movimiento mejorMovimiento = null;
        int mayorValorVisto = Integer.MIN_VALUE;
        int menorValorVisto = Integer.MAX_VALUE;
        int valorActual;

        System.out.println(tablero.jugadorActual() + " pensando con profundidad = " + profundidadBusqueda);
        int numMovimientos = tablero.jugadorActual().getMovimientosLegales().size();
        for(final Movimiento movimiento : tablero.jugadorActual().getMovimientosLegales()) {
            final MovimientoTransicion movimientoTransicion = tablero.jugadorActual().realizarMovimiento(movimiento);
            if(movimientoTransicion.getEstadoMovimiento().estaHecho()) {
                valorActual = tablero.jugadorActual().getAlianza().esBlanca()
                        ? min(movimientoTransicion.getTableroTransicion(), profundidadBusqueda - 1)
                        : max(movimientoTransicion.getTableroTransicion(), profundidadBusqueda - 1);
                if (tablero.jugadorActual().getAlianza().esBlanca() && valorActual >= mayorValorVisto) {
                    mayorValorVisto = valorActual;
                    mejorMovimiento = movimiento;
                } else if (tablero.jugadorActual().getAlianza().esNegra() && valorActual <= menorValorVisto) {
                    menorValorVisto = valorActual;
                    mejorMovimiento = movimiento;
                }
            }
        }

        final long tiempoEjecucion = System.currentTimeMillis() - tiempoInicial;

        return mejorMovimiento;
    }

    public int min(final Tablero tablero, final int profundidad) {
        if (profundidad == 0) { // fin del juego
            return this.evaluadorTablero.evaluar(tablero, profundidad);
        }

        int menorValorvisto = Integer.MAX_VALUE;
        for (final Movimiento movimiento : tablero.jugadorActual().getMovimientosLegales()) {
            final MovimientoTransicion movimientoTransicion = tablero.jugadorActual().realizarMovimiento(movimiento);
            if (movimientoTransicion.getEstadoMovimiento().estaHecho()) {
                final int valorActual = max(movimientoTransicion.getTableroTransicion(), profundidad - 1);
                if (valorActual <= menorValorvisto) {
                    menorValorvisto = valorActual;
                }
            }
        }
        return menorValorvisto;
    }

    public int max(final Tablero tablero, final int profundidad) {
        if (profundidad == 0) { // fin del juego
            return this.evaluadorTablero.evaluar(tablero, profundidad);
        }
        int mayorValorVisto = Integer.MIN_VALUE;
        for (final Movimiento movimiento : tablero.jugadorActual().getMovimientosLegales()) {
            final MovimientoTransicion movimientoTransicion = tablero.jugadorActual().realizarMovimiento(movimiento);
            if (movimientoTransicion.getEstadoMovimiento().estaHecho()) {
                final int valorActual = min(movimientoTransicion.getTableroTransicion(), profundidad - 1);
                if (valorActual >= mayorValorVisto) {
                    mayorValorVisto = valorActual;
                }
            }
        }
        return mayorValorVisto;
    }
}
