module org.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.mail;
    // requires org.junit.jupiter.api;

    opens org.example.gui to javafx.fxml;
    exports org.example.gui;
    exports org.example.sys;
    opens org.example.sys to javafx.base, javafx.fxml;
    exports org.example.database;
    /*exports org.example.database;
    exports org.example.hypermarket;
    exports org.example.sys;
    exports org.example.pdflib;*/
}