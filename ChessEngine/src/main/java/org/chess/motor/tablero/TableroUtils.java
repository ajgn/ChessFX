package org.chess.motor.tablero;

import java.util.*;

public class TableroUtils {

    public static final List<Boolean> PRIMERA_COLUMNA = inicializarColumna(0);
    public static final List<Boolean> SEGUNDA_COLUMNA = inicializarColumna(1);
    public static final List<Boolean> TERCERA_COLUMNA = inicializarColumna(2);
    public static final List<Boolean> CUARTA_COLUMNA = inicializarColumna(3);
    public static final List<Boolean> QUINTA_COLUMNA = inicializarColumna(4);
    public static final List<Boolean> SEXTA_COLUMNA = inicializarColumna(5);
    public static final List<Boolean> SEPTIMA_COLUMNA = inicializarColumna(6);
    public static final List<Boolean> OCTAVA_COLUMNA = inicializarColumna(7);
    public static final List<Boolean> PRIMERA_FILA = inicializarFila(0);
    public static final List<Boolean> SEGUNDA_FILA = inicializarFila(8);
    public static final List<Boolean> TERCERA_FILA = inicializarFila(16);
    public static final List<Boolean> CUARTA_FILA = inicializarFila(24);
    public static final List<Boolean> QUINTA_FILA = inicializarFila(32);
    public static final List<Boolean> SEXTA_FILA = inicializarFila(40);
    public static final List<Boolean> SEPTIMA_FILA = inicializarFila(48);
    public static final List<Boolean> OCTAVA_FILA = inicializarFila(56);

    public static final List<String> NOTACION_ALGEBRAICA = inicializarNotacionAlgebraica();

    private static List<String> inicializarNotacionAlgebraica() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }

    public static final Map<String, Integer> POSICION_A_COORDENADA = inicializarMapaPosicionACoordenada();

    private static Map<String, Integer> inicializarMapaPosicionACoordenada() {
        final Map<String, Integer> posicionACoordenada = new HashMap<>();
        for (int i = 0; i < NUM_CASILLAS; i++) {
            posicionACoordenada.put(NOTACION_ALGEBRAICA.get(i), i);
        }
        return Collections.unmodifiableMap(posicionACoordenada);
    }

    public static final int NUM_CASILLAS_POR_FILA = 8;
    public static final int NUM_CASILLAS = 64;

    private TableroUtils() {
        throw new RuntimeException("¡No me puedes instanciar!");
    }

    private static List<Boolean> inicializarColumna(int numeroColumna) {
        final Boolean[] columna = new Boolean[NUM_CASILLAS];
        Arrays.fill(columna, false);
        do {
            columna[numeroColumna] = true;
            numeroColumna += NUM_CASILLAS_POR_FILA;
        } while(numeroColumna < NUM_CASILLAS);
        return Collections.unmodifiableList(Arrays.asList((columna)));
    }

    private static List<Boolean> inicializarFila(int numFila) {
        final Boolean[] fila = new Boolean[NUM_CASILLAS];
        Arrays.fill(fila, false);
        do {
            fila[numFila] = true;
            numFila++;
        } while (numFila % NUM_CASILLAS_POR_FILA != 0);
        return Collections.unmodifiableList(Arrays.asList(fila));
    }

    public static boolean esCoordenadaCasillaValida(final int coordenada) {
        return coordenada >= 0 && coordenada < NUM_CASILLAS;
    }

    public static int getCoordenadaEnPosicion(final String posicion) {
        return POSICION_A_COORDENADA.get(posicion);
    }

    public static String getPosicionEnCoordenada(final int coordenada) {
        return NOTACION_ALGEBRAICA.get(coordenada);
    }
}
