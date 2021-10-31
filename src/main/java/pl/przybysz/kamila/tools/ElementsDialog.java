package pl.przybysz.kamila.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.TextAlignment;

public class ElementsDialog {

    public static Alert createAlter(Alert.AlertType alertType, String title){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        return alert;
    }


    public static Label createLabel(String text, int layoutX, int layoutY){
        Label label = new Label();
        label.setText(text);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        return label;
    }

    public static Label createLabel(String text, int layoutX, int layoutY, int prefHeight, int prefWidth, TextAlignment textAlignment){
        Label label = createLabel(text, layoutX, layoutY);
        label.setPrefHeight(prefHeight);
        label.setPrefWidth(prefWidth);
        label.setTextAlignment(textAlignment);
        return label;
    }

    public static Slider createSlider(int blockIncrement,  int layoutX, int layoutY, int min, int max, int prefHeight, int prefWidth, int value, boolean showTickLabels, boolean showTickMarks){
        Slider slider = new Slider();
        slider.setBlockIncrement(blockIncrement);
        slider.setLayoutX(layoutX);
        slider.setLayoutY(layoutY);
        slider.setMin(min);
        slider.setMax(max);
        slider.setPrefHeight(prefHeight);
        slider.setPrefWidth(prefWidth);
        slider.setValue(value);
        slider.setShowTickLabels(showTickLabels);
        slider.setShowTickMarks(showTickMarks);
        return slider;
    }


}
