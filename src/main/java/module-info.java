module org.tin.oop2_capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens org.tin.oop2_capstone to javafx.fxml;
    exports org.tin.oop2_capstone;

    opens org.tin.oop2_capstone.screens.dashboard to javafx.fxml;
    exports org.tin.oop2_capstone.screens.dashboard to javafx.graphics;

    opens org.tin.oop2_capstone.screens.activity_log to javafx.fxml;
    exports org.tin.oop2_capstone.screens.activity_log to javafx.graphics;

    opens org.tin.oop2_capstone.screens.food_log to javafx.fxml;
    exports org.tin.oop2_capstone.screens.food_log to javafx.graphics;

    opens org.tin.oop2_capstone.screens.profile to javafx.fxml;
    exports org.tin.oop2_capstone.screens.profile to javafx.graphics;

    opens org.tin.oop2_capstone.screens.settings to javafx.fxml;
    exports org.tin.oop2_capstone.screens.settings to javafx.graphics;

    opens org.tin.oop2_capstone.screens.notification_tab to javafx.fxml;
    exports org.tin.oop2_capstone.screens.notification_tab to javafx.graphics;

    opens org.tin.oop2_capstone.screens.exercise_prompt to javafx.fxml;

}