module com.ca.chatappcs4 {
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
    requires java.desktop;
    requires javafx.media;
    requires javafx.swing;
    requires java.sql;
    requires webcam.capture;
    requires com.jfoenix;
    requires fontawesomefx;
    requires java.base;

    opens com.ca.chatappcs4.Model to javafx.base;
    opens com.ca.chatappcs4.Interfaces to javafx.fxml;
    exports com.ca.chatappcs4.Interfaces;
}