package org.tin.oop2_capstone.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class InputManager {
    private InputManager(){}

    public static void acceptOnlyIntegers(TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public static void acceptOnlyDouble(TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.isEmpty()) return;

                // to keep decimal values to only have one dot
                if (newValue.chars().filter(ch -> ch == '.').count() > 1) {
                    textField.setText(oldValue);
                    return;
                }

                if (!newValue.matches("\\d*(\\.\\d{0,2})?")) { // keeps decimal places to two only
                    textField.setText(oldValue);
                }
            }
        });
    }
}
