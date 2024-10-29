module com.example.library_managmenrt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;
    requires java.persistence;
    requires org.hibernate.orm.core;

    opens com.example.library_managmenrt to javafx.fxml;
    exports com.example.library_managmenrt;
    exports com.example.library_managmenrt.service;
    exports com.example.library_managmenrt.controller; // Add this line to allow access to your controller package
    opens com.example.library_managmenrt.controller to javafx.fxml;

    // Other module declarations
    opens com.example.library_managmenrt.model to org.hibernate.orm.core;
}