package com.chess.gui.chessfx;

import org.chess.motor.tablero.Tablero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLitePersistence {
    private static final String BASE_DE_DATOS = "chess.db";
    private static final String url = "jdbc:sqlite:" + BASE_DE_DATOS;
    private static final String TABLA_SQL_POSICION = "posicion";
    private static final String TABLA_SQL_PARTIDAS = "partidas";
    private static final String COLUMNA_TIMESTAMP = "timestamp";
    private static final String COLUMNA_FEN = "fen";
    private static final String COLUMNA_PARTIDA = "partida";
    private static final String COLUMNA_NOMBRE = "nombre";
    private static Connection conn;
    private static Statement stmt;
    private static String sql = "";

    private SQLitePersistence() { throw new RuntimeException("¡No instanciable!");}; // no instanciable

    public static Tablero cargarPartida() {
        // Recuperar datos de la base de datos
        sql = "SELECT " + COLUMNA_TIMESTAMP + ", " + COLUMNA_FEN + " FROM " + TABLA_SQL_POSICION;
        String fenCadena = "";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong(COLUMNA_TIMESTAMP);
                fenCadena = rs.getString(COLUMNA_FEN);
                System.out.println("Timestamp = " + id + ", FEN = " + fenCadena);
            }
            System.out.println("Capa de persistencia: partida cargada");
        } catch (SQLException e) {
            System.out.println("Error al cargar la partida");
            throw  new RuntimeException(e);
        }
        return UtilesFen.crearJuegoDesdeFen(fenCadena);
    }

    public static List<Partida> listarPartidasGuardadas() {
        List<Partida> listaPartidas = new ArrayList<>();
        // Recuperar datos de la base de datos
        sql = "SELECT * FROM " + TABLA_SQL_PARTIDAS;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong(COLUMNA_TIMESTAMP);
                String nombre = rs.getString(COLUMNA_NOMBRE);
                String textoPartida = rs.getString(COLUMNA_PARTIDA);
                Partida partida = new Partida(id, nombre, textoPartida);
                listaPartidas.add(partida);
            }
            System.out.println("Capa de persistencia: listar partidas guardadas");
        } catch (SQLException e) {
            System.out.println("Error al listar partidas guardadas");
            throw  new RuntimeException(e);
        }
        return listaPartidas;
    }

    public static void borrarPartida(long id) {
        String sql = "DELETE FROM " + TABLA_SQL_PARTIDAS + " WHERE " + COLUMNA_TIMESTAMP + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("Capa de persistencia: borrar partida + " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void borrarTodasLasPartidas() {
        String sql = "DELETE FROM " + TABLA_SQL_PARTIDAS;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Capa de persistencia: borrar todas las partidas");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void borrarPosicion() {
        // Solo guardamos última posición, por lo que borramos todas
        sql = "DELETE FROM " + TABLA_SQL_POSICION;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Capa de persistencia: borrar posición");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void limpiarBaseDeDatos() {
        borrarPosicion();
        borrarTodasLasPartidas();
    }

    public static void guardarPartida(String cadenaFen, String partida, String resultado, String nombreJugador) {
        // Insertar datos en la tabla
        long timestamp = System.currentTimeMillis();
        // Borramos todas las posiciones anteriores
        sql = "DELETE FROM " + TABLA_SQL_POSICION;
        // Guardamos nueva partida
        try {
            // Guardar posicion
            stmt.executeUpdate(sql);
            sql = "INSERT INTO " + TABLA_SQL_POSICION
                     + " (" + COLUMNA_TIMESTAMP + ", " + COLUMNA_FEN + ") "
                     + "VALUES (" + timestamp + ", '" + cadenaFen + "')";
            stmt.executeUpdate(sql);
            // Guardar partida
            String mate = "";
            if (resultado.equals("1-0") || resultado.equals("0-1")) {
                mate = "#";
            }
            sql = "INSERT INTO " + TABLA_SQL_PARTIDAS
                    + " (" + COLUMNA_TIMESTAMP + ", " + COLUMNA_NOMBRE + ", " + COLUMNA_PARTIDA + ") "
                    + "VALUES (" + timestamp + ", '" + nombreJugador + "', '" + partida + mate + ' ' + resultado + "')";
            stmt.executeUpdate(sql);
            System.out.println("Capa de persistencia: partida guardada");
        } catch (SQLException e) {
            System.out.println("Error al guardar la partida");
            throw new RuntimeException(e);
        }
    }

    public static void conectar() {
        // Cargar el controlador JDBC
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Establecer la conexión
        try {
            conn = DriverManager.getConnection(url);

            // Crear la tabla si no existe
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLA_SQL_POSICION +
                    " (" + COLUMNA_TIMESTAMP + " INT PRIMARY KEY NOT NULL, " +
                    COLUMNA_FEN + " TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS " + TABLA_SQL_PARTIDAS +
                    " (" + COLUMNA_TIMESTAMP + " INT PRIMARY KEY NOT NULL, " +
                    COLUMNA_NOMBRE + " VARCHAR(50) NOT NULL, " +
                    COLUMNA_PARTIDA + " TEXT NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void desconectar() {
        // Cerrar la conexión
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        conectar();
//      Posición inicial
//      String cadenaFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq – 0 1";
//      Tras movimiento 1. e4
//      String cadenaFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
//      Y entonces tras 1. ... c5:
//      String cadenaFen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
//      Y entonces tras 2. Nf3:
        String cadenaFen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        guardarPartida(cadenaFen, "", "", "Kasparov");
        Tablero tablero = cargarPartida();
        System.out.println(tablero);
        desconectar();
    }

}
