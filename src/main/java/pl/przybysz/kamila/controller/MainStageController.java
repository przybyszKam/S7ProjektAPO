package pl.przybysz.kamila.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pl.przybysz.kamila.dialog.ImageViewStage;
import pl.przybysz.kamila.enums.FunctionName;
import pl.przybysz.kamila.enums.Mask;
import pl.przybysz.kamila.tools.CalculateTool;
import pl.przybysz.kamila.tools.CreatorDialog;
import pl.przybysz.kamila.tools.FFTTool;
import pl.przybysz.kamila.tools.ImageAndMatTool;
import pl.przybysz.kamila.utils.ImageHistogram;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class MainStageController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<ImageViewStage> imagesList;

    private final BooleanProperty needsImage = new SimpleBooleanProperty(true);

    private CalculateTool calculateTool;
    private ImageAndMatTool imageAndMatTool;
    private FFTTool fftTool;

    private static ObservableList<ImageViewStage> imageViewStageObservableList;//elementy dodawane do observable list
    private static ListProperty<ImageViewStage> imageViewStagesListProperty;

    //aktywny stage
    private ImageViewStage activeImageViewStage = null;
    private Stage mainStage;

    //weryfikacja czy obraz
    public boolean getNeedsImage() {
        if (activeImageViewStage == null) setNeedsImage(true);
        else setNeedsImage(false);
        return needsImage.get();
    }

    public BooleanProperty needsImageProperty() {
        return needsImage;
    }

    public void setNeedsImage(boolean needsImage) {
        this.needsImage.set(needsImage);
    }

    //usuniecie z listy
    public static void removeFromImageViewStageObservableList(ImageViewStage imageViewStage) {
        imageViewStageObservableList.remove(imageViewStage);
    }

    @FXML
    private void initialize() {
        setTools();
        imageViewStagesListProperty = new SimpleListProperty<>();
        imageViewStageObservableList = FXCollections.observableArrayList();//pusta lista linkedlist
        imagesList.setPromptText("Wybierz obraz...");

        imageViewStagesListProperty.set(imageViewStageObservableList);
        imagesList.itemsProperty().bindBidirectional(imageViewStagesListProperty);
    }

    private void setTools() {
        this.imageAndMatTool = new ImageAndMatTool();
        this.calculateTool = new CalculateTool();
        this.fftTool = new FFTTool();
    }

    @FXML
    public void loadFile(ActionEvent actionEvent) {
        Window window = anchorPane.getScene().getWindow();
        FileChooser fileChooser = prepareFileChooser("Wybierz plik...");

        File chosenFile = fileChooser.showOpenDialog(window);
        if (chosenFile != null) {
            String fileName = imageAndMatTool.createFileName(imagesList, chosenFile.getName());
            ImageViewStage stage = createNewImageViewStage(fileName, chosenFile);
            stage.show();
        }
    }

    /**
     * @param title - nazwa pokazywana uzytkownikowi
     * @return
     */
    private FileChooser prepareFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.tif")
        );
        return fileChooser;
    }

    /**
     * Powielanie obrazu
     *
     * @return
     * @throws IOException
     */
    @FXML
    public ImageViewStage duplicateFile() {
        return duplicateFile(true);
    }
    public ImageViewStage duplicateFile(boolean show) {
        File file = activeImageViewStage.getFile();
        if (file != null) {
            String fileName = imageAndMatTool.createFileName(imagesList, activeImageViewStage.getImageName());//np 4.png
            ImageViewStage stage = createNewImageViewStage(fileName, file);
            stage.setMat(activeImageViewStage.getMat());
            stage.setBufferedImage(activeImageViewStage.getBufferedImage());
            stage.getImageViewStageController().getImageViewLeft().setImage(activeImageViewStage.getImageViewStageController().getImageViewLeft().getImage());
            if(show == true)
                stage.show();
            //FFT
            stage.setComplexImage(activeImageViewStage.getComplexImage());
            stage.setPlanes(activeImageViewStage.getPlanes());
            stage.setMagnitude(activeImageViewStage.isMagnitude());
            return stage;
        }
        return null;
    }

    @FXML
    public void getActiveImageViewStage(ActionEvent actionEvent) {
        ImageViewStage chooseStage = imagesList.getValue();
        if (chooseStage != null) {
            for (ImageViewStage stage : imagesList.getItems()) {
                stage.setActiveStage(false);
            }
            chooseStage.setActiveStage(true);
            activeImageViewStage = chooseStage;
        } else {
            activeImageViewStage = null;
        }
    }

    public void closeAllStages() {
        ObservableList<ImageViewStage> list = imagesList.getItems();
        if (!list.isEmpty()) {
            for (ImageViewStage stage : list) {
                stage.setOpenStage(false);
                stage.close();
            }
        }
        mainStage.close();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
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

    private void loadTabsInTabPane(ImageViewStageController imageViewStageController) {
        imageViewStageController.createHistogramRGB();
        imageViewStageController.createHistogramMono();
        imageViewStageController.createTabLUT();
    }

    @FXML
    public void saveFileAs(ActionEvent actionEvent) throws IOException {
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

    private void switchFunctionName(FunctionName functionName, boolean operationOnMat, boolean openAlert){
        Alert alert = null;
        Optional<ButtonType> result = null;
        Mat binaryOperationMat = null;

        if(openAlert){
            switch (functionName){
                case BINARY_THRESHOLDING_IMAGE:
                    alert = CreatorDialog.createAlterBinaryThreshold();
                break;
                case LEVEL_COLOR_THRESHOLDING:
                    alert = CreatorDialog.createAlterLevelColorThreshold();
                break;
                case REDUCTION_LEVELS_COLORS:
                    alert = CreatorDialog.createAlterReductionLevelColor();
                    break;
                case SCALE_HISTOGRAM_Q1_Q2:
                    alert = CreatorDialog.createAlterScaleHistogramQ1Q2();
                    break;
                case LINEAR_BLUR:
                    alert = CreatorDialog.createLinearBlur();
                    break;
                case SHARPEN_MASK:
                    alert = CreatorDialog.createSharpenMask();
                    break;
                case EDGE_DIRECTION_SOBEL:
                    alert = CreatorDialog.createEdgeDirectionSobel();
                    break;
                case EDGE_DIRECTION_PREWITT:
                    alert = CreatorDialog.createEdgeDirectionPrewitt();
                    break;
                case UNIVERSAL_MEDIAN:
                    alert = CreatorDialog.createEdgeUniversalMedian();
                    break;
                case BINARY_OPERATION_AND:
                case BINARY_OPERATION_OR:
                case BINARY_OPERATION_XOR:{
                    Window window = anchorPane.getScene().getWindow();
                    FileChooser fileChooser = prepareFileChooser("Wybierz plik...");

                    File chosenFile = fileChooser.showOpenDialog(window);
                    if (chosenFile != null) {
                        binaryOperationMat = Imgcodecs.imread(chosenFile.getPath(), CvType.CV_8S);
                    }else
                        return;
                }
                break;
            }
            if(alert!=null)
                result = alert.showAndWait();
        }

        ImageViewStage stage = duplicateFile(false);
        ImageViewStageController imageViewStageController = stage.getImageViewStageController();

        if(!operationOnMat){//operacja nie na mat
            ImageHistogram imageHistogramPrimary = imageViewStageController.getImageHistogram();

            //pierworny obraz
            BufferedImage primaryImage = stage.getBufferedImage();
            int height = primaryImage.getHeight();
            int width = primaryImage.getWidth();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            long tabColorR[] = imageHistogramPrimary.getRed();
            long tabColorG[] = imageHistogramPrimary.getGreen();
            long tabColorB[] = imageHistogramPrimary.getBlue();

            int rgb = 0;
            int pixel = 0;
            int red = 0;
            int green = 0;
            int blue = 0;
            int m = 256;

            switch (functionName) {
                case FULL_SCALE_HISTOGRAM: {
                    //wartosci max i min r g b
                    int minR = calculateTool.setMinColorValue(tabColorR);
                    int maxR = calculateTool.setMaxColorValue(tabColorR);
                    int minG = calculateTool.setMinColorValue(tabColorG);
                    int maxG = calculateTool.setMaxColorValue(tabColorG);
                    int minB = calculateTool.setMinColorValue(tabColorB);
                    int maxB = calculateTool.setMaxColorValue(tabColorB);

                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //liniowe rozciaganie histogramu
                            //I(x,y) = (I(x,y) - min) * (255/max-min)
                            red = calculateTool.calculateFullScalePixelColor(red, minR, maxR);
                            green = calculateTool.calculateFullScalePixelColor(green, minG, maxG);
                            blue = calculateTool.calculateFullScalePixelColor(blue, minB, maxB);

                            rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                }
                break;
                case EQUALIZATION_HISTOGRAM: {
                    //D[i]=(H0+H1+…+Hi)/sum
                    double tabRedD[] = calculateTool.createTabD(width, height, tabColorR, m);
                    double tabGreenD[] = calculateTool.createTabD(width, height, tabColorG, m);
                    double tabBlueD[] = calculateTool.createTabD(width, height, tabColorB, m);

                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
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

                            rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                }
                break;
                case NEGATIVE_IMAGE:{
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            pixel = primaryImage.getRGB(i, j);
                            red = (pixel >> 16) & 0x000000FF;
                            green = (pixel >> 8) & 0x000000FF;
                            blue = (pixel) & 0x000000FF;
                            //negacja obrazu
                            //q(i,j) = Lmax – p(i,j)
                            red = calculateTool.negativePixelColor(red, m);
                            green = calculateTool.negativePixelColor(green, m);
                            blue = calculateTool.negativePixelColor(blue, m);

                            rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                }
                break;
                case BINARY_THRESHOLDING_IMAGE:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            int lMin = 0;
                            for (int i = 0; i < width; i++) {
                                for (int j = 0; j < height; j++) {
                                    pixel = primaryImage.getRGB(i, j);
                                    red = (pixel >> 16) & 0x000000FF;
                                    green = (pixel >> 8) & 0x000000FF;
                                    blue = (pixel) & 0x000000FF;
                                    //progowanie binarne z progiem wskazywanym suwakiem
                                    //q = Lmin dla p<=p1
                                    //    Lmax dla p>p1
                                    red = calculateTool.binaryThresholdingPixelColor(red, lMin, m - 1, ImageAndMatTool.thresholdP1);
                                    green = calculateTool.binaryThresholdingPixelColor(green, lMin, m - 1, ImageAndMatTool.thresholdP1);
                                    blue = calculateTool.binaryThresholdingPixelColor(blue, lMin, m - 1, ImageAndMatTool.thresholdP1);

                                    rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                                    newImage.setRGB(i, j, rgb);
                                }
                            }
                        }
                    }
                }
                break;
                case LEVEL_COLOR_THRESHOLDING:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            for (int i = 0; i < width; i++) {
                                for (int j = 0; j < height; j++) {
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

                                    rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                                    newImage.setRGB(i, j, rgb);
                                }
                            }
                        }
                    }
                }
                break;
                case REDUCTION_LEVELS_COLORS: {
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            //obliczanie progow kwantyzacji
                            int pTab[] = calculateTool.calculateReductionThreshold(ImageAndMatTool.reductionLevel, 255);
                            //obliczanie wartosci dla progow
                            int qTab[] = calculateTool.calculateQ(ImageAndMatTool.reductionLevel, 255);

                            for (int i = 0; i < width; i++) {
                                for (int j = 0; j < height; j++) {
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

                                    rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                                    newImage.setRGB(i, j, rgb);
                                }
                            }
                        }
                    }
                }
                break;
                case SCALE_HISTOGRAM_Q1_Q2:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            for (int i = 0; i < width; i++) {
                                for (int j = 0; j < height; j++) {
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

                                    rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
                                    newImage.setRGB(i, j, rgb);
                                }
                            }
                        }
                    }
                }
                break;
            }
            imageAndMatTool.changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }else {//operacje na Mat
            //pierwotny obraz
            Mat primaryMat = stage.getMat();
            Mat newMat = new Mat(primaryMat.height(), primaryMat.width(), primaryMat.type());
            switch (functionName){
                case LINEAR_BLUR:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                            Alert alertBorderType = CreatorDialog.createSelectBorderType();
                            Optional<ButtonType> resultBorderType = alertBorderType.showAndWait();
                            if (resultBorderType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                                switch (ImageAndMatTool.usingMask) {
                                    case A:
                                        //uśrednianie
                                        Imgproc.blur(primaryMat, newMat, ImageAndMatTool.averageSize, new Point(-1, -1), Core.BORDER_REPLICATE);//3x3 lub 5x5   domyslnie jest 3x3
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
                                        Imgproc.GaussianBlur(primaryMat, newMat, new Size(3, 3), 0, 0, Core.BORDER_REPLICATE);//rozmiar 3x3
                                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, null, null);
                                        break;
                                }
                            }
                        }
                    }
                }
                break;
                case EDGE_DIRECTION_SOBEL:
                case SHARPEN_MASK:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            Alert alertBorderType = CreatorDialog.createSelectBorderType();
                            Optional<ButtonType> resultBorderType = alertBorderType.showAndWait();
                            if (resultBorderType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                                Mat kernel = imageAndMatTool.createKernel3x3(ImageAndMatTool.usingMask.getTab());

                                Imgproc.filter2D(primaryMat, newMat, primaryMat.depth(), kernel);
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernel, null);
                            }
                        }
                    }
                }
                break;
                case EDGE_DIRECTION_PREWITT:{
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                            Alert alertBorderType = CreatorDialog.createSelectBorderType();
                            Optional<ButtonType> resultBorderType = alertBorderType.showAndWait();
                            if (resultBorderType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
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
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, kernelS, kernelE);
                            }
                        }
                    }
                }
                break;
                case EDGE_DIRECTION_CANNYEGO:{
                    Alert alertBorderType = CreatorDialog.createSelectBorderType();
                    Optional<ButtonType> resultBorderType = alertBorderType.showAndWait();
                    if (resultBorderType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        Mat primaryMatGray = new Mat();
                        newMat = new Mat();

                        Imgproc.cvtColor(primaryMat, primaryMatGray, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.Canny(primaryMatGray, newMat, 60, 60 * 3);
                        imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, 3, false, null, null);
                    }
                }
                break;
                case UNIVERSAL_MEDIAN: {
                    if (result.isPresent()) {
                        if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                            Alert alertBorderTyp = CreatorDialog.createSelectBorderType();
                            Optional<ButtonType> resultBorderTyp = alertBorderTyp.showAndWait();
                            if (resultBorderTyp.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                                Imgproc.medianBlur(primaryMat, newMat, ImageAndMatTool.medianSize);
                                imageAndMatTool.fillBorder(primaryMat, newMat, ImageAndMatTool.borderType, ImageAndMatTool.medianSize, true, null, null);
                            }
                        }
                    }
                }
                break;
                case FAST_FOURIER_TRANSFORMATION:{
                    Mat primaryMatGray = new Mat();
                    if(primaryMat.channels() != 1)
                        Imgproc.cvtColor(primaryMat, primaryMatGray, Imgproc.COLOR_RGB2GRAY);
                    else
                        primaryMatGray = primaryMat.clone();

                    Mat padded = fftTool.optimizeImageDim(primaryMatGray);
                    padded.convertTo(padded, CvType.CV_32F);

                    List<Mat> planes = new ArrayList<>();
                    planes.add(padded);
                    planes.add(Mat.zeros(padded.size(), CvType.CV_32F));

                    Mat complexImage = new Mat();
                    Core.merge(planes, complexImage);

                    Core.dft(complexImage, complexImage);
                    stage.setComplexImage(complexImage);
                    stage.setPlanes(planes);
                    stage.setMagnitude(true);
                    Mat magnitude = fftTool.createOptimizedMagnitude(complexImage);

                    newMat = magnitude.clone();
                }
                break;
                case INVERSE_FAST_FOURIER_TRANSFORMATION:{
                    if(!stage.isMagnitude()){
                        stage.close();
                        MainStageController.removeFromImageViewStageObservableList(stage);
                        return;
                    }
                    stage.setMagnitude(false);
                    Mat complexImage = stage.getComplexImage();
                    List<Mat> planes = stage.getPlanes();

                    Core.idft(complexImage, complexImage);
                    Mat restoredImage = new Mat();
                    Core.split(complexImage, planes);
                    Core.normalize(planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX);
                    // move back the Mat to 8 bit, in order to proper show the result
                    restoredImage.convertTo(restoredImage, CvType.CV_8U);
                    newMat = restoredImage.clone();
                }
                break;
                case BINARY_OPERATION_AND:{
                    //sprawdzenie rizmiaru obrazu, ilosc kanlow
                    if(primaryMat.width()!=binaryOperationMat.width() || primaryMat.height()!=binaryOperationMat.height() || primaryMat.type()!=binaryOperationMat.type())
                        return;//dodac pozniej informacje o nieprawidlowym obrazie
                    newMat = new Mat();

                    Core.bitwise_and(primaryMat, binaryOperationMat, newMat);
                }
                break;
                case BINARY_OPERATION_OR:{
                    if(primaryMat.width()!=binaryOperationMat.width() || primaryMat.height()!=binaryOperationMat.height() || primaryMat.type()!=binaryOperationMat.type())
                        return;//dodac pozniej informacje o nieprawidlowym obrazie
                    newMat = new Mat();

                    Core.bitwise_or(primaryMat, binaryOperationMat, newMat);
                }
                break;
                case BINARY_OPERATION_XOR:{
                    if(primaryMat.width()!=binaryOperationMat.width() || primaryMat.height()!=binaryOperationMat.height() || primaryMat.type()!=binaryOperationMat.type())
                        return;//dodac pozniej informacje o nieprawidlowym obrazie
                    newMat = new Mat();

                    Core.bitwise_xor(primaryMat, binaryOperationMat, newMat);
                }
                break;
                case SEGMENT_IMAGE_ADAPTATION:{
                    newMat = new Mat();
                    Mat grayScale = new Mat();
                    Imgproc.cvtColor(primaryMat, grayScale, Imgproc.COLOR_RGB2GRAY);
                    Imgproc.adaptiveThreshold(grayScale, newMat, 125, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 12);
                }
                break;
                case SEGMENT_IMAGE_OTSU:{
                    newMat = new Mat();
                    Mat grayScale = new Mat();
                    Imgproc.cvtColor(primaryMat, grayScale, Imgproc.COLOR_RGB2GRAY);
                    Imgproc.threshold(grayScale, newMat, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
                }
                break;
                case WATERSHED:{
                    Mat img = primaryMat.clone();

                    Mat threeChannel = new Mat();

                    Imgproc.cvtColor(img, threeChannel, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.threshold(threeChannel, threeChannel, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

                    Mat fg = new Mat(img.size(),CvType.CV_8U);
                    Imgproc.erode(threeChannel,fg,new Mat());

                    Mat bg = new Mat(img.size(),CvType.CV_8U);
                    Imgproc.dilate(threeChannel,bg,new Mat());
                    Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);

                    Mat markers = new Mat(img.size(),CvType.CV_8U, new Scalar(0));
                    Core.add(fg, bg, markers);
                    Imgproc.cvtColor(img, img, Imgproc.COLOR_BGRA2BGR);
                    Mat markers2 = new Mat();

                    markers.convertTo(markers2, CvType.CV_32SC1);

                    Imgproc.watershed(img,markers2);
                    markers2.convertTo(markers2,CvType.CV_8U);

                    newMat = markers2.clone();
                }
                break;
            }
            imageAndMatTool.changeImage(newMat, stage);
            loadTabsInTabPane(imageViewStageController);
        }
        stage.show();
    }

    @FXML//liniowe rozciaganie histogramu
    public void fullScaleHistogram() {
        switchFunctionName(FunctionName.FULL_SCALE_HISTOGRAM, false, false);
    }

    @FXML
    public void equalizationHistogram() {
        switchFunctionName(FunctionName.EQUALIZATION_HISTOGRAM, false, false);
    }

    @FXML
    public void negativeImage() {
        switchFunctionName(FunctionName.NEGATIVE_IMAGE, false, false);
    }

    @FXML//progowanie binarne z progiem wskazywanym suwakiem
    public void binaryThresholdingImage() {
        switchFunctionName(FunctionName.BINARY_THRESHOLDING_IMAGE, false, true);
    }

    @FXML//progowanie z zachowaniem poziomow szarosci
    public void levelColorThresholding() {
        switchFunctionName(FunctionName.LEVEL_COLOR_THRESHOLDING, false, true);
    }

    @FXML//redukcja poziomów szarości przez powtórną kawntyzację z liczbą poziomów szarości wskazaną przez użytkownika
    public void reductionLevelsColors() {
        switchFunctionName(FunctionName.REDUCTION_LEVELS_COLORS, false, true);
    }

    @FXML//rozciaganie hostogramu ze wskazaniem zakresu
    public void scaleHistogramQ1Q2() {
        switchFunctionName(FunctionName.SCALE_HISTOGRAM_Q1_Q2, false, true);
    }

    @FXML//wygładzania liniowego oparte na typowych maskach wygładzania (uśrednienie, uśrednienie z
//    wagami, filtr gaussowski – przedstawione na wykładzie)
    public void linearBlur() {
        switchFunctionName(FunctionName.LINEAR_BLUR, true, true);
    }

    @FXML//wyostrzania liniowego oparte na 3 maskach laplasjanowych
    public void sharpenMask() {
        switchFunctionName(FunctionName.SHARPEN_MASK, true, true);
    }

    @FXML//kierunkowej detekcji krawędzi w oparciu o maski 8 kierunkowych masek Sobela (podstawowe 8
//kierunków) przestawionych użytkownikowi do wyboru
    public void edgeDirectionSobel() {
        switchFunctionName(FunctionName.EDGE_DIRECTION_SOBEL, true, true);
    }

    @FXML//kierunkowa detekcja krawedzi Prewitt kierunki E i S
    public void edgeDirectionPrewitt() {
        switchFunctionName(FunctionName.EDGE_DIRECTION_PREWITT, true, true);
    }

    @FXML//kierunkowa detekcja krawedzi Cannyego
    public void edgeDirectionCannyego() {
        switchFunctionName(FunctionName.EDGE_DIRECTION_CANNYEGO, true, false);
    }

    @FXML
    public void universalMedian() {
        switchFunctionName(FunctionName.UNIVERSAL_MEDIAN, true, true);
    }


    @FXML
    public void binaryOperationAND() {
        switchFunctionName(FunctionName.BINARY_OPERATION_AND, true, true);
    }

    @FXML
    public void binaryOperationOR() {
        switchFunctionName(FunctionName.BINARY_OPERATION_OR, true, true);
    }

    @FXML
    public void binaryOperationXOR() {
        switchFunctionName(FunctionName.BINARY_OPERATION_XOR, true, true);
    }

    @FXML
    public void segmentImageOtsu() {
        switchFunctionName(FunctionName.SEGMENT_IMAGE_OTSU, true, false);
    }

    @FXML
    public void watershed() {
        switchFunctionName(FunctionName.WATERSHED, true, false);
    }


    @FXML
    public void segmentImageAdaptation() {
        switchFunctionName(FunctionName.SEGMENT_IMAGE_ADAPTATION, true, false);
    }




    //Projekt
    @FXML//wtkonanie transformaty - wynikiem jest widmo amplitudowe
    public void fastFourierTransformation() {
        switchFunctionName(FunctionName.FAST_FOURIER_TRANSFORMATION, true, false);
    }

    @FXML
    public void inverseFastFourierTransformation() {
        switchFunctionName(FunctionName.INVERSE_FAST_FOURIER_TRANSFORMATION, true, false);
    }

    @FXML
    public void modifyFastFourierTransformation(ActionEvent actionEvent) {
        //rysowanie po obrazie tylko jesli to jest widmo
    }


}
