module org.tin.oop2_capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires com.google.gson;
    requires java.sql;

    opens org.tin.oop2_capstone to javafx.fxml;
    exports org.tin.oop2_capstone;

    opens org.tin.oop2_capstone.controllers to javafx.fxml;
    exports org.tin.oop2_capstone.model.entities;
    opens org.tin.oop2_capstone.model.entities to javafx.fxml;
    exports org.tin.oop2_capstone.controllers;

}