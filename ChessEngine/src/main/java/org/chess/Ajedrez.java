package org.chess;


import org.chess.motor.tablero.Tablero;

public class Ajedrez {
    public static void main(String[] args) {
        Tablero tablero = Tablero.crearTableroEstandar();
        System.out.println(tablero);
    }
}
