module com.desarrollo.proyectocrud {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.desarrollo.proyectocrud to javafx.fxml;
    opens com.desarrollo.proyectocrud.controller to javafx.fxml;
    opens com.desarrollo.proyectocrud.model to javafx.base;

    exports com.desarrollo.proyectocrud;
    exports com.desarrollo.proyectocrud.controller;
    exports com.desarrollo.proyectocrud.model;
}