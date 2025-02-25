package com.chess.gui.chessfx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.chess.motor.Alianza;
import org.chess.motor.jugador.ia.EstrategiaMovimiento;
import org.chess.motor.jugador.ia.MiniMax;
import org.chess.motor.piezas.Pieza;
import org.chess.motor.tablero.Casilla;
import org.chess.motor.tablero.Movimiento;
import org.chess.motor.tablero.MovimientoTransicion;
import org.chess.motor.tablero.Tablero;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class MainController implements Initializable {
    private ResourceBundle resources;
    private static final int PROFUNDIDAD = 4;
    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private static final int ANCHO_CASILLA = 10;
    private static final int ALTO_CASILLA = 10;
    private static final int ALTO_ICONO = 64;
    private static final int ANCHO_ICONO_TABLERO = 64;
    private static final int ANCHO_ICONO_CAPTURAS = 24;
    private static final int TAMANYO_PUNTO = 16;
    private static final String COLOR_CASILLA_CLARA = "#ffcf9f";
    private static final String COLOR_CASILLA_OSCURA = "#d28c45";
    private static final String COLOR_PANEL_CAPTURADAS = "#76923c";
    private static final Border CASILLA_SELECCIONADA = new Border(
                    new BorderStroke(
                            Color.web("#0062da"),
                            BorderStrokeStyle.SOLID,
                            CornerRadii.EMPTY,
                            BorderStroke.MEDIUM));
    private List<Node> casillas;
    private Tablero tablero;
    private RegistroMovimientos registroMovimientos;
    private Pieza casillaOrigen;
    private Pieza piezaMovidaHumano;
    private DireccionTablero direccionTablero;
    private boolean resaltarMovimientosLegales;
    private static final String PUNTO_VERDE = "punto_verde";
    private List<ImageView> puntosVerdes;
    final ObservableList<Fila> data = FXCollections.observableArrayList();
    private String resultado = " - ";
    @FXML
    Label labelNombre;
    @FXML
    BorderPane borderPane;
    @FXML
    MenuBar menuBar;
    @FXML
    Menu menuArchivo;
    @FXML
    MenuItem cargarPartida;
    @FXML
    MenuItem guardarPartida;
    @FXML
    MenuItem salir;
    @FXML
    Menu menuAyuda;
    @FXML
    MenuItem acercaDe;
    @FXML
    GridPane panelTablero;
    @FXML
    Label label;
    @FXML
    MenuItem invertido;
    @FXML
    CheckMenuItem mostrarLegalesCheckMenuItem;
    @FXML
    SplitPane panelPiezasCapturadas;
    @FXML
    AnchorPane panelSuperior;
    @FXML
    AnchorPane panelInferior;
    @FXML
    GridPane gridPaneSuperior;
    @FXML
    GridPane gridPaneInferior;
    @FXML
    TableView<Fila> panelHistoricoMovimientos;
    @FXML
    TableColumn<Fila,String> c1;
    @FXML
    TableColumn<Fila,String> c2;

    private static final Set<String> codigosPieza = new HashSet<>(Arrays.asList("R","N","B","Q","K","P"));

    public static int getCoordenada(int fila, int columna) {
        return (fila * NUM_COLUMNAS) + columna;
    }

    private static int getFila(int coordenada) {
        return coordenada / NUM_COLUMNAS;
    }

    private static int getColumna(int coordenada) {
        return coordenada % NUM_COLUMNAS;
    }

    private boolean esUltimaColumna(int coordenada) {
        return (coordenada + 1) % NUM_COLUMNAS == 0;
    }

    public void nuevaPartida() {
        System.out.println("Nueva partida");
        tablero = Tablero.crearTableroEstandar();
        panelHistoricoMovimientos.getItems().clear();
        System.out.println(tablero);
        crearGUI();
        labelNombre.setText(leerNombreJugador());
    }

    private String leerNombreJugador() {
        TextInputDialog td = new TextInputDialog();
        td.initStyle(StageStyle.UNDECORATED);
        ((Button) td.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        td.setTitle("ChessFX");
        td.setHeaderText(resources.getString("introduzca_nombre"));
        td.setContentText(resources.getString("nombre") + ":");
        td.setGraphic(null);
        td.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        td.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            td.getDialogPane().lookupButton(ButtonType.OK).setDisable(newValue.trim().isBlank());
        });
        Optional<String> resultado = td.showAndWait();
        if (resultado.isPresent()) {
            String entrada = resultado.get();
            System.out.println("Nombre: " + entrada);
            return entrada;
        } else {
            System.out.println("Ninguna entrada");
        }
        return "";
    }

    public void cargarPartida() {
        System.out.println("Cargar partida");
        SQLitePersistence.conectar();
        Tablero nuevoTablero = SQLitePersistence.cargarPartida();
        SQLitePersistence.desconectar();
        System.out.println(nuevoTablero);
        if (nuevoTablero != null) {
            tablero = nuevoTablero;
            crearGUI();
        } else {
            System.out.println("El nuevo tablero es nulo");
        }
    }

    public void guardarPartida() {
        System.out.println("Guardar partida");
        String cadenaFen = UtilesFen.crearFenDesdeJuego(tablero);
        SQLitePersistence.conectar();
        SQLitePersistence.guardarPartida(cadenaFen, registroMovimientos.toString(), resultado, labelNombre.getText());
        SQLitePersistence.desconectar();
    }

    public void limpiarBaseDeDatos() {
        System.out.println("Limpiar base de datos");
        ButtonType okButton = new ButtonType(resources.getString("aceptar"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(resources.getString("cancelar"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                resources.getString("confirmar_borrado"),
                okButton,
                cancelButton);
        alert.setTitle("ChessFX");
        alert.setHeaderText("Limpiar base de datos");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(cancelButton) == okButton) {
            SQLitePersistence.conectar();
            SQLitePersistence.limpiarBaseDeDatos();
            SQLitePersistence.desconectar();
        }
    }

    public void salir() {
        System.out.println("Salir");
        Platform.exit();
    }

    public void invertir() {
        direccionTablero = direccionTablero.opuesto();
        Platform.runLater(this::dibujarTablero);
    }

    public void seleccionarMostrarLegales() {
        System.out.println("Seleccionar mostrar legales");
        resaltarMovimientosLegales = mostrarLegalesCheckMenuItem.isSelected();
    }
    public void acercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ChessFX");
        alert.setHeaderText(resources.getString("acerca_de"));
        StringBuilder sb = new StringBuilder("Alberto Joaquín Gomis Naranjo")
                .append(System.lineSeparator())
                .append("Desarrollo de Aplicaciones Multiplataforma")
                .append(System.lineSeparator())
                .append("Proyecto Fin de Ciclo")
                .append(System.lineSeparator())
                .append("IES San Vicente - 2023");
        System.out.println(sb);
        alert.setContentText(sb.toString());
        alert.initOwner(panelTablero.getScene().getWindow());
        alert.getDialogPane().setId("about");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("css/dialog.css")).toExternalForm());
        dialogPane.getStyleClass().add("about");
        alert.show();
    }

    private String ColorCasilla(int fila, int columna) {
        return (fila + columna) % 2 == 0 ? COLOR_CASILLA_CLARA : COLOR_CASILLA_OSCURA;
    }

    private static String getRutaIconoPieza(String codigoPieza) {
        String [] parts = codigoPieza.split("");
        if ( ! (parts[0].equals("B") || parts[0].equals("W")) && codigosPieza.contains(parts[1]) ) {
            throw new AssertionError("Código de pieza no válido: " + codigoPieza);
        } else {
            return "art/piezas/" + codigoPieza + ALTO_ICONO + ".png";
        }
    }

    private ImageView getIconoPieza(String codigoPieza) {
        return getIconoPieza(codigoPieza, ANCHO_ICONO_TABLERO);
    }

    private ImageView getIconoPieza(String codigoPieza, int tamanyo) {
        Image image = new Image(Objects.requireNonNull(MainController.class.getResourceAsStream(getRutaIconoPieza(codigoPieza))));
        ImageView iv = new ImageView(image);
        iv.setFitWidth(tamanyo);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        return iv;
    }

    private static ImageView getImagenPuntoVerde() {
        Image image = new Image(Objects.requireNonNull(MainController.class.getResourceAsStream("art/misc/green_dot.png")));
        ImageView iv = new ImageView(image);
        iv.setFitWidth(TAMANYO_PUNTO);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setId(PUNTO_VERDE);
        return iv;
    }

    private void limpiarIconos() {
        for (Node childFromGrid : panelTablero.getChildren()) {
            if(childFromGrid instanceof Pane pane) {
                for (Node childFromPane : pane.getChildren()) {
                    if (childFromPane instanceof ImageView imageView) {
                        Platform.runLater(() -> {
                            pane.getChildren().remove(imageView);
                        });
                    }
                }
            }
        }
    }

    private void dibujarTablero() {
        dibujarTablero(tablero);
    }

    private int getCoordenadaCasilla(Pane pane) {
        return direccionTablero.recorrer(casillas).indexOf(pane);
    }

    private void dibujarTablero(final Tablero nuevoTablero) {
        limpiarIconos();
        direccionTablero.recorrer(casillas).forEach(node -> {
            if (node instanceof Pane pane) {
                int coordenada = direccionTablero.recorrer(casillas).indexOf(pane);
                Casilla casilla = nuevoTablero.getCasilla(coordenada);
                if (casilla.esCasillaOcupada()) {
                    String codigoPieza = casilla.getPieza().getCodigoPieza();
                    ImageView imageView = getIconoPieza(codigoPieza);
                    Platform.runLater(() -> pane.getChildren().add(imageView));
                }
                if (resaltarMovimientosLegales) {
                    for (final Movimiento movimiento : movimientosLegalesPiezas(tablero)) {
                        if (movimiento.getCoordenadaDestino() == direccionTablero.recorrer(casillas).indexOf(pane)) {
                            final ImageView puntoVerde = getImagenPuntoVerde();
                            puntosVerdes.add(puntoVerde);
                            Platform.runLater(() -> pane.getChildren().add(puntoVerde));
                        }
                    }
                }
            }
        });
        System.out.println(tablero);
    }

    private Collection<Movimiento> movimientosLegalesPiezas(final Tablero tablero) {
        if (piezaMovidaHumano != null && piezaMovidaHumano.getAlianzaPieza() == tablero.jugadorActual().getAlianza()) {
            return piezaMovidaHumano.calcularMovimientosLegales(tablero);
        }
        return Collections.emptyList();
    }

    private void crearGUI() {
        registroMovimientos = new RegistroMovimientos();
        direccionTablero = DireccionTablero.NORMAL;
        resaltarMovimientosLegales = mostrarLegalesCheckMenuItem.isSelected();
        System.out.println(tablero);
        label.setText(resources.getString("juegan_blancas"));
        casillas = new ArrayList<>();
        puntosVerdes = new ArrayList<>();
        for (int fila = 0; fila < NUM_FILAS; fila++) {
            for (int columna = 0; columna < NUM_COLUMNAS; columna++) {
                StackPane pane = new StackPane();
                pane.setPrefSize(ANCHO_CASILLA, ALTO_CASILLA);
                pane.setStyle("-fx-background-color: " + ColorCasilla(fila,columna));
                pane.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        casillaOrigen = null;
                        piezaMovidaHumano = null;
                        for (Node node : direccionTablero.recorrer(casillas)) {
                            if (node instanceof Pane p) {
                                puntosVerdes.forEach(pv -> Platform.runLater(() -> p.getChildren().remove(pv)));
                            }
                        }
                    } else if (e.getButton() == MouseButton.PRIMARY) {
                        if (casillaOrigen == null) {
                            casillaOrigen = tablero.getCasilla(getCoordenadaCasilla(pane)).getPieza();
                            piezaMovidaHumano = casillaOrigen;
                            if (piezaMovidaHumano != null) {
                                casillas.stream()
                                        .filter(node -> node instanceof Pane)
                                        .forEach(node -> ((Pane) node).setBorder(null));
                                pane.setBorder(CASILLA_SELECCIONADA);
                            }
                        } else {
                            final Movimiento movimiento =
                                    Movimiento.FabricaMovimiento.crearMovimiento(
                                            tablero,
                                            casillaOrigen.getPosicionPieza(),
                                            getCoordenadaCasilla(pane));
                            if (movimiento.getPiezaMovida() != null) {
                                final MovimientoTransicion transicion = tablero.jugadorActual().realizarMovimiento(movimiento);
                                if (transicion.getEstadoMovimiento().estaHecho()) {
                                    tablero = transicion.getTableroTransicion();
                                    registroMovimientos.anyadirMovimiento(movimiento);
                                    rehacerPanelHistorico(tablero, registroMovimientos);
                                    rehacerPanelPiezasCapturadas(registroMovimientos);
                                    Alianza alianza = movimiento.getPiezaMovida().getAlianzaPieza();
                                    System.out.printf("%3s: %s\n", alianza, movimiento.toString().replace("P",""));
                                    String siguienteTurno = "";
                                    if (alianza.esBlanca()) {
                                        siguienteTurno = resources.getString("juegan_negras");
                                    } else if (alianza.esNegra()) {
                                        siguienteTurno = resources.getString("juegan_blancas");
                                    } else {
                                        throw new RuntimeException("¿Cómo llegaste tan lejos?");
                                    }
                                    label.setText(siguienteTurno);
                                    System.out.println(siguienteTurno);
                                    if (!tablero.jugadorActual().esJaqueMate() && !tablero.jugadorActual().esReyAhogado()) {
                                        final GrupoReflexionIA grupoReflexionIA = new GrupoReflexionIA();
                                        Thread thread = new Thread(grupoReflexionIA);
                                        thread.start();
                                    }
                                    if (tablero.jugadorActual().esJaqueMate() || tablero.jugadorActual().esReyAhogado()) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("ChessFX");
                                        alert.setHeaderText(resources.getString("juego_terminado"));
                                        alert.initOwner(panelTablero.getScene().getWindow());
                                        alert.getDialogPane().setId("terminado");
                                        DialogPane dialogPane = alert.getDialogPane();
                                        dialogPane.getStylesheets()
                                                .add(Objects.requireNonNull(getClass().getResource("css/dialog.css")).toExternalForm());
                                        dialogPane.getStyleClass().add("terminado");

                                        if (tablero.jugadorActual().esJaqueMate()) {
                                            alert.setContentText(resources.getString("jaque_mate"));
                                        }
                                        if (tablero.jugadorActual().esReyAhogado()) {
                                            alert.setContentText(resources.getString("rey_ahogado"));
                                        }
                                        alert.show();
                                    }
                                }
                            }
                            casillas.stream()
                                    .filter(node -> node instanceof Pane)
                                    .forEach(node -> ((Pane) node).setBorder(null));
                            casillaOrigen = null;
                            piezaMovidaHumano = null;
                        }
                        Platform.runLater(() -> dibujarTablero(tablero));
                    }
                });
                Casilla casilla = tablero.getCasilla(getCoordenada(fila,columna));
                if (casilla.esCasillaOcupada()) {
                    ImageView imageView = getIconoPieza(casilla.getPieza().getCodigoPieza());
                    pane.getChildren().add(imageView);
                }
                casillas.add(pane);
                panelTablero.add(pane, columna, fila);
            }
        }
        panelPiezasCapturadas.setStyle("-fx-background-color: " + COLOR_PANEL_CAPTURADAS);

        panelHistoricoMovimientos.setEditable(false);
        panelHistoricoMovimientos.setPlaceholder(new Label(resources.getString("sin_movimientos")));
        c1.setCellValueFactory(new PropertyValueFactory<Fila,String>("movimientoBlancas"));
        c1.setSortable(false);
        c2.setCellValueFactory(new PropertyValueFactory<Fila,String>("movimientoNegras"));
        c2.setSortable(false);
        panelHistoricoMovimientos.setItems(data);
        rehacerPanelHistorico(tablero, registroMovimientos);
        rehacerPanelPiezasCapturadas(registroMovimientos);
    }

    public static String getDisplayInfo() {
        try {
            StringBuilder s = new StringBuilder();
            // Comprobar si cada monitor soportará mi ventana de aplicación
            // Iterar a través de cada monitor y ver tamaño de cada uno
            GraphicsEnvironment ge      = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[]    gs      = ge.getScreenDevices();
            for (int i = 0; i < gs.length; i++)
            {
                java.awt.DisplayMode dm = gs[i].getDisplayMode();
                s.append("[")
                        .append(i)
                        .append("]: id=")
                        .append(gs[i].getIDstring())
                        .append(", size ")
                        .append(dm.getWidth())
                        .append("x")
                        .append(dm.getHeight())
                        .append("\n");
            }
            return s.toString();
        } catch (Exception e) {
            return " getDisplayInfo() failed";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Versión Java: " + System.getProperty("java.version"));
        System.out.println("Java home: " + System.getProperty("java.home"));
        System.out.println("Directorio temporal: " + System.getProperty("java.io.tmpdir"));
        System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));
        System.out.println("Classpath: " + System.getProperty("java.class.path") );
        System.out.println("Librarypath: " + System.getProperty("java.library.path"));
        System.out.println("Display: " + getDisplayInfo());
        System.out.println("Inicializar GUI");
        this.resources = resources;
        tablero = Tablero.crearTableroEstandar();
        crearGUI();
        labelNombre.setText(leerNombreJugador());
    }

    public void rehacerPanelPiezasCapturadas(final RegistroMovimientos registroMovimientos) {

        gridPaneInferior.getChildren().clear();
        gridPaneSuperior.getChildren().clear();

        final List<Pieza> piezasCapturadasBlancas = new ArrayList<>();
        final List<Pieza> piezasCapturadasNegras = new ArrayList<>();

        for (final Movimiento movimiento : registroMovimientos.getMovimientos()) {
            if (movimiento.esAtaque()) {
                final Pieza piezaCapturada = movimiento.getPiezaAtacada();
                if (piezaCapturada.getAlianzaPieza().esBlanca()) {
                    piezasCapturadasBlancas.add(piezaCapturada);
                } else if (piezaCapturada.getAlianzaPieza().esNegra()) {
                    piezasCapturadasNegras.add(piezaCapturada);
                } else {
                    throw new RuntimeException("¿Cómo llegaste tan lejos?");
                }
            }
        }

        piezasCapturadasBlancas.sort(Comparator.comparingInt(Pieza::getValorPieza));

        piezasCapturadasNegras.sort(Comparator.comparingInt(Pieza::getValorPieza));

        int contador = 0;
        for (final Pieza piezaCapturada : piezasCapturadasBlancas) {
            final ImageView imageView = getIconoPieza(piezaCapturada.getCodigoPieza(), ANCHO_ICONO_CAPTURAS);
            gridPaneInferior.getChildren().remove(imageView);
            GridPane.setHalignment(imageView, HPos.CENTER);
            gridPaneInferior.add(imageView, contador % 2, contador / 2);
            contador++;
        }

        contador = 0;
        for (final Pieza piezaCapturada : piezasCapturadasNegras) {
            final ImageView imageView = getIconoPieza(piezaCapturada.getCodigoPieza(), ANCHO_ICONO_CAPTURAS);
            gridPaneSuperior.getChildren().remove(imageView);
            GridPane.setHalignment(imageView, HPos.CENTER);
            gridPaneSuperior.add(imageView, contador % 2, contador / 2);
            contador++;
        }

    }

    public void rehacerPanelHistorico(final Tablero tablero, final RegistroMovimientos registroMovimientos) {
        panelHistoricoMovimientos.getItems().clear();
        data.removeAll();
        for (final Movimiento movimiento : registroMovimientos.getMovimientos()) {
            final String textoMovimiento = movimiento.toString().replace("P","");
            if (movimiento.getPiezaMovida().getAlianzaPieza().esBlanca()) {
                Fila nuevoRegistro = new Fila(textoMovimiento, "");
                data.add(nuevoRegistro);
            } else if (movimiento.getPiezaMovida().getAlianzaPieza().esNegra()){
                int ultimo = data.size() - 1;
                Fila registro = data.get(ultimo);
                String movimientoBlancas = registro.getMovimientoBlancas();
                Fila nuevoRegistro = new Fila(movimientoBlancas, textoMovimiento);
                data.remove(ultimo);
                data.add(nuevoRegistro);
            }
        }
        // Comprobar Jaques
        if (registroMovimientos.getMovimientos().size() > 0) {
            final Movimiento ultimoMovimiento =
                    registroMovimientos.getMovimientos().get(registroMovimientos.tamanyo() - 1);
            final String textoMovimiento = ultimoMovimiento.toString();
            int ultimo = data.size() - 1;
            Fila ultimoRegistro = data.get(ultimo);
            String movimientoBlancas = ultimoRegistro.getMovimientoBlancas();
            String movimientoNegras = ultimoRegistro.getMovimientoNegras();
            if (ultimoMovimiento.getPiezaMovida().getAlianzaPieza().esBlanca()) {
                Fila nuevoRegistro =
                        new Fila(movimientoBlancas + calcularJaqueYJaqueMate(tablero), movimientoNegras);
                data.remove(ultimo);
                data.add(nuevoRegistro);
            } else if (ultimoMovimiento.getPiezaMovida().getAlianzaPieza().esNegra()) {
                Fila nuevoRegistro =
                        new Fila(movimientoBlancas, movimientoNegras + calcularJaqueYJaqueMate(tablero));
                data.remove(ultimo);
                data.add(nuevoRegistro);
            }
        }
        Platform.runLater( () -> panelHistoricoMovimientos.scrollTo(data.size()-1) );
    }

    private String calcularJaqueYJaqueMate(final Tablero tablero) {
        if (tablero.jugadorActual().esJaqueMate()) {
            System.out.println("Jaque mate");
            return "#";
        } else if (tablero.jugadorActual().estaEnJaque()) {
            System.out.println("Jaque");
            return "+";
        }
        return "";
    }

    public void actualizarTablero(final Tablero nuevoTablero) {
        tablero = nuevoTablero;
    }

    public void actualizarMovimientoComputadora(final Movimiento movimiento) {
    }

    private TableView<Fila> getPanelHistorico() {
        return panelHistoricoMovimientos;
    }

    private SplitPane getPanelPiezasCapturadas() {
        return panelPiezasCapturadas;
    }

    private RegistroMovimientos getRegistroMovimientos() {
        return this.registroMovimientos;
    }

    private GridPane getPanelTablero() {
        return panelTablero;
    }

    private void actualizarMovimientoRealizado() {

    }

    public void exportarPartidas(ActionEvent actionEvent) {
        System.out.println("Exportar partidas a PDF");
        try {
            URL location = getClass().getResource("exportar.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(location);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            assert location != null;
            Parent root = FXMLLoader.load(location, Main.getBundle());
            Stage stage = new Stage();
            stage.setTitle("ChessFX");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Esperar que el ExportController se cierre
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GrupoReflexionIA extends Task<Movimiento> {
        @Override
        protected Movimiento call() throws Exception {
            Platform.runLater(() ->  label.setText(resources.getString("pensando")));
            // Realizar operación de larga duración
            final EstrategiaMovimiento miniMax = new MiniMax(PROFUNDIDAD);
            // Devuelve mejor movimiento
            Movimiento mejorMovimiento = miniMax.ejecutar(tablero);
            System.out.println("IA: " + mejorMovimiento);
            return mejorMovimiento;
        }
        @Override
        protected void succeeded() {
            // Manejar conclusión exitosa
            try {
                final Movimiento mejorMovimiento = get();
                actualizarMovimientoComputadora(mejorMovimiento);
                actualizarTablero(tablero.jugadorActual().realizarMovimiento(mejorMovimiento).getTableroTransicion());
                registroMovimientos.anyadirMovimiento(mejorMovimiento);
                rehacerPanelHistorico(tablero, registroMovimientos);
                rehacerPanelPiezasCapturadas(registroMovimientos);
                dibujarTablero();
                actualizarMovimientoRealizado();
                Platform.runLater(() -> label.setText(resources.getString("juegan_blancas")));

                if (tablero.jugadorActual().esJaqueMate() || tablero.jugadorActual().esReyAhogado()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("ChessFX");
                    alert.setHeaderText(resources.getString("juego_terminado"));
                    alert.initOwner(panelTablero.getScene().getWindow());
                    alert.getDialogPane().setId("terminado");
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets()
                            .add(Objects.requireNonNull(getClass().getResource("css/dialog.css")).toExternalForm());
                    dialogPane.getStyleClass().add("terminado");

                    if (tablero.jugadorActual().esJaqueMate()) {
                        alert.setContentText(resources.getString("jaque_mate"));
                    }
                    if (tablero.jugadorActual().esReyAhogado()) {
                        alert.setContentText(resources.getString("rey_ahogado"));
                    }
                    System.out.print(registroMovimientos.toString());
                    if (tablero.jugadorActual().esJaqueMate()) {
                        System.out.print("#");
                    } else {
                        System.out.println();
                    }
                    if (tablero.jugadorActual().esReyAhogado()) {
                        resultado = "1/2-1/2";
                    } else if (tablero.jugadorActual().toString().equals("Negras")) {
                        resultado = "1-0";
                    } else if (tablero.jugadorActual().toString().equals("Blancas")) {
                        resultado = "0-1";
                    } else {
                        throw new RuntimeException("Ganador no válido");
                    }
                    System.out.println(" " + resultado);
                    alert.show();
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void failed() {
            // Manejar fallo
        }
    }


    public enum DireccionTablero {
        NORMAL {
            @Override
            List<Node> recorrer(final List<Node> casillas) {
                return casillas;
            }
            @Override
            DireccionTablero opuesto() {
                return INVERTIDO;
            }
        },
        INVERTIDO {
            @Override
            List<Node> recorrer(final List<Node> casillas) {
                List<Node> reverse = new ArrayList<>(casillas);
                Collections.reverse(reverse);
                return reverse;
            }
            @Override
            DireccionTablero opuesto() {
                return NORMAL;
            }
        };

        abstract List<Node> recorrer(final List<Node> casillas);
        abstract DireccionTablero opuesto();
    }

    public static class Fila {
        private final SimpleStringProperty movimientoBlancas;
        private final SimpleStringProperty movimientoNegras;

        private Fila(String movimientoBlancas, String movimientoNegras) {
            this.movimientoBlancas = new SimpleStringProperty(movimientoBlancas);
            this.movimientoNegras = new SimpleStringProperty(movimientoNegras);
        }

        public String getMovimientoBlancas() {
            return movimientoBlancas.get();
        }

        public String getMovimientoNegras() {
            return movimientoNegras.get();
        }

        public void setMovimientoBlancas(final String movimiento) {
            this.movimientoBlancas.set(movimiento);
        }

        public void setMovimientoNegras(final String movimiento) {
            this.movimientoNegras.set(movimiento);
        }

    }

}