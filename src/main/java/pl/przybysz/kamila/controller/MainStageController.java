package pl.przybysz.kamila.controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pl.przybysz.kamila.dialog.ImageViewStage;
import pl.przybysz.kamila.enums.Mask;
import pl.przybysz.kamila.tools.CalculateTool;
import pl.przybysz.kamila.tools.CreatorDialog;
import pl.przybysz.kamila.tools.ElementsDialog;
import pl.przybysz.kamila.tools.ImageAndMatTool;
import pl.przybysz.kamila.utils.ImageHistogram;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import static org.opencv.core.Core.BORDER_REPLICATE;

public class MainStageController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem menuItemLoadFile;
    @FXML
    private MenuItem menuItemSaveFileAs;
    @FXML
    private MenuItem menuItemDuplicate;
    @FXML
    private ComboBox<ImageViewStage> imagesList;

    private CalculateTool calculateTool;
    private ImageAndMatTool imageAndMatTool;

    private static ObservableList<ImageViewStage> imageViewStageObservableList;//elementy dodawane do observable list
	private static ListProperty<ImageViewStage> imageViewStagesListProperty;

	//aktywny stage
    private ImageViewStage activeImageViewStage = null;
    private Stage mainStage;

    //usuniecie z listy
    public static void removeFromImageViewStageObservableList(ImageViewStage imageViewStage){
        imageViewStageObservableList.remove(imageViewStage);
    }

    @FXML
    private void initialize(){
        setTools();

        imageViewStagesListProperty = new SimpleListProperty<>();
        imageViewStageObservableList = FXCollections.observableArrayList();//pusta lista linkedlist
        imagesList.setPromptText("Wybierz obraz...");

        imageViewStagesListProperty.set(imageViewStageObservableList);
        imagesList.itemsProperty().bindBidirectional(imageViewStagesListProperty);
    }

    private void setTools(){
        this.imageAndMatTool = new ImageAndMatTool();
        this.calculateTool = new CalculateTool();
    }

    @FXML
    public void loadFile(ActionEvent actionEvent) {
        Window window = anchorPane.getScene().getWindow();
        FileChooser fileChooser = prepareFileChooser("Wybierz plik...");

        File chosenFile = fileChooser.showOpenDialog(window);
        if(chosenFile != null){
            String fileName = imageAndMatTool.createFileName(imagesList, chosenFile.getName());
            ImageViewStage stage = createNewImageViewStage(fileName, chosenFile);
            stage.show();
        }
    }

    private ImageViewStage createNewImageViewStage(String fileName, File file) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ImageViewStage.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageViewStageController imageViewStageController = loader.getController();
        BufferedImage bufferedImage = imageAndMatTool.loadImage(file);
        imageViewStageController.setImageInScrollPaneImageLeft(bufferedImage);

        Mat mat = Imgcodecs.imread(file.getPath(), CvType.CV_8S);
//         Mat source = Highgui.imread("grayscale.jpg",  Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//        Mat mat = Imgcodecs.imread(file.getPath(), CvType.CV_8U);  //1 kanał

        ImageViewStage stage = new ImageViewStage();
        stage.setScene(new Scene(root));
        stage.setTitle(fileName);
        stage.setFile(file);
        stage.setMat(mat);

        stage.getScene().getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));

        stage.setImageViewStageController(imageViewStageController);
        stage.setImagePath(file.getPath());
        stage.setImageName(fileName);
        stage.setBufferedImage(bufferedImage);
        stage.setExtension(imageAndMatTool.getFileExtension(fileName));

        Image image = imageViewStageController.getImageViewLeft().getImage();
        ImageHistogram imageHistogram = new ImageHistogram(image);
        imageViewStageController.setImageHistogram(imageHistogram);

        loadTabsInTabPane(imageViewStageController);

        imageViewStageObservableList.add(stage);
        return stage;
    }

    private void loadTabsInTabPane(ImageViewStageController imageViewStageController){
        imageViewStageController.createHistogramRGB();
        imageViewStageController.createHistogramMono();
        imageViewStageController.createTabLUT();
    }

    @FXML
    public void saveFileAs(ActionEvent actionEvent) throws IOException {
        if(activeImageViewStage != null) {
            BufferedImage bufferedImage = activeImageViewStage.getBufferedImage();
            String extension = activeImageViewStage.getExtension();

            Window window = anchorPane.getScene().getWindow();
            FileChooser fileChooser = prepareFileChooser("Zapisz plik...");

            File file = fileChooser.showSaveDialog(window);
            if (file != null) {
                ImageIO.write(bufferedImage, extension, file);
            }
            activeImageViewStage.setTitle(file.getName());
            activeImageViewStage.setImageName(file.getName());
            activeImageViewStage.setImagePath(file.getPath());
        }
    }

    @FXML//liniowe rozciaganie histogramu
    public void fullScaleHistogram(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            ImageViewStage stage = duplicateFile();
            ImageViewStageController imageViewStageController = stage.getImageViewStageController();

            ImageHistogram imageHistogramPrimary = imageViewStageController.getImageHistogram();

            long tabColorR[] = imageHistogramPrimary.getRed();
            long tabColorG[] = imageHistogramPrimary.getGreen();
            long tabColorB[] = imageHistogramPrimary.getBlue();

            //wartosci max i min r g b
            int minR = calculateTool.setMinColorValue(tabColorR);
            int maxR = calculateTool.setMaxColorValue(tabColorR);
            int minG = calculateTool.setMinColorValue(tabColorG);
            int maxG = calculateTool.setMaxColorValue(tabColorG);
            int minB = calculateTool.setMinColorValue(tabColorB);
            int maxB = calculateTool.setMaxColorValue(tabColorB);

            //pierworny obraz
            BufferedImage primaryImage = stage.getBufferedImage();
            int height = primaryImage.getHeight();
            int width = primaryImage.getWidth();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int rgb = 0;
            int pixel = 0;
            int red = 0;
            int green = 0;
            int blue = 0;

            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    pixel = primaryImage.getRGB(i, j);
                    red = (pixel >> 16) & 0x000000FF;
                    green = (pixel >> 8) & 0x000000FF;
                    blue = (pixel) & 0x000000FF;
                    //liniowe rozciaganie histogramu
                    //I(x,y) = (I(x,y) - min) * (255/max-min)
                    red = calculateTool.calculateFullScalePixelColor(red, minR, maxR);
                    green = calculateTool.calculateFullScalePixelColor(green, minG, maxG);
                    blue = calculateTool.calculateFullScalePixelColor(blue, minB, maxB);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            imageAndMatTool.changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }
    }

    @FXML
    public void equalizationHistogram(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            ImageViewStage stage = duplicateFile();
            ImageViewStageController imageViewStageController = stage.getImageViewStageController();

            ImageHistogram imageHistogramPrimary = imageViewStageController.getImageHistogram();

            long tabColorR[] = imageHistogramPrimary.getRed();
            long tabColorG[] = imageHistogramPrimary.getGreen();
            long tabColorB[] = imageHistogramPrimary.getBlue();

            //pierworny obraz
            BufferedImage primaryImage = stage.getBufferedImage();
            int height = primaryImage.getHeight();
            int width = primaryImage.getWidth();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int m = 256;
            //D[i]=(H0+H1+…+Hi)/sum
            double tabRedD[] = calculateTool.createTabD(width, height, tabColorR, m);
            double tabGreenD[] = calculateTool.createTabD(width, height, tabColorG, m);
            double tabBlueD[] = calculateTool.createTabD(width, height, tabColorB, m);

            int rgb = 0;
            int pixel = 0;
            int red = 0;
            int green = 0;
            int blue = 0;

            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    pixel = primaryImage.getRGB(i, j);
                    red = (pixel >> 16) & 0x000000FF;
                    green = (pixel >> 8) & 0x000000FF;
                    blue = (pixel) & 0x000000FF;
                    //wyrównanie histogramu
                    //D0 – pierwsza niezerowa wartość
                    //LUT[i]=((D[i]−D0)/(1−D0))*(M−1) = ((20*D[i]/20−2/20)*20/18)
                    red = calculateTool.calculateEqualizationPixelColor(tabRedD, m, red);
                    green = calculateTool.calculateEqualizationPixelColor(tabGreenD, m, green);
                    blue = calculateTool.calculateEqualizationPixelColor(tabBlueD, m, blue);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            imageAndMatTool.changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }
    }

    /**
     *
     * @param title - nazwa pokazywana uzytkownikowi
     * @return
     */
    private FileChooser prepareFileChooser(String title){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.tif")
        );
        return fileChooser;
    }

    /**
     * Powielanie obrazu
     * @return
     * @throws IOException
     */
    @FXML
    public ImageViewStage duplicateFile() {
        if(activeImageViewStage != null) {
            File file = activeImageViewStage.getFile();
            if (file != null) {
                String fileName = imageAndMatTool.createFileName(imagesList, activeImageViewStage.getImageName());//np 4.png
                ImageViewStage stage = createNewImageViewStage(fileName, file);
                stage.show();
                return stage;
            }
        }
        return null;
    }

    @FXML
    public void getActiveImageViewStage(ActionEvent actionEvent) {
        ImageViewStage chooseStage = imagesList.getValue();
        if(chooseStage != null){
            for(ImageViewStage stage:imagesList.getItems()){
                stage.setActiveStage(false);
            }
            chooseStage.setActiveStage(true);
            activeImageViewStage = chooseStage;
        }else{
            activeImageViewStage = null;
        }
    }

    public void closeAllStages(){
        ObservableList<ImageViewStage> list = imagesList.getItems();
        if(!list.isEmpty()){
            for(ImageViewStage stage: list){
                stage.setOpenStage(false);
                stage.close();
            }
        }
        mainStage.close();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void negativeImage(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            ImageViewStage stage = duplicateFile();
            ImageViewStageController imageViewStageController = stage.getImageViewStageController();

            ImageHistogram imageHistogramPrimary = imageViewStageController.getImageHistogram();

            //pierworny obraz
            BufferedImage primaryImage = stage.getBufferedImage();
            int height = primaryImage.getHeight();
            int width = primaryImage.getWidth();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int m = 256;

            int rgb = 0;
            int pixel = 0;
            int red = 0;
            int green = 0;
            int blue = 0;

            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    pixel = primaryImage.getRGB(i, j);
                    red = (pixel >> 16) & 0x000000FF;
                    green = (pixel >> 8) & 0x000000FF;
                    blue = (pixel) & 0x000000FF;
                    //negacja obrazu
                    //q(i,j) = Lmax – p(i,j)
                    red = calculateTool.negativePixelColor(red, m);
                    green = calculateTool.negativePixelColor(green, m);
                    blue = calculateTool.negativePixelColor(blue, m);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            imageAndMatTool.changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }
    }

    @FXML//progowanie binarne z progiem wskazywanym suwakiem
    public void binaryThresholdingImage(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createAlterBinaryThreshold();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    //wykonujemy progowanie
                    ImageViewStage stage = duplicateFile();
                    ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                    //pierworny obraz
                    BufferedImage primaryImage = stage.getBufferedImage();
                    int height = primaryImage.getHeight();
                    int width = primaryImage.getWidth();
                    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    int m = 256;
                    int lMin = 0;

                    int rgb = 0;
                    int pixel = 0;
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for(int i=0; i<width; i++){
                        for(int j=0; j<height; j++){
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //progowanie binarne z progiem wskazywanym suwakiem
                            //q = Lmin dla p<=p1
                            //    Lmax dla p>p1
                            red = calculateTool.binaryThresholdingPixelColor(red, lMin, m-1, ImageAndMatTool.thresholdP1);
                            green = calculateTool.binaryThresholdingPixelColor(green, lMin, m-1, ImageAndMatTool.thresholdP1);
                            blue = calculateTool.binaryThresholdingPixelColor(blue, lMin, m-1, ImageAndMatTool.thresholdP1);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    imageAndMatTool.changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    @FXML//progowanie z zachowaniem poziomow szarosci
    public void levelColorThresholding(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createAlterLevelColorThreshold();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    //wykonujemy progowanie z zachowaniem poziomow szarosci
                    ImageViewStage stage = duplicateFile();
                    ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                    //pierworny obraz
                    BufferedImage primaryImage = stage.getBufferedImage();
                    int height = primaryImage.getHeight();
                    int width = primaryImage.getWidth();
                    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    int rgb = 0;
                    int pixel = 0;
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for(int i=0; i<width; i++){
                        for(int j=0; j<height; j++){
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //progowanie z zachowanie poziomow szarosci z progiem wskazywanym suwakiem
                            //q = p dla p1<=p<=p2
                            //    0 dla p<p1 , p>p2
                            red = calculateTool.levelColorThresholdingPixelColor(red, ImageAndMatTool.thresholdP1, ImageAndMatTool.thresholdP2);
                            green = calculateTool.levelColorThresholdingPixelColor(green, ImageAndMatTool.thresholdP1, ImageAndMatTool.thresholdP2);
                            blue = calculateTool.levelColorThresholdingPixelColor(blue, ImageAndMatTool.thresholdP1, ImageAndMatTool.thresholdP2);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    imageAndMatTool.changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    @FXML//redukcja poziomów szarości przez powtórną kawntyzację z liczbą poziomów szarości wskazaną przez użytkownika
    public void reductionLevelsColors(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createAlterReductionLevelColor();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    //wykonujemy redukcja poziomów szarości przez powtórną kawntyzację z liczbą poziomów szarości wskazaną przez użytkownika,
                    ImageViewStage stage = duplicateFile();
                    ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                    //pierworny obraz
                    BufferedImage primaryImage = stage.getBufferedImage();
                    int height = primaryImage.getHeight();
                    int width = primaryImage.getWidth();
                    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    int rgb = 0;
                    int pixel = 0;
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    //obliczanie progow kwantyzacji
                    int pTab[] = calculateTool.calculateReductionThreshold(ImageAndMatTool.reductionLevel, 255);
                    //obliczanie wartosci dla progow
                    int qTab[] = calculateTool.calculateQ(ImageAndMatTool.reductionLevel, 255);

                    for(int i=0; i<width; i++){
                        for(int j=0; j<height; j++){
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //redukcja poziomów szarości przez powtórną kawntyzację  z liczbą poziomów szarości wskazaną przez z uzytkownika
                            //q = 0   dla p<=p1
                            //    q2  dla p1<p<=p2
                            //    q3  dla p2<p<=p3
                            //    q4  dla p3<p<=p4
                            //    255 dla p4<p<=255
                            red = calculateTool.levelColorReductionPixelColor(red, pTab, qTab);
                            green = calculateTool.levelColorReductionPixelColor(green, pTab, qTab);
                            blue = calculateTool.levelColorReductionPixelColor(blue, pTab, qTab);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    imageAndMatTool.changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    @FXML//rozciaganie hostogramu ze wskazaniem zakresu
    public void scaleHistogramQ1Q2(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createAlterScaleHistogramQ1Q2();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    //wykonujemyaną rozciaganie hostogramu ze wskazaniem zakresu
                    ImageViewStage stage = duplicateFile();
                    ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                    //pierworny obraz
                    BufferedImage primaryImage = stage.getBufferedImage();
                    int height = primaryImage.getHeight();
                    int width = primaryImage.getWidth();
                    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    int rgb = 0;
                    int pixel = 0;
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for(int i=0; i<width; i++){
                        for(int j=0; j<height; j++){
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //redukcja poziomów szarości przez powtórną kawntyzację  z liczbą poziomów szarości wskazaną przez z uzytkownika
                            //q =                          lmin       dla p(i,j) < min
                            //   ((p(i,j)-min)*lmax) / (max-min)      dla min <= p(i,j) <= max
                            //                             lmax       dla p(i,j) > max
                            red = calculateTool.calculateScaleQ1Q2PixelColor(red, ImageAndMatTool.scaleHistogramP1, ImageAndMatTool.scaleHistogramP2, ImageAndMatTool.scaleHistogramQ1, ImageAndMatTool.scaleHistogramQ2);
                            green = calculateTool.calculateScaleQ1Q2PixelColor(green, ImageAndMatTool.scaleHistogramP1, ImageAndMatTool.scaleHistogramP2, ImageAndMatTool.scaleHistogramQ1, ImageAndMatTool.scaleHistogramQ2);
                            blue = calculateTool.calculateScaleQ1Q2PixelColor(blue, ImageAndMatTool.scaleHistogramP1, ImageAndMatTool.scaleHistogramP2, ImageAndMatTool.scaleHistogramQ1, ImageAndMatTool.scaleHistogramQ2);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    imageAndMatTool.changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    @FXML//wygładzania liniowego oparte na typowych maskach wygładzania (uśrednienie, uśrednienie z
//    wagami, filtr gaussowski – przedstawione na wykładzie)
    public void linearBlur(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createLinearBlur();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    Alert alert2 = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        //wygładzania liniowego oparte na typowych maskach wygładzania
                        ImageViewStage stage = duplicateFile();
                        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                        //pierworny obraz
                        Mat primaryMat = stage.getMat();
                        Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());

                        switch (ImageAndMatTool.usingMask) {
                            case A:
                                //uśrednianie
                                Imgproc.blur(primaryMat, newMat, ImageAndMatTool.averageSize, new Point(-1, -1), BORDER_REPLICATE);//3x3 lub 5x5   domyslnie jest 3x3
                                //wartosci brzegowe
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, (int) ImageAndMatTool.averageSize.height, false, null, null);

                                break;
                            case B:
                                //K-pudełkowe
                                int[][] mask = Mask.B.getBMaskK(ImageAndMatTool.blurK);
                                int kBox = mask[1][1];//srodkowy punkt - wartosc K filtru k-pudelkowego
                                //3x3 - rozmiar maski
                                Mat kernel = Mat.ones(3, 3, CvType.CV_32F);
                                double divisor = ((3 * 3) + kBox - 1);//dla 2 jest 10
                                double dividend = 0;

                                for (int i = 0; i < kernel.rows(); i++) {
                                    for (int j = 0; j < kernel.height(); j++) {
                                        double[] m = kernel.get(i, j);

                                        if (i == 1 && j == 1) dividend = kBox;
                                        else dividend = 1;

                                        for (int k = 0; k < m.length; k++) {
                                            m[k] = dividend / divisor;
                                        }
                                        kernel.put(i, j, m);
                                    }
                                }
                                Imgproc.filter2D(primaryMat, newMat, primaryMat.depth(), kernel);
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernel, null);

                                break;
                            case G:
                                //filtr gaussowski
                                Imgproc.GaussianBlur(primaryMat, newMat, new Size(3, 3), 0, 0, BORDER_REPLICATE);//rozmiar 3x3
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, null, null);
                                break;
                        }

                        imageAndMatTool.changeImage(newMat, stage);
                        loadTabsInTabPane(imageViewStageController);
                    }
                }
            }
        }
    }

    @FXML//wyostrzania liniowego oparte na 3 maskach laplasjanowych
    public void sharpenMask(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createSharpenMask();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    Alert alert2 = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        //wyostrzanie liniowe
                        ImageViewStage stage = duplicateFile();
                        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                        //pierworny obraz
                        Mat primaryMat = stage.getMat();
                        Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());
                        Mat kernel = imageAndMatTool.createKernel3x3(ImageAndMatTool.usingMask.getTab());

                        Imgproc.filter2D(primaryMat, newMat, primaryMat.depth(), kernel);

                        //wartosci brzegowe
                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernel, null);

                        imageAndMatTool.changeImage(newMat, stage);
                        loadTabsInTabPane(imageViewStageController);
                    }
                }
            }
        }
    }

    @FXML//kierunkowej detekcji krawędzi w oparciu o maski 8 kierunkowych masek Sobela (podstawowe 8
//kierunków) przestawionych użytkownikowi do wyboru
    public void edgeDirectionSobel(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createEdgeDirectionSobel();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    Alert alert2 = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        ImageViewStage stage = duplicateFile();
                        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                        Mat primaryMat = stage.getMat();
                        Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());
                        Mat kernel = imageAndMatTool.createKernel3x3(ImageAndMatTool.usingMask.getTab());

                        Imgproc.filter2D(primaryMat, newMat, primaryMat.depth(), kernel);

                        //wartosci brzegowe
                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernel, null);


                        imageAndMatTool.changeImage(newMat, stage);
                        loadTabsInTabPane(imageViewStageController);
                    }
                }
            }
        }
    }

    @FXML//kierunkowa detekcja krawedzi Prewitt kierunki E i S
    public void edgeDirectionPrewitt(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createEdgeDirectionPrewitt();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    Alert alert2 = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        ImageViewStage stage = duplicateFile();
                        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                        Mat primaryMat = stage.getMat();
                        Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());

                        Mat newMatKernelS = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());
                        Mat newMatKernelE = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());

                        //operacje na mapie
                        Mat kernelS = imageAndMatTool.createKernel3x3(Mask.PrewittS.getTab());
                        Mat kernelE = imageAndMatTool.createKernel3x3(Mask.PrewittE.getTab());

                        Imgproc.filter2D(primaryMat, newMatKernelS, primaryMat.depth(), kernelS);
                        Imgproc.filter2D(primaryMat, newMatKernelE, primaryMat.depth(), kernelE);

                        for (int i = 0; i < newMatKernelS.rows(); i++) {
                            for (int j = 0; j < newMatKernelS.cols(); j++) {
                                double[] tabS = newMatKernelS.get(i, j);
                                double[] tabE = newMatKernelE.get(i, j);
                                double[] resultTab = new double[tabS.length];

                                for (int k = 0; k < tabS.length; k++) {
                                    if (ImageAndMatTool.valuePrewitt)//dokladna
                                        resultTab[k] = (int) Math.sqrt((Math.pow(tabS[k], 2) + Math.pow(tabE[k], 2)));
                                    else//przyblizona
                                        resultTab[k] = (int) (Math.abs(tabS[k]) + Math.abs(tabE[k]));
                                }
                                newMat.put(i, j, resultTab);
                            }
                        }
                        //wartosci brzegowe
                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernelS, kernelE);

                        imageAndMatTool.changeImage(newMat, stage);
                        loadTabsInTabPane(imageViewStageController);
                    }
                }
            }
        }
    }

    @FXML//kierunkowa detekcja krawedzi Cannyego
    public void edgeDirectionCannyego(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert2 = CreatorDialog.createSelectBorderType();
            Optional<ButtonType> result2 = alert2.showAndWait();
            if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                ImageViewStage stage = duplicateFile();
                ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                Mat primaryMat = stage.getMat();
                Mat primaryMatGray = new Mat();
                Imgproc.cvtColor(primaryMat, primaryMatGray, Imgproc.COLOR_BGR2GRAY);

                Mat newMat = new Mat();
                Imgproc.Canny(primaryMatGray, newMat, 60, 60 * 3);

                //wartosci brzegowe
                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, null, null);

                imageAndMatTool.changeImage(newMat, stage);
                loadTabsInTabPane(imageViewStageController);
            }
        }
    }

    @FXML
    public void universalMedian(ActionEvent actionEvent) {
        if(activeImageViewStage != null) {
            Alert alert = CreatorDialog.createEdgeUniversalMedian();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    Alert alert2 = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        ImageViewStage stage = duplicateFile();
                        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

                        Mat primaryMat = stage.getMat();
                        Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());

                        Imgproc.medianBlur(primaryMat, newMat, ImageAndMatTool.medianSize);

                        //wartosci brzegowe
                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, ImageAndMatTool.medianSize, true, null, null);

                        imageAndMatTool.changeImage(newMat, stage);
                        loadTabsInTabPane(imageViewStageController);
                    }
                }
            }
        }
    }


}
