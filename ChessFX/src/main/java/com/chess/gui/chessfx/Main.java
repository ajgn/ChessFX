package com.chess.gui.chessfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application {

    public static ResourceBundle bundle;
    @Override
    public void start(Stage stage) throws IOException {
        final Parameters argumentos = getParameters();
        final List<String> parametros = argumentos.getRaw();
        String idioma = "es";
        if (parametros.size() == 1 && (parametros.get(0).equals("es") || parametros.get(0).equals("ca"))) {
            idioma = parametros.get(0);
        }
        Locale locale = new Locale(idioma, "ES");
        System.out.println("Localización: " + locale);
        bundle = ResourceBundle.getBundle("com.chess.gui.chessfx.textos", locale);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tabla.fxml")), bundle);
        Scene scene = new Scene(root);
        stage.setTitle("ChessFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void main(String[] args) {
        launch(args);
    }
}