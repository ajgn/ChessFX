package org.chess.motor.tablero;

public class MovimientoTransicion {

    private final Tablero tableroTransicion;
    private final Movimiento movimiento;
    private final EstadoMovimiento estadoMovimiento;

    public MovimientoTransicion(final Tablero tableroTransicion,
                                final Movimiento movimiento,
                                final EstadoMovimiento estadoMovimiento) {
        this.tableroTransicion = tableroTransicion;
        this.movimiento = movimiento;
        this.estadoMovimiento = estadoMovimiento;
    }

    public EstadoMovimiento getEstadoMovimiento() {
        return this.estadoMovimiento;
    }

    public Tablero getTableroTransicion() {
        return this.tableroTransicion;
    }

}
