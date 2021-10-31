package pl.przybysz.kamila.tools;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import pl.przybysz.kamila.controller.MainStageController;

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
                MainStageController.reductionLevel = (int) slider.getValue();
                reductionLabel.setText(String.valueOf( MainStageController.reductionLevel));
            }
        });

        MainStageController.reductionLevel = (int) slider.getValue();
        reductionLabel.setText(String.valueOf( MainStageController.reductionLevel));

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
                MainStageController.thresholdP1 = (int) sliderP1.getValue();
                thresholdingLabelP1.setText(String.valueOf(MainStageController.thresholdP1));
                sliderP2.setMin(MainStageController.thresholdP1);
                if(sliderP2.getValue()<MainStageController.thresholdP1){
                    sliderP2.setValue(MainStageController.thresholdP1);
                    thresholdingLabelP2.setText(String.valueOf(sliderP2.getValue()));
                }
            }
        });

        sliderP2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainStageController.thresholdP2 = (int) sliderP2.getValue();
                thresholdingLabelP2.setText(String.valueOf(MainStageController.thresholdP2));
                sliderP1.setMax(MainStageController.thresholdP2);
                if(sliderP1.getValue()>MainStageController.thresholdP2){
                    sliderP1.setValue(MainStageController.thresholdP2);
                    thresholdingLabelP1.setText(String.valueOf(sliderP1.getValue()));
                }
            }
        });

        MainStageController.thresholdP1 = (int) sliderP1.getValue();
        thresholdingLabelP1.setText(String.valueOf(MainStageController.thresholdP1));

        MainStageController.thresholdP2 = (int) sliderP2.getValue();
        thresholdingLabelP2.setText(String.valueOf(MainStageController.thresholdP2));

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
                MainStageController.thresholdP1 = (int) slider.getValue();
                thresholdingLabel.setText(String.valueOf(MainStageController.thresholdP1));
            }
        });

        MainStageController.thresholdP1 = (int) slider.getValue();
        thresholdingLabel.setText(String.valueOf(MainStageController.thresholdP1));

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
                MainStageController.scaleHistogramP1 = (int) sliderP1.getValue();
                scaleLabelP1.setText(String.valueOf(MainStageController.scaleHistogramP1));
                sliderP2.setMin(MainStageController.scaleHistogramP1);
                if(sliderP2.getValue()<MainStageController.scaleHistogramP1){
                    sliderP2.setValue(MainStageController.scaleHistogramP1);
                    scaleLabelP2.setText(String.valueOf(sliderP2.getValue()));
                }
            }
        });

        sliderP2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainStageController.scaleHistogramP2 = (int) sliderP2.getValue();
                scaleLabelP2.setText(String.valueOf(MainStageController.scaleHistogramP2));
                sliderP1.setMax(MainStageController.scaleHistogramP2);
                if(sliderP1.getValue()>MainStageController.scaleHistogramP2){
                    sliderP1.setValue(MainStageController.scaleHistogramP2);
                    scaleLabelP1.setText(String.valueOf(sliderP1.getValue()));
                }
            }
        });

        sliderQ1.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainStageController.scaleHistogramQ1 = (int) sliderQ1.getValue();
                scaleLabelQ1.setText(String.valueOf(MainStageController.scaleHistogramQ1));
                sliderQ2.setMin(MainStageController.scaleHistogramQ1);
                if(sliderQ2.getValue()<MainStageController.scaleHistogramQ1){
                    sliderQ2.setValue(MainStageController.scaleHistogramQ1);
                    scaleLabelQ2.setText(String.valueOf(sliderQ2.getValue()));
                }
            }
        });

        sliderQ2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainStageController.scaleHistogramQ2 = (int) sliderQ2.getValue();
                scaleLabelQ2.setText(String.valueOf(MainStageController.scaleHistogramQ2));
                sliderQ1.setMax(MainStageController.scaleHistogramQ2);
                if(sliderQ1.getValue()>MainStageController.scaleHistogramQ2){
                    sliderQ1.setValue(MainStageController.scaleHistogramQ2);
                    scaleLabelQ1.setText(String.valueOf(sliderQ1.getValue()));
                }
            }
        });

        MainStageController.scaleHistogramP1 = (int) sliderP1.getValue();
        scaleLabelP1.setText(String.valueOf(MainStageController.scaleHistogramP1));

        MainStageController.scaleHistogramP2 = (int) sliderP2.getValue();
        scaleLabelP2.setText(String.valueOf(MainStageController.scaleHistogramP2));

        MainStageController.scaleHistogramQ1 = (int) sliderQ1.getValue();
        scaleLabelQ1.setText(String.valueOf(MainStageController.scaleHistogramQ1));

        MainStageController.scaleHistogramQ2 = (int) sliderQ2.getValue();
        scaleLabelQ2.setText(String.valueOf(MainStageController.scaleHistogramQ2));

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

}
