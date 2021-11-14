package pl.przybysz.kamila.tools;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
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

    public static RadioButton createRadioButton(String text, boolean selected, boolean mnemonicParsing, int layoutX, int layoutY, ToggleGroup toggleGroup){
        RadioButton radioButton = new RadioButton();
        radioButton.setText(text);
        radioButton.setSelected(selected);
        radioButton.setMnemonicParsing(mnemonicParsing);
        radioButton.setLayoutX(layoutX);
        radioButton.setLayoutY(layoutY);
        radioButton.setToggleGroup(toggleGroup);
        return radioButton;
    }

    public static ComboBox createComboBox(int layoutX, int layoutY, int prefHeight, int prefWidth){
        ComboBox comboBox = new ComboBox();
        comboBox.setLayoutX(layoutX);
        comboBox.setLayoutY(layoutY);
        comboBox.setPrefHeight(prefHeight);
        comboBox.setPrefWidth(prefWidth);
        return comboBox;
    }

    public static TableView createTableView(int layoutX, int layoutY, int prefHeight, int prefWidth){
        TableView tableView = new TableView();
        tableView.setLayoutX(layoutX);
        tableView.setLayoutY(layoutY);
        tableView.setPrefHeight(prefHeight);
        tableView.setPrefWidth(prefWidth);

        TableColumn tableColumn1 = createTableColumn(35,"", "col1");
        TableColumn tableColumn2 = createTableColumn(35,"", "col2");
        TableColumn tableColumn3 = createTableColumn(35,"", "col3");

        tableView.getColumns().addAll(tableColumn1, tableColumn2, tableColumn3);
        return tableView;
    }

    public static TableColumn createTableColumn(int prefWidth, String text, String columnName){
        TableColumn tableColumn = new TableColumn(text);
        tableColumn.setPrefWidth(prefWidth);
        tableColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>(columnName));
        return tableColumn;
    }
}
