module org.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
   // requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.mail;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires StonkaPdfLib;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires jakarta.transaction;
    requires jakarta.cdi;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires mysql.connector.j;
    requires jbcrypt;
    requires org.slf4j;

    opens org.example.gui to javafx.fxml;
    opens org.example.sys to javafx.base, javafx.fxml, org.hibernate.orm.core, jakarta.persistence;
    opens org.example.pdflib to javafx.fxml;

    exports org.example.gui;
    exports org.example.sys;
    exports org.example.database;
    exports org.example.pdflib;
    exports org.example.wyjatki;
    exports org.example.gui.controllers;
    opens org.example.gui.controllers to javafx.fxml;
    exports org.example.gui.panels;
    opens org.example.gui.panels to javafx.fxml;
    exports org.example.gui.elements;
    opens org.example.gui.elements to javafx.fxml;
    exports org.example.database.repositories;
}
