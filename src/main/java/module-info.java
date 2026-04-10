module org.tin.oop2_capstone {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.tin.oop2_capstone to javafx.fxml;
    exports org.tin.oop2_capstone;
}