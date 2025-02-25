module com.chess.gui.chessfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires commons.logging;
    requires org.apache.commons.text;
    requires ChessEngine;
    requires pdfbox;

    opens com.chess.gui.chessfx to javafx.fxml;
    exports com.chess.gui.chessfx;
}