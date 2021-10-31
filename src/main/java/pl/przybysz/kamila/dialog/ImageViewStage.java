package pl.przybysz.kamila.dialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.PixelReader;
import javafx.stage.Stage;
import pl.przybysz.kamila.controller.ImageViewStageController;
import pl.przybysz.kamila.controller.MainStageController;
import pl.przybysz.kamila.utils.ImageHistogram;

import java.awt.image.BufferedImage;
import java.io.File;


public class ImageViewStage extends Stage {

    private StringProperty imageName = new SimpleStringProperty();
    private StringProperty imagePath = new SimpleStringProperty();

    private ImageViewStageController imageViewStageController;

    private BufferedImage bufferedImage;
    private File file;
    private String extension;//rozszerzenie pliku

    private Boolean activeStage;//z początku nie jest aktywna
    private Boolean openStage;//true - kiedy jest otwarta, false - kiedy zamknie się okno

    private ImageHistogram imageHistogram;

    public ImageViewStage(){
        this.activeStage = false;
        this.openStage = true;
        this.setOnCloseRequest(event -> {
            this.activeStage = false;
            this.openStage = false;
            MainStageController.removeFromImageViewStageObservableList(this);
            this.close();
        });
    }






    @Override
    public String toString(){
        return imageName.getValue();
    }

    public String getImageName() {
        return imageName.get();
    }

    public void setImageName(String imageName) {
        this.imageName.set(imageName);
    }

    public String getImagePath() {
        return imagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public Boolean getActiveStage() {
        return activeStage;
    }

    public void setActiveStage(Boolean activeStage) {
        this.activeStage = activeStage;
    }

    public ImageViewStageController getImageViewStageController() {
        return imageViewStageController;
    }

    public void setImageViewStageController(ImageViewStageController imageViewStageController) {
        this.imageViewStageController = imageViewStageController;
    }

    public Boolean getOpenStage() {
        return openStage;
    }

    public void setOpenStage(Boolean openStage) {
        this.openStage = openStage;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}