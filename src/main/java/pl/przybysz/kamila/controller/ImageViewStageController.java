package pl.przybysz.kamila.controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import pl.przybysz.kamila.dialog.DataRowTableLUT;
import pl.przybysz.kamila.utils.ImageHistogram;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageViewStageController {

    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane imagePane;
    @FXML
    private ImageView imageViewLeft;//image w środku
    //histogram
    @FXML
    private AnchorPane histogram;
    @FXML
    private TabPane histogramTabPane;
    @FXML
    private Tab histogramMonochromaticTab;
    @FXML
    private Tab histogramRGBTab;
    @FXML
    private Tab tablicaLUTTab;



    private ImageHistogram imageHistogram;
    ObservableList<DataRowTableLUT> dataTableLUT = FXCollections.observableArrayList();
    private TableView<DataRowTableLUT> tableLUT;


    @FXML
    private void initialize(){

    }

    public void setImageInScrollPaneImageLeft(BufferedImage bufferedImage){
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageViewLeft.setImage(image);
    }

    public void createHistogramMono(){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chartHistogram = new BarChart<>(xAxis, yAxis);
        chartHistogram.getData().clear();

        if(imageHistogram.isSuccess()){
            chartHistogram.getData().addAll(
                    imageHistogram.getSeriesMono());
        }

        chartHistogram.lookupAll(".default-color0.chart-bar").forEach(n -> n.setStyle("-fx-bar-fill: black;"));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(chartHistogram);

        histogramMonochromaticTab.setContent(hBox);
    }

    public void createHistogramRGB(){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chartHistogram = new BarChart<>(xAxis, yAxis);
        chartHistogram.getData().clear();

        if(imageHistogram.isSuccess()){
            chartHistogram.getData().addAll(
                imageHistogram.getSeriesRed(),
                imageHistogram.getSeriesGreen(),
                imageHistogram.getSeriesBlue());
        }

        HBox hBox = new HBox();
        hBox.getChildren().addAll(chartHistogram);

        histogramRGBTab.setContent(hBox);
    }

    public void createTabLUT(){
        tableLUT = new TableView<DataRowTableLUT>();
        tableLUT.setEditable(true);
        TableColumn wartCol = new TableColumn("Wart.");
        wartCol.setMinWidth(60);
        wartCol.setCellValueFactory(
                new PropertyValueFactory<DataRowTableLUT, Long>("wart"));

        TableColumn colorRCol = new TableColumn("R");
        colorRCol.setMinWidth(60);
        colorRCol.setCellValueFactory(
                new PropertyValueFactory<DataRowTableLUT, Long>("colorR"));

        TableColumn colorGCol = new TableColumn("G");
        colorGCol.setMinWidth(60);
        colorGCol.setCellValueFactory(
                new PropertyValueFactory<DataRowTableLUT, Long>("colorG"));

        TableColumn colorBCol = new TableColumn("B");
        colorBCol.setMinWidth(60);
        colorBCol.setCellValueFactory(
                new PropertyValueFactory<DataRowTableLUT, Long>("colorB"));

        TableColumn colorMonoCol = new TableColumn("Mono");
        colorMonoCol.setMinWidth(60);
        colorMonoCol.setCellValueFactory(
                new PropertyValueFactory<DataRowTableLUT, Long>("colorMono"));

        tableLUT.getColumns().removeAll();

        createDataTableLUT();
        tableLUT.setItems(dataTableLUT);
        tableLUT.getColumns().removeAll();
        tableLUT.getColumns().addAll(wartCol, colorRCol, colorGCol, colorBCol, colorMonoCol);

        tablicaLUTTab.setContent(tableLUT);
    }

    private void createDataTableLUT(){
        dataTableLUT = FXCollections.observableArrayList();
        long red[] = imageHistogram.getRed();
        long green[] = imageHistogram.getGreen();
        long blue[] = imageHistogram.getBlue();
        long mono[] = imageHistogram.getMono();
        for(int i=0; i<256; i++){
            DataRowTableLUT dataRowTableLUT = new DataRowTableLUT(new SimpleLongProperty(i),
                    new SimpleLongProperty(red[i]),
                    new SimpleLongProperty(green[i]),
                    new SimpleLongProperty(blue[i]),
                    new SimpleLongProperty(mono[i])
                    );
            dataTableLUT.add(dataRowTableLUT);
        }
    }

    public ImageView getImageViewLeft() {
        return imageViewLeft;
    }

    public void setImageViewLeft(ImageView imageViewLeft) {
        this.imageViewLeft = imageViewLeft;
    }

    public ImageHistogram getImageHistogram() {
        return imageHistogram;
    }

    public void setImageHistogram(ImageHistogram imageHistogram) {
        this.imageHistogram = imageHistogram;
    }

}
