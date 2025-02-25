package com.chess.gui.chessfx;


import org.chess.motor.tablero.Movimiento;

import java.util.ArrayList;
import java.util.List;

public class RegistroMovimientos {
    private final List<Movimiento> movimientos;

    public RegistroMovimientos() {
        this.movimientos = new ArrayList<>();
    }

    public List<Movimiento> getMovimientos() {
        return this.movimientos;
    }

    public void anyadirMovimiento(final Movimiento movimiento) {
        this.movimientos.add(movimiento);
    }

    public int tamanyo() {
        return this.movimientos.size();
    }

    public void borrar() {
        this.movimientos.clear();
    }

    public Movimiento retirarMovimiento(int indice) {
        return this.movimientos.remove(indice);
    }

    public boolean retirarMovimiento(final Movimiento movimiento) {
        return this.movimientos.remove(movimiento);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < movimientos.size(); i++) {
            int numMovimiento = i / 2 + 1;
            if (i % 2 == 0) {
                sb.append(" ")
                    .append(numMovimiento)
                    .append(".")
                    .append(movimientos.get(i));
            } else {
                sb.append(" ")
                    .append(movimientos.get(i));
            }
        }
        return sb.toString()
                .replace("P","")
                .replace("p","")
                .trim();
    }
}
