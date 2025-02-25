package com.chess.gui.chessfx;

import org.chess.motor.Alianza;
import org.chess.motor.piezas.*;
import org.chess.motor.tablero.Tablero;
import org.chess.motor.tablero.TableroUtils;
import org.chess.motor.tablero.Tablero.Builder;

/**
 * Utilidades para manejar cadenas Forsyth-Edwards Notation
 */
public class UtilesFen {
    private UtilesFen() {
        throw new RuntimeException("¡No instanciable!");
    }

    public static Tablero crearJuegoDesdeFen(final String cadenaFen) {
        return parsearFen(cadenaFen);
    }

    private static Tablero parsearFen(final String cadenaFen) {
        final String[] fenPartitions = cadenaFen.trim().split(" ");
        final Builder builder = new Builder();
        final boolean enroqueLadoReyBlancas = enroqueLadoReyBlancas(fenPartitions[2]);
        final boolean enroqueLadoDamaBlancas = enroqueLadoDamaBlancas(fenPartitions[2]);
        final boolean enroqueLadoReyNegras = enroqueLadoReyNegras(fenPartitions[2]);
        final boolean enroqueLadoDamaNegras = enroqueLadoDamaNegras(fenPartitions[2]);
        final String configuracionJuego = fenPartitions[0];
        final char[] casillasTablero = configuracionJuego.replaceAll("/","")
                .replaceAll("8","--------")
                .replaceAll("7","-------")
                .replaceAll( "6","------")
                .replaceAll( "5","-----")
                .replaceAll( "4","----")
                .replaceAll( "3", "---")
                .replaceAll( "2", "--")
                .replaceAll( "1", "-")
                .toCharArray();

        int i = 0;
        while (i < casillasTablero.length) {
            switch (casillasTablero[i]) {
                case 'r':
                    builder.setPieza(new Torre(Alianza.NEGRA, i));
                    i++;
                    break;
                case 'n':
                    builder.setPieza(new Caballo(Alianza.NEGRA, i));
                    i++;
                    break;
                case 'b':
                    builder.setPieza(new Alfil(Alianza.NEGRA, i));
                    i++;
                    break;
                case 'q':
                    builder.setPieza(new Dama(Alianza.NEGRA, i));
                    i++;
                    break;
                case 'k':
                    final boolean isCastled = !enroqueLadoReyNegras && !enroqueLadoDamaNegras;
                    builder.setPieza(new Rey(Alianza.NEGRA, i, enroqueLadoReyNegras, enroqueLadoDamaNegras));
                    i++;
                    break;
                case 'p':
                    builder.setPieza(new Peon(Alianza.NEGRA, i));
                    i++;
                    break;
                case 'R':
                    builder.setPieza(new Torre(Alianza.BLANCA, i));
                    i++;
                    break;
                case 'N':
                    builder.setPieza(new Caballo(Alianza.BLANCA, i));
                    i++;
                    break;
                case 'B':
                    builder.setPieza(new Alfil(Alianza.BLANCA, i));
                    i++;
                    break;
                case 'Q':
                    builder.setPieza(new Dama(Alianza.BLANCA, i));
                    i++;
                    break;
                case 'K':
                    builder.setPieza(new Rey(Alianza.BLANCA, i, enroqueLadoReyBlancas, enroqueLadoDamaBlancas));
                    i++;
                    break;
                case 'P':
                    builder.setPieza(new Peon(Alianza.BLANCA, i));
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                default:
                    throw new RuntimeException("Cadena FEN inválida " + configuracionJuego);
            }
        }
        builder.setCreadorMovimiento(creadorMovimiento(fenPartitions[1]));
        return builder.build();
    }

    private static Alianza creadorMovimiento(String cadenaCreadorMovimiento) {
        if (cadenaCreadorMovimiento.equals("w")) {
            return Alianza.BLANCA;
        } else if (cadenaCreadorMovimiento.equals("b")) {
            return Alianza.NEGRA;
        }
        throw new RuntimeException("Cadena FEN inválida " + cadenaCreadorMovimiento);
    }

    private static boolean enroqueLadoReyBlancas(final String cadenaFenEnroque) {
        return cadenaFenEnroque.contains("K");
    }
    
    private static boolean enroqueLadoDamaBlancas(final String cadenaFenEnroque) {
        return cadenaFenEnroque.contains("Q");
    }
    
    private static boolean enroqueLadoReyNegras(final String cadenaFenEnroque) {
        return cadenaFenEnroque.contains("k");
    }
    
    private static boolean enroqueLadoDamaNegras(final String cadenaFenEnroque) {
        return cadenaFenEnroque.contains("q");
    }

    public static String crearFenDesdeJuego(final Tablero tablero) {
        // Posición inicial
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq – 0 1
        return calcularTextoTablero(tablero)
                + " " + calcularTextoJugadorActual(tablero)
                + " " + calcularTextoEnroque(tablero)
                + " " + calcularCasillaAlPaso(tablero)
                + " " + "0 1";
    }

    private static String calcularTextoTablero(final Tablero tablero) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TableroUtils.NUM_CASILLAS; i++) {
            final String textoCasilla = tablero.getCasilla(i).toString();
            sb.append(textoCasilla);
        }
        sb.insert(8, "/");
        sb.insert(17, "/");
        sb.insert(26, "/");
        sb.insert(35, "/");
        sb.insert(44, "/");
        sb.insert(53, "/");
        sb.insert(62, "/");

        return sb.toString().replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calcularTextoJugadorActual(final Tablero tablero) {
        return tablero.jugadorActual().getAlianza().toString().toLowerCase();
    }

    private static Object calcularCasillaAlPaso(Tablero tablero) {
        final Peon peonAlPaso = tablero.getPeonAlPaso();
        if (peonAlPaso != null) {
            return TableroUtils.getPosicionEnCoordenada(peonAlPaso.getPosicionPieza()
                    + 8 * peonAlPaso.getAlianzaPieza().getDireccionOpuesta());
        }
        return "-";
    }

    private static String calcularTextoEnroque(final Tablero tablero) {
        final StringBuilder sb = new StringBuilder();
        if (tablero.jugadorBlanco().esCapazDeLadoRey()) {
            sb.append("K");
        }
        if (tablero.jugadorBlanco().esCapazDeLadoDama()) {
            sb.append("Q");
        }
        if (tablero.jugadorNegro().esCapazDeLadoRey()) {
            sb.append("k");
        }
        if (tablero.jugadorNegro().esCapazDeLadoDama()) {
            sb.append("q");
        }

        final String result = sb.toString();

        return result.isEmpty() ? "-" : result;
    }

}
