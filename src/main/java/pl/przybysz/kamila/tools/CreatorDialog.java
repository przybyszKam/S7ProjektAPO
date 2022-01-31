package pl.przybysz.kamila.tools;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import org.opencv.core.Size;
import pl.przybysz.kamila.dialog.ImageViewStage;
import pl.przybysz.kamila.enums.BorderType;
import pl.przybysz.kamila.enums.EditMagnitudeType;
import pl.przybysz.kamila.enums.Mask;
import pl.przybysz.kamila.enums.SegmentationOption;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class CreatorDialog {

    public static Alert createAlterReductionLevelColor(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Redukcja poziomow szarosci");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Docelowa liczba poziomow szarosci: ", 37, 38);
        Label reductionLabel = ElementsDialog.createLabel(null, 159, 116, 35, 50, TextAlignment.CENTER);

        Slider slider = ElementsDialog.createSlider(1, 21, 69, 2, 32, 47, 326, 2, true, true);
        slider.setMajorTickUnit(4);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.reductionLevel = (int) slider.getValue();
                reductionLabel.setText(String.valueOf( ImageAndMatTool.reductionLevel));
            }
        });

        ImageAndMatTool.reductionLevel = (int) slider.getValue();
        reductionLabel.setText(String.valueOf(ImageAndMatTool.reductionLevel));

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane reductionAnchorPane = new AnchorPane();
        reductionAnchorPane.setPrefHeight(200);
        reductionAnchorPane.setPrefWidth(368);
        reductionAnchorPane.getChildren().addAll(label, slider, reductionLabel);

        alert.getDialogPane().setContent(reductionAnchorPane);
        return alert;
    }

    public static Alert createAlterLevelColorThreshold(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Progowanie z zachowaniem poziomow szarosci");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label labelP1 = ElementsDialog.createLabel("Wartosc progowania P1: ", 37, 38);
        Label thresholdingLabelP1 = ElementsDialog.createLabel(null, 159, 116, 35, 50, TextAlignment.CENTER);
        Slider sliderP1 = ElementsDialog.createSlider(1, 21, 69, 0, 255, 47, 326, 0, true, true);

        Label labelP2 = ElementsDialog.createLabel("Wartosc progowania P2: ", 37, 238);
        Label thresholdingLabelP2 = ElementsDialog.createLabel(null, 159, 316, 35, 50, TextAlignment.CENTER);
        Slider sliderP2 = ElementsDialog.createSlider(1, 21, 269, 0, 255, 47, 326, 0, true, true);

        sliderP1.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.thresholdP1 = (int) sliderP1.getValue();
                thresholdingLabelP1.setText(String.valueOf(ImageAndMatTool.thresholdP1));
                sliderP2.setMin(ImageAndMatTool.thresholdP1);
                if(sliderP2.getValue()<ImageAndMatTool.thresholdP1){
                    sliderP2.setValue(ImageAndMatTool.thresholdP1);
                    thresholdingLabelP2.setText(String.valueOf(sliderP2.getValue()));
                }
            }
        });

        sliderP2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.thresholdP2 = (int) sliderP2.getValue();
                thresholdingLabelP2.setText(String.valueOf(ImageAndMatTool.thresholdP2));
                sliderP1.setMax(ImageAndMatTool.thresholdP2);
                if(sliderP1.getValue()>ImageAndMatTool.thresholdP2){
                    sliderP1.setValue(ImageAndMatTool.thresholdP2);
                    thresholdingLabelP1.setText(String.valueOf(sliderP1.getValue()));
                }
            }
        });

        ImageAndMatTool.thresholdP1 = (int) sliderP1.getValue();
        thresholdingLabelP1.setText(String.valueOf(ImageAndMatTool.thresholdP1));

        ImageAndMatTool.thresholdP2 = (int) sliderP2.getValue();
        thresholdingLabelP2.setText(String.valueOf(ImageAndMatTool.thresholdP2));

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane thresholdingAnchorPane = new AnchorPane();
        thresholdingAnchorPane.setPrefHeight(400);
        thresholdingAnchorPane.setPrefWidth(368);
        thresholdingAnchorPane.getChildren().addAll(labelP1, sliderP1, thresholdingLabelP1, labelP2, sliderP2, thresholdingLabelP2);

        alert.getDialogPane().setContent(thresholdingAnchorPane);
        return alert;
    }

    public static Alert createAlterBinaryThreshold(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Progowanie binarne");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Wartosc progowania: ", 37, 38);
        Label thresholdingLabel = ElementsDialog.createLabel(null, 159, 116, 35, 50, TextAlignment.CENTER);

        Slider slider = ElementsDialog.createSlider(1, 21, 69, 0, 255, 47, 326, 0, true, true);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.thresholdP1 = (int) slider.getValue();
                thresholdingLabel.setText(String.valueOf(ImageAndMatTool.thresholdP1));
            }
        });

        ImageAndMatTool.thresholdP1 = (int) slider.getValue();
        thresholdingLabel.setText(String.valueOf(ImageAndMatTool.thresholdP1));

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane thresholdingAnchorPane = new AnchorPane();
        thresholdingAnchorPane.setPrefHeight(200);
        thresholdingAnchorPane.setPrefWidth(368);
        thresholdingAnchorPane.getChildren().addAll(label, slider, thresholdingLabel);

        alert.getDialogPane().setContent(thresholdingAnchorPane);
        return alert;
    }

    public static Alert createAlterScaleHistogramQ1Q2(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Rozciaganie histogramu do zakresu p1-p2 do zakresu q1-q2");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label labelP1 = ElementsDialog.createLabel("P1:", 30, 34);
        Label scaleLabelP1 = ElementsDialog.createLabel(null, 84, 34, 35, 50, TextAlignment.CENTER);
        Slider sliderP1 = ElementsDialog.createSlider(1, 14, 59, 0, 255, 14, 140, 0, true, true);

        Label labelP2 = ElementsDialog.createLabel("P2:", 230, 34);
        Label scaleLabelP2 = ElementsDialog.createLabel(null, 284, 34, 35, 50, TextAlignment.CENTER);
        Slider sliderP2 = ElementsDialog.createSlider(1, 214, 59, 0, 255, 14, 140, 0, true, true);

        Label labelQ1 = ElementsDialog.createLabel("Q1:", 30, 97);
        Label scaleLabelQ1 = ElementsDialog.createLabel(null, 84, 97, 35, 50, TextAlignment.CENTER);
        Slider sliderQ1 = ElementsDialog.createSlider(1, 14, 121, 0, 255, 14, 140, 0, true, true);

        Label labelQ2 = ElementsDialog.createLabel("Q2:", 230, 97);
        Label scaleLabelQ2 = ElementsDialog.createLabel(null, 284, 97, 35, 50, TextAlignment.CENTER);
        Slider sliderQ2 = ElementsDialog.createSlider(1, 214, 121, 0, 255, 14, 140, 0, true, true);

        sliderP1.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.scaleHistogramP1 = (int) sliderP1.getValue();
                scaleLabelP1.setText(String.valueOf(ImageAndMatTool.scaleHistogramP1));
                sliderP2.setMin(ImageAndMatTool.scaleHistogramP1);
                if(sliderP2.getValue()<ImageAndMatTool.scaleHistogramP1){
                    sliderP2.setValue(ImageAndMatTool.scaleHistogramP1);
                    scaleLabelP2.setText(String.valueOf(sliderP2.getValue()));
                }
            }
        });

        sliderP2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.scaleHistogramP2 = (int) sliderP2.getValue();
                scaleLabelP2.setText(String.valueOf(ImageAndMatTool.scaleHistogramP2));
                sliderP1.setMax(ImageAndMatTool.scaleHistogramP2);
                if(sliderP1.getValue()>ImageAndMatTool.scaleHistogramP2){
                    sliderP1.setValue(ImageAndMatTool.scaleHistogramP2);
                    scaleLabelP1.setText(String.valueOf(sliderP1.getValue()));
                }
            }
        });

        sliderQ1.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.scaleHistogramQ1 = (int) sliderQ1.getValue();
                scaleLabelQ1.setText(String.valueOf(ImageAndMatTool.scaleHistogramQ1));
                sliderQ2.setMin(ImageAndMatTool.scaleHistogramQ1);
                if(sliderQ2.getValue()<ImageAndMatTool.scaleHistogramQ1){
                    sliderQ2.setValue(ImageAndMatTool.scaleHistogramQ1);
                    scaleLabelQ2.setText(String.valueOf(sliderQ2.getValue()));
                }
            }
        });

        sliderQ2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.scaleHistogramQ2 = (int) sliderQ2.getValue();
                scaleLabelQ2.setText(String.valueOf(ImageAndMatTool.scaleHistogramQ2));
                sliderQ1.setMax(ImageAndMatTool.scaleHistogramQ2);
                if(sliderQ1.getValue()>ImageAndMatTool.scaleHistogramQ2){
                    sliderQ1.setValue(ImageAndMatTool.scaleHistogramQ2);
                    scaleLabelQ1.setText(String.valueOf(sliderQ1.getValue()));
                }
            }
        });

        ImageAndMatTool.scaleHistogramP1 = (int) sliderP1.getValue();
        scaleLabelP1.setText(String.valueOf(ImageAndMatTool.scaleHistogramP1));

        ImageAndMatTool.scaleHistogramP2 = (int) sliderP2.getValue();
        scaleLabelP2.setText(String.valueOf(ImageAndMatTool.scaleHistogramP2));

        ImageAndMatTool.scaleHistogramQ1 = (int) sliderQ1.getValue();
        scaleLabelQ1.setText(String.valueOf(ImageAndMatTool.scaleHistogramQ1));

        ImageAndMatTool.scaleHistogramQ2 = (int) sliderQ2.getValue();
        scaleLabelQ2.setText(String.valueOf(ImageAndMatTool.scaleHistogramQ2));

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(224);
        scaleAnchorPane.setPrefWidth(400);
        scaleAnchorPane.getChildren().addAll(labelP1, sliderP1, scaleLabelP1, labelP2, sliderP2, scaleLabelP2, labelQ1, sliderQ1, scaleLabelQ1, labelQ2, sliderQ2, scaleLabelQ2);

        alert.getDialogPane().setContent(scaleAnchorPane);
        return alert;
    }

    public static Alert createLinearBlur(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Wygładzanie liniowe oparte na maskach wygładzania");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("Uśrednianie", true, false, 14, 14, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("Uśrednianie z wagami", false, false, 14, 39, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("Filtr gaussowski", false, false, 14, 64, toggleGroup);

        ComboBox comboBox = ElementsDialog.createComboBox(151, 10, 25, 105);
        comboBox.getItems().addAll("3x3", "5x5");

        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                String[] temp = newValue.split("x");
                ImageAndMatTool.averageSize = new Size(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]));
            }
        });

        Label labelColon = ElementsDialog.createLabel(": ", 153, 39);

        TextField textFieldK = new TextField();
        textFieldK.setLayoutX(165);
        textFieldK.setLayoutY(39);
        textFieldK.setPrefWidth(40);
        textFieldK.setPrefHeight(17);
        textFieldK.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*")){
                    textFieldK.setText(newValue.replaceAll("[^\\d]", ""));
                }else {
                    ImageAndMatTool.blurK = Integer.parseInt(textFieldK.getText());
                }
            }
        });

        labelColon.setVisible(false);
        textFieldK.setVisible(false);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton2.isSelected()){
                    labelColon.setVisible(true);
                    textFieldK.setVisible(true);
                    comboBox.setVisible(false);
                    ImageAndMatTool.usingMask = Mask.B;
                }else {
                    labelColon.setVisible(false);
                    textFieldK.setVisible(false);
                    if(radioButton1.isSelected()){
                        comboBox.setVisible(true);
                        ImageAndMatTool.usingMask = Mask.A;
                    }else{
                        comboBox.setVisible(false);
                        ImageAndMatTool.usingMask = Mask.G;
                    }
                }
            }
        });

        ImageAndMatTool.usingMask = Mask.A;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(144);
        scaleAnchorPane.setPrefWidth(310);
        scaleAnchorPane.getChildren().addAll(radioButton1, comboBox, radioButton2, labelColon, textFieldK, radioButton3);

        alert.getDialogPane().setContent(scaleAnchorPane);
        return alert;
    }

    public static Alert createSharpenMask(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Wyostrzania liniowego oparte na maskach laplasjanowych");
            alert.setOnCloseRequest(event -> {
            alert.close();
        });

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("", true, false, 70, 14, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("", false, false, 217, 14, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("", false, false, 366, 14, toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.usingMask = Mask.L1;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.usingMask = Mask.L2;
                else
                    ImageAndMatTool.usingMask = Mask.L3;
            }
        });

        ImageAndMatTool.usingMask = Mask.L1;

        TableView tableView1 = ElementsDialog.createTableView(25,43,120,105);
        setData3x3(tableView1, Mask.L1.getTab());

        TableView tableView2 = ElementsDialog.createTableView(172,43,120,105);
        setData3x3(tableView2, Mask.L2.getTab());

        TableView tableView3 = ElementsDialog.createTableView(321,43,120,105);
        setData3x3(tableView3, Mask.L3.getTab());

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(210);
        scaleAnchorPane.setPrefWidth(450);
        scaleAnchorPane.getChildren().addAll(radioButton1, radioButton2, radioButton3, tableView1, tableView2, tableView3);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

    public static Alert createEdgeDirectionSobel(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Kierunkowa detekcja krawędzi - Sobel");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("E - Wschód", true, false, 25, 15, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("SE - Pł. wschód", false, false, 175, 15, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("S - Południe", false, false, 325, 15, toggleGroup);
        RadioButton radioButton4 = ElementsDialog.createRadioButton("SW - Pł. zachód", false, false, 475, 15, toggleGroup);

        RadioButton radioButton5 = ElementsDialog.createRadioButton("W - Zachód", false, false, 25, 180, toggleGroup);
        RadioButton radioButton6 = ElementsDialog.createRadioButton("NW - Płn. zachód", false, false, 175, 180, toggleGroup);
        RadioButton radioButton7 = ElementsDialog.createRadioButton("N - Północ", false, false, 325, 180, toggleGroup);
        RadioButton radioButton8 = ElementsDialog.createRadioButton("NE - Płn. wschód", false, false, 475, 180, toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelE;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelSE;
                else if(radioButton3.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelS;
                else if(radioButton4.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelSW;
                else if(radioButton5.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelW;
                else if(radioButton6.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelNW;
                else if(radioButton7.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelN;
                else if(radioButton8.isSelected())
                    ImageAndMatTool.usingMask = Mask.SobelNE;
            }
        });

        ImageAndMatTool.usingMask = Mask.SobelE;

        TableView tableView1 = ElementsDialog.createTableView(25,45,120,105);
        setData3x3(tableView1, Mask.SobelE.getTab());

        TableView tableView2 = ElementsDialog.createTableView(175,45,120,105);
        setData3x3(tableView2, Mask.SobelSE.getTab());

        TableView tableView3 = ElementsDialog.createTableView(325,45,120,105);
        setData3x3(tableView3, Mask.SobelS.getTab());

        TableView tableView4 = ElementsDialog.createTableView(475,45,120,105);
        setData3x3(tableView4, Mask.SobelSW.getTab());

        TableView tableView5 = ElementsDialog.createTableView(25,210,120,105);
        setData3x3(tableView5, Mask.SobelW.getTab());

        TableView tableView6 = ElementsDialog.createTableView(175,210,120,105);
        setData3x3(tableView6, Mask.SobelNW.getTab());

        TableView tableView7 = ElementsDialog.createTableView(325,210,120,105);
        setData3x3(tableView7, Mask.SobelN.getTab());

        TableView tableView8 = ElementsDialog.createTableView(475,210,120,105);
        setData3x3(tableView8, Mask.SobelNE.getTab());

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(380);
        scaleAnchorPane.setPrefWidth(600);
        scaleAnchorPane.getChildren().addAll(radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6, radioButton7, radioButton8,
                tableView1, tableView2, tableView3, tableView4, tableView5, tableView6, tableView7, tableView8);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

    public static Alert createEdgeDirectionPrewitt(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Kierunkowa detekcja krawędzi - Prewitt kierunki S i E");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Wartość: ", 27, 14);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("Dokładna", true, false, 22, 37, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("Przybliżona", false, false, 22, 59, toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.valuePrewitt = true;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.valuePrewitt = false;
            }
        });
        ImageAndMatTool.valuePrewitt = true;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(130);
        scaleAnchorPane.setPrefWidth(300);
        scaleAnchorPane.getChildren().addAll(label, radioButton1, radioButton2);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

    public static Alert createEdgeUniversalMedian(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Uniwersalna operacja medianowa");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Rozmiar otoczenia: ", 27, 14);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("3x3", true, false, 22, 37, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("5x5", false, false, 22, 59, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("7x7", false, false, 22, 81, toggleGroup);
        RadioButton radioButton4 = ElementsDialog.createRadioButton("9x9", false, false, 22, 103, toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.medianSize = 3;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.medianSize = 5;
                else if(radioButton3.isSelected())
                    ImageAndMatTool.medianSize = 7;
                else if(radioButton4.isSelected())
                    ImageAndMatTool.medianSize = 9;
            }
        });

        ImageAndMatTool.medianSize = 3;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(130);
        scaleAnchorPane.setPrefWidth(300);
        scaleAnchorPane.getChildren().addAll(label, radioButton1, radioButton2, radioButton3, radioButton4);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

    public static Alert createSegmentationImage(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Segmentacja obrazu");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Wybierz metodę segmentacji: ", 27, 14);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("Srednia", true, false, 22, 37, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("Odchylenie standardowe", false, false, 22, 59, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("Warancja jasności", false, false, 22, 81, toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.segmentationOption = SegmentationOption.MEAN;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.segmentationOption = SegmentationOption.STANDARD_DEVIATION;
                else if(radioButton3.isSelected())
                    ImageAndMatTool.segmentationOption = SegmentationOption.BRIGHTNESS_VARIANCE;
            }
        });

        ImageAndMatTool.segmentationOption = SegmentationOption.MEAN;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(130);
        scaleAnchorPane.setPrefWidth(300);
        scaleAnchorPane.getChildren().addAll(label, radioButton1, radioButton2, radioButton3);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

    public static Alert createModifyFastFourierTransformation(int sizeMagnitude, BufferedImage bufferedImage){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Modyfikacja widma amplitudowego: ");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Wybierz wspolrzedne prostokata: ", 326, 14);
        Label labelP1 = ElementsDialog.createLabel("Punkt P1: ", 326, 31);
        Label labelP2 = ElementsDialog.createLabel("Punkt P2: ", 326, 109);

        Label labelP1X = ElementsDialog.createLabel("X: ", 343, 56);
        Label labelP1Y = ElementsDialog.createLabel("Y: ", 343, 83);
        Label labelP2X = ElementsDialog.createLabel("X: ", 343, 135);
        Label labelP2Y = ElementsDialog.createLabel("Y: ", 343, 162);

        Label labelValueP1X = ElementsDialog.createLabel(null, 519, 56);
        Label labelValueP1Y = ElementsDialog.createLabel(null, 519, 83);
        Label labelValueP2X = ElementsDialog.createLabel(null, 519, 135);
        Label labelValueP2Y = ElementsDialog.createLabel(null, 519, 162);

        Label labelModification = ElementsDialog.createLabel("Wybierz modyfikacje: ", 326, 207);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("Wypelnienie biela", true, false, 342, 230, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("Wypelnienie czernia", false, false, 342, 255, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("Podwojenie wartosci", false, false, 342, 280, toggleGroup);

        Slider sliderP1X = ElementsDialog.createSlider(1, 366, 56, 0, sizeMagnitude, 14, 140, 0, true, true);
        Slider sliderP1Y = ElementsDialog.createSlider(1, 366, 83, 0, sizeMagnitude, 14, 140, 0, true, true);
        Slider sliderP2X = ElementsDialog.createSlider(1, 366, 135, 0, sizeMagnitude, 14, 140, 0, true, true);
        Slider sliderP2Y = ElementsDialog.createSlider(1, 366, 162, 0, sizeMagnitude, 14, 140, 0, true, true);

        ImageView imageView = ElementsDialog.createImageView(0,0,310,310, true, true);

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(image);

        ColorModel cm = bufferedImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bufferedImage.copyData(null);
        BufferedImage bufferedImageNew =  new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        sliderP1X.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.P1X = (int) sliderP1X.getValue();
                labelValueP1X.setText(String.valueOf(ImageAndMatTool.P1X));
                editBufferedImage(bufferedImageNew, ImageAndMatTool.P1X, ImageAndMatTool.P1Y, ImageAndMatTool.P2X, ImageAndMatTool.P2Y, ImageAndMatTool.editMagnitudeType, imageView);
            }
        });

        sliderP1Y.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.P1Y = (int) sliderP1Y.getValue();
                labelValueP1Y.setText(String.valueOf(ImageAndMatTool.P1Y));
                editBufferedImage(bufferedImageNew, ImageAndMatTool.P1X, ImageAndMatTool.P1Y, ImageAndMatTool.P2X, ImageAndMatTool.P2Y, ImageAndMatTool.editMagnitudeType, imageView);
            }
        });

        sliderP2X.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.P2X = (int) sliderP2X.getValue();
                labelValueP2X.setText(String.valueOf(ImageAndMatTool.P2X));
                editBufferedImage(bufferedImageNew, ImageAndMatTool.P1X, ImageAndMatTool.P1Y, ImageAndMatTool.P2X, ImageAndMatTool.P2Y, ImageAndMatTool.editMagnitudeType, imageView);
            }
        });

        sliderP2Y.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.P2Y = (int) sliderP2Y.getValue();
                labelValueP2Y.setText(String.valueOf(ImageAndMatTool.P2Y));
                editBufferedImage(bufferedImageNew, ImageAndMatTool.P1X, ImageAndMatTool.P1Y, ImageAndMatTool.P2X, ImageAndMatTool.P2Y, ImageAndMatTool.editMagnitudeType, imageView);
            }
        });

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected())
                    ImageAndMatTool.editMagnitudeType = EditMagnitudeType.WHITE;
                else if(radioButton2.isSelected())
                    ImageAndMatTool.editMagnitudeType = EditMagnitudeType.BLACK;
                else if(radioButton3.isSelected())
                    ImageAndMatTool.editMagnitudeType = EditMagnitudeType.DOUBLE;
                editBufferedImage(bufferedImageNew, ImageAndMatTool.P1X, ImageAndMatTool.P1Y, ImageAndMatTool.P2X, ImageAndMatTool.P2Y, ImageAndMatTool.editMagnitudeType, imageView);
            }
        });

        ImageAndMatTool.editMagnitudeType = EditMagnitudeType.WHITE;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(350);
        anchorPane.setPrefWidth(620);
        anchorPane.getChildren().addAll(imageView,
                label, labelModification, labelP1, labelP1X, labelP1Y, labelP2, labelP2X, labelP2Y,
                labelValueP1X, labelValueP1Y, labelValueP2X, labelValueP2Y,
                radioButton1, radioButton2, radioButton3,
                sliderP1X, sliderP1Y, sliderP2X, sliderP2Y);
        alert.getDialogPane().setContent(anchorPane);
        return alert;
    }

    public static void editBufferedImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY, EditMagnitudeType type, ImageView imageView){
        //kopiowanie bufora obrazu
         ColorModel cm = bufferedImage.getColorModel();
         boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
         WritableRaster raster = bufferedImage.copyData(null);
         BufferedImage newImage =  new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        int rgb = 0;
        int pixel = 0;
        int red = 0;
        int green = 0;
        int blue = 0;

        if(endX <= startX){
            int tmp = startX;
            startX = endX;
            endX = tmp;
        }
        if(endY <= startY){
            int tmp = startY;
            startY = endY;
            endY = tmp;
        }

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                pixel = bufferedImage.getRGB(i, j);
                red = (pixel >> 16) & 0x000000FF;
                green = (pixel >> 8) & 0x000000FF;
                blue = (pixel) & 0x000000FF;

                red = calculateValue(type, red);
                green = calculateValue(type, green);
                blue = calculateValue(type, blue);

                rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                newImage.setRGB(i, j, rgb);
            }
        }

        Image image = SwingFXUtils.toFXImage(newImage, null);
        imageView.setImage(image);
    }

    private static int calculateValue(EditMagnitudeType type, int pixel){
        int value = 0;
        switch(type){
            case WHITE:
                value = 255;
                break;
            case BLACK:
                value = 0;
                break;
            case DOUBLE:
                value = pixel * 2;
                break;
        }
        if (value > 255)
            return 255;
        else
            return value;
    }

    private static void setData3x3(TableView tableView, int[][] tab){
        tableView.setEditable(true);
        final ObservableList<TableRow> data = FXCollections.observableArrayList(
                new TableRow(tab[0][0],tab[0][1],tab[0][2]),
                new TableRow(tab[1][0],tab[1][1],tab[1][2]),
                new TableRow(tab[2][0],tab[2][1],tab[2][2])
        );
        tableView.setItems(data);
    }

    public static Alert createSelectBorderType(){
        Alert alert = ElementsDialog.createAlter(Alert.AlertType.NONE, "Sposób uzupełnienia wart. brzegowych");
        alert.setOnCloseRequest(event -> {
            alert.close();
        });

        Label label = ElementsDialog.createLabel("Sposób uzupełnienia pikseli brzegowych: ", 27, 14);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = ElementsDialog.createRadioButton("Pozostawienie wartosci pikseli bez zmian", true, false, 22, 40, toggleGroup);
        RadioButton radioButton2 = ElementsDialog.createRadioButton("Zadana wartość arbitralna", false, false, 22, 70, toggleGroup);
        RadioButton radioButton3 = ElementsDialog.createRadioButton("Powielenie skrajnych wierszy i kolumn", false, false, 22, 100, toggleGroup);
        RadioButton radioButton4 = ElementsDialog.createRadioButton("Wykorzystanie pikseli z istniejącego sąsiedztwa", false, false, 22, 130, toggleGroup);

        Label value = ElementsDialog.createLabel(null, 364, 62, 16, 40, TextAlignment.CENTER);
        Slider sliderValue = ElementsDialog.createSlider(1, 214, 65, 0, 255, 14, 140, 0, true, true);

        value.setVisible(false);
        sliderValue.setVisible(false);

        sliderValue.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ImageAndMatTool.arbitraryValue = (int) sliderValue.getValue();
                value.setText(String.valueOf(ImageAndMatTool.arbitraryValue));
            }
        });

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (radioButton1.isSelected()) {
                    value.setVisible(false);
                    sliderValue.setVisible(false);
                    ImageAndMatTool.borderType = BorderType.PIXEL_WITHOUT_CHANGE;
                } else if (radioButton2.isSelected()) {
                    value.setVisible(true);
                    sliderValue.setVisible(true);
                    ImageAndMatTool.borderType = BorderType.ARBITRARY_VALUE;
                } else if (radioButton3.isSelected()){
                    value.setVisible(false);
                    sliderValue.setVisible(false);
                    ImageAndMatTool.borderType = BorderType.REFLECT;
                }else if(radioButton4.isSelected()){
                    value.setVisible(false);
                    sliderValue.setVisible(false);
                    ImageAndMatTool.borderType = BorderType.PIXEL_FROM_PROXIMITY;
                }
            }
        });

        ImageAndMatTool.borderType = BorderType.PIXEL_WITHOUT_CHANGE;
        ImageAndMatTool.arbitraryValue = 0;

        ButtonType applyButtonType = new ButtonType("Zastosuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        AnchorPane scaleAnchorPane = new AnchorPane();
        scaleAnchorPane.setPrefHeight(200);
        scaleAnchorPane.setPrefWidth(420);
        scaleAnchorPane.getChildren().addAll(label, radioButton1, radioButton2, sliderValue, value, radioButton3, radioButton4);
        alert.getDialogPane().setContent(scaleAnchorPane);

        return alert;
    }

}
