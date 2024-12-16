module com.example.admindashboard {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;

    opens com.example.admindashboard to javafx.fxml;
    exports com.example.admindashboard;
}