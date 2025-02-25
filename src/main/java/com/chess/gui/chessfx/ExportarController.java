package com.chess.gui.chessfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.commons.text.WordUtils;


public class ExportarController implements Initializable {
    @FXML
    Label labelListaPartidas;
    @FXML
    CheckBox cbSeleccionarTodo;
    @FXML
    TableColumn<DataModelPartida,String> fecha, nombre;
    @FXML
    TableView<DataModelPartida> tableViewListaPartidas;
    @FXML
    Button btnExportar;
    ObservableList<DataModelPartida> data;
    TableView.TableViewSelectionModel<DataModelPartida> selectionModel;
    List<Partida> listaPartidas;
    List<Partida> listaPartidasSeleccionadas;
    public static final String NOMBRE_ARCHIVO_PDF = "documento";
    public static final int ESPACIADO_ENTRE_LINEAS = 32;

    // Botón Exportar
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("Exportar");
        if (!selectionModel.getSelectedItems().isEmpty()) {
            selectionModel.getSelectedItems().forEach(System.out::println);
            List<Long> listaFechas = selectionModel.getSelectedItems().stream()
                    .map(DataModelPartida::getId)
                    .toList();
            listaPartidasSeleccionadas = new ArrayList<>(
                    listaPartidas.stream()
                    .filter(p -> listaFechas.contains(p.getTimestamp()))
                    .toList());

            // Creamos el PDF
            try {
                // Crear un nuevo documento PDF
                PDDocument documento = new PDDocument();

                // Crear una nueva página
                // Escribir en la página
                for (Partida partidaSeleccionada : listaPartidasSeleccionadas) {
                    PDPage pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);

                    // Crear un flujo de contenido para escribir en la página
                    PDPageContentStream contenido = new PDPageContentStream(documento, pagina);

                    // Escribir el título
                    contenido.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contenido.beginText();
                    contenido.newLineAtOffset(50, 700);
                    contenido.showText(Main.getBundle().getString("lista_partidas"));
                    contenido.endText();

                    contenido.beginText();
                    contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
                    contenido.newLineAtOffset(50, 670);
                    contenido.showText(partidaSeleccionada.getFecha() + " - " + partidaSeleccionada.getNombre());
                    contenido.endText();

                    String[] wrT;
                    String s;
                    wrT = WordUtils.wrap(partidaSeleccionada.getPartida(), 100).split("\\r?\\n");

                    for (int i=0; i< wrT.length; i++) {
                        contenido.beginText();
                        contenido.setFont(PDType1Font.HELVETICA, 10);
                        contenido.newLineAtOffset(50,600-i*15);
                        s = wrT[i];
                        contenido.showText(s);
                        contenido.endText();
                    }

    //                contenido.endText();
                    // Logotipos
                    PDImageXObject image = PDImageXObject.createFromByteArray(
                            documento,
                            Objects.requireNonNull(Main.class.getResourceAsStream("art/misc/chessfx125.png")).readAllBytes(),
                            "Logo ChessFX");
    //                contenido.drawImage(image, 20, 20, (float) image.getWidth() / 3, (float) image.getHeight() / 3);
                    contenido.drawImage(image, 20, 750, (float) image.getWidth() * 0.75f , (float) image.getHeight() * 0.75f);
                    image = PDImageXObject.createFromByteArray(
                            documento,
                            Objects.requireNonNull(Main.class.getResourceAsStream("art/misc/logo50invertido72.png")).readAllBytes(),
                            "Logo IES San Vicente");
                    contenido.drawImage(image, 400, 750, (float) image.getWidth(), (float) image.getHeight());

                    // Escribir el pie de página
                    LocalDate currentDate = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                    String formattedDate = currentDate.format(formatter);
                    System.out.println("Current Date: " + formattedDate);
                    String pie = (listaPartidasSeleccionadas.indexOf(partidaSeleccionada) + 1)
                            + "/" + listaPartidasSeleccionadas.size();

                    int marginTop = 30;
                    int marginBottom = 30;
                    float fontSize = 12;
                    PDFont font = PDType1Font.HELVETICA_OBLIQUE;
                    float anchoTextoFecha = font.getStringWidth(pie) / 1000 * fontSize;
                    float alturaTextoFecha = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                    contenido.beginText();
                    contenido.setFont(font, fontSize);
    //                contenido.newLineAtOffset((pagina.getMediaBox().getWidth() - anchoTextoFecha) / 2, pagina.getMediaBox().getHeight() - marginTop - alturaTextoFecha);
                    contenido.newLineAtOffset((pagina.getMediaBox().getWidth() - anchoTextoFecha) / 2, marginBottom + alturaTextoFecha);
                    contenido.showText(pie);
                    contenido.endText();

                    // Cerrar el flujo de contenido
                    contenido.close();

                }

                // Guardar el documento como un archivo PDF
                FileChooser fileChooser = new FileChooser();
                String directorioInicial = Paths.get(System.getProperty("user.home"), "Desktop").toString();
                fileChooser.setInitialDirectory(new File(directorioInicial));
                fileChooser.setInitialFileName(NOMBRE_ARCHIVO_PDF);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files","*.pdf"));
                File selectedFile = fileChooser.showSaveDialog(btnExportar.getScene().getWindow());
                documento.save(selectedFile.getAbsoluteFile());

                // Cerrar el documento
                documento.close();

                System.out.println("PDF creado correctamente");

                // Mostrar mensaje de información
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(Main.getBundle().getString("exportar_partidas"));
                alert.setContentText(Main.getBundle().getString("creado") + " " + selectedFile.getName());
                alert.setTitle("ChessFX");
                alert.showAndWait();

            } catch (IOException e) {
                // Mostrar mensaje de error
                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setHeaderText(Main.getBundle().getString("exportar_partidas"));
                alert.setHeaderText(null);
                alert.setContentText(Main.getBundle().getString("error_crear_archivo"));
                alert.setTitle("ChessFX");
                alert.showAndWait();
                throw new RuntimeException(e);
            }
        } else {
            listaPartidas = new ArrayList<>();
        }

        // Conseguir un manejador al escenario
        Stage stage = (Stage) btnExportar.getScene().getWindow();
        // Cerrar
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaPartidas = new ArrayList<>();
        tableViewListaPartidas.setPlaceholder(new Label(Main.getBundle().getString("no_hay_partidas")));
        selectionModel = tableViewListaPartidas.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        cbSeleccionarTodo.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (cbSeleccionarTodo.isSelected()) {
                    selectionModel.selectAll();
                } else {
                    selectionModel.clearSelection();
                }
            }
        });

//        data = FXCollections.observableArrayList(
//                new DataModelPartida("01/01/1970","Tom"),
//                new DataModelPartida("02/01/1970", "Sara"),
//                new DataModelPartida("03/01/1970", "Joe")
//        );

        data = FXCollections.observableArrayList();
        SQLitePersistence.conectar();
        listaPartidas = SQLitePersistence.listarPartidasGuardadas();
        SQLitePersistence.desconectar();
        listaPartidas.forEach(p -> data.add(new DataModelPartida(p.getTimestamp(), p.getFecha().toString(), p.getNombre())));

        // Set cell value factories
        fecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha")
        );

        nombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        tableViewListaPartidas.setItems(data);
    }

    public List<DataModelPartida> getPartidasSeleccionadas() {
        return selectionModel.getSelectedItems().stream().toList();
    }

    public static class DataModelPartida {

        private long id;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty nombre;
        public DataModelPartida(long id, String fecha, String nombre) {
            this.id = id;
            this.fecha = new SimpleStringProperty(fecha);
            this.nombre = new SimpleStringProperty(nombre);
        }

        public long getId() {
            return this.id;
        }

        public String getFecha() {
            return fecha.get();
        }
        public StringProperty fechaProperty() {
            return fecha;
        }
        public String getNombre() {
            return nombre.get();
        }
        public StringProperty nombreProperty() {
            return nombre;
        }
        public String toString() {
            return getId() + " " + getFecha() + " " + getNombre();
        }
    }

}
