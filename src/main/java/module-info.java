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
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires kernel;
    requires layout;

    opens org.example.gui to javafx.fxml;
    opens org.example.sys to javafx.base, javafx.fxml, org.hibernate.orm.core, jakarta.persistence;

    exports org.example.gui;
    exports org.example.sys;
    exports org.example.database;
    exports org.example.hypermarket;
    exports org.example.pdflib;
    exports org.example.wyjatki;
}
