package pl.przybysz.kamila.controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.przybysz.kamila.dialog.ImageViewStage;
import pl.przybysz.kamila.tools.CreatorDialog;
import pl.przybysz.kamila.tools.ElementsDialog;
import pl.przybysz.kamila.utils.ImageHistogram;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

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

    private static ObservableList<ImageViewStage> imageViewStageObservableList;//elementy dodawane do observable list
	private static ListProperty<ImageViewStage> imageViewStagesListProperty;

	//aktywny stage
    private ImageViewStage activeImageViewStage = null;
    private Stage mainStage;

    //progowanie
    public static int thresholdP1;
    public static int thresholdP2;
    public static int reductionLevel;
    public static int scaleHistogramP1;
    public static int scaleHistogramP2;
    public static int scaleHistogramQ1;
    public static int scaleHistogramQ2;


    //usuniecie z listy
    public static void removeFromImageViewStageObservableList(ImageViewStage imageViewStage){
        imageViewStageObservableList.remove(imageViewStage);
    }

    @FXML
    private void initialize(){
        imageViewStagesListProperty = new SimpleListProperty<>();
        imageViewStageObservableList = FXCollections.observableArrayList();//pusta lista linkedlist
        imagesList.setPromptText("Wybierz obraz...");

        imageViewStagesListProperty.set(imageViewStageObservableList);
        imagesList.itemsProperty().bindBidirectional(imageViewStagesListProperty);
    }

    @FXML
    public void loadFile(ActionEvent actionEvent) {
        Window window = anchorPane.getScene().getWindow();
        FileChooser fileChooser = prepareFileChooser("Wybierz plik...");

        File chosenFile = fileChooser.showOpenDialog(window);
        if(chosenFile != null){
            String fileName = createFileName(chosenFile.getName());
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
        BufferedImage bufferedImage = loadImage(file);
        imageViewStageController.setImageInScrollPaneImageLeft(bufferedImage);


        ImageViewStage stage = new ImageViewStage();
        stage.setScene(new Scene(root));
        stage.setTitle(fileName);
        stage.setFile(file);

        stage.getScene().getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));

        stage.setImageViewStageController(imageViewStageController);
        stage.setImagePath(file.getPath());
        stage.setImageName(fileName);
        stage.setBufferedImage(bufferedImage);
        stage.setExtension(getFileExtension(fileName));

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

    private String getFileExtension(String fileName){
        String[] tab = fileName.split("\\.");
        return tab[tab.length-1];
    }

    private String createFileName(String imageName){
        String fileName = imageName;
        if(!imagesList.getItems().isEmpty()){
            String tempFileName = fileName;
            int number=1;
            for(int i=0; i<imagesList.getItems().size(); i++){
                ImageViewStage stage = imagesList.getItems().get(i);
                if (stage.getImageName().equals(tempFileName)){
                    String[] tab = fileName.split("\\.");
                    String extension = tab[tab.length-1];
                    StringBuilder name = new StringBuilder();
                    for(int j=0; j<(tab.length-1); j++){
                        name.append(tab[j]);
                    }
                    name.append("(").append(number).append(")");
                    name.append(".").append(extension);
                    tempFileName = name.toString();
                    number++;
                    i=-1;
                }
            }
            fileName = tempFileName;
        }
        return fileName;
    }

     public static BufferedImage loadImage(File file) {
        try {
            FileInputStream fileIs = new FileInputStream(file);
            BufferedImage image = null;
            image = ImageIO.read(fileIs);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            int minR = setMin(tabColorR);
            int maxR = setMax(tabColorR);
            int minG = setMin(tabColorG);
            int maxG = setMax(tabColorG);
            int minB = setMin(tabColorB);
            int maxB = setMax(tabColorB);

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
                    red = calculateFullScalePixelColor(red, minR, maxR);
                    green = calculateFullScalePixelColor(green, minG, maxG);
                    blue = calculateFullScalePixelColor(blue, minB, maxB);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            changeImage(newImage, stage);
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
            double tabRedD[] = createTabD(width, height, tabColorR, m);
            double tabGreenD[] = createTabD(width, height, tabColorG, m);
            double tabBlueD[] = createTabD(width, height, tabColorB, m);

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
                    red = calculateEqualizationPixelColor(tabRedD, m, red);
                    green = calculateEqualizationPixelColor(tabGreenD, m, green);
                    blue = calculateEqualizationPixelColor(tabBlueD, m, blue);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }
    }

    /**
     * Funkcja przelicza equalizacje dla danego kanalu
     * @param tabD - tablica histogramu skumulowanego
     * @param m - liczba poziomow jasnosci
     * @param color - wartosc koloru
     * @return
     */
    private int calculateEqualizationPixelColor(double tabD[], int m, int color){
        double temp = ((tabD[color] - tabD[0])/(1-tabD[0])) * (m-1);
        return Math.toIntExact(Math.round(temp));
    }

    /**
     * Funkcja przeliczajaca rozciaganie liniowe dla danego kanalu
     * @param color - wartosc kanalu
     * @param minColor - minimalna wartosc dla danego kanalu
     * @param maxColor - makxumalna wartosc dla danego kanalu
     * @return
     */
    private int calculateFullScalePixelColor(int color, int minColor, int maxColor){
        return  ((color - minColor) * (255/(maxColor - minColor)));
    }

    /**
     * Funkcja zwraca tablice histogramu skumulowanego dla danego kanalu
     * @param width - szerokosc obrazu
     * @param height - wysokosc obrazu
     * @param tabColor - liczba wystapien danego koloru
     * @param m - liczba poziomow jasnosci
     * @return
     */
    private double[] createTabD(int width, int height, long tabColor[], int m){
        double resultTab[] = new double[m];
        long sum;
        double sumPixels = width*height;
        for(int i=0; i<m; i++){
            sum = 0;
            for(int j=0 ; j<=i; j++){
                sum += tabColor[j];
            }
            //D[i]=(H0+H1+…+Hi)/sum
            resultTab[i] = sum/sumPixels;
        }
        return resultTab;
    }

    /**
     * Funkcja podmieniajaca obraz
     * @param bufferedImage - obraz
     * @param stage - okno w ktorym ma byc podmieniona
     */
    private void changeImage(BufferedImage bufferedImage, ImageViewStage stage){
        ImageViewStageController imageViewStageController = stage.getImageViewStageController();
        stage.setBufferedImage(bufferedImage);
        imageViewStageController.setImageInScrollPaneImageLeft(bufferedImage);

        Image image = imageViewStageController.getImageViewLeft().getImage();
        ImageHistogram imageHistogram = new ImageHistogram(image);
        imageViewStageController.setImageHistogram(imageHistogram);
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
                String fileName = createFileName(activeImageViewStage.getImageName());//np 4.png
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

    private int setMin(long tabColor[]){
        for (int i=-1; i<255; i++){
            if(tabColor[i+1] != 0){
                int min = i+1;
                return min;
            }
        }
        return 0;
    }

    private int setMax(long tabColor[]){
        for (int i=256; i>=0; i--){
            if(tabColor[i-1] != 0){
                int max = i-1;
                return max;
            }
        }
        return 0;
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

            long tabColorR[] = imageHistogramPrimary.getRed();
            long tabColorG[] = imageHistogramPrimary.getGreen();
            long tabColorB[] = imageHistogramPrimary.getBlue();

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
                    red = negativePixelColor(red, m);
                    green = negativePixelColor(green, m);
                    blue = negativePixelColor(blue, m);

                    rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    newImage.setRGB(i, j, rgb);
                }
            }
            changeImage(newImage, stage);
            loadTabsInTabPane(imageViewStageController);
        }
    }

    /**
     * Funkcja neguje wartosc dla danego kanalu
     * @param color - wartosc koloru dla danwgo kanalu
     * @param m - liczba poziomow jasnosci
     * @return
     */
    private int negativePixelColor(int color, int m){
        //q(i,j) = Lmax – p(i,j)
        return (m-1) - color;
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
                            red = binaryThresholdingPixelColor(red, lMin, m-1, thresholdP1);
                            green = binaryThresholdingPixelColor(green, lMin, m-1, thresholdP1);
                            blue = binaryThresholdingPixelColor(blue, lMin, m-1, thresholdP1);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    /**
     * Funkcja progowania binarnego dla danego koloru
     * @param color - wartosc koloru dla danego kanalu
     * @param lMin - wartosc minimalna
     * @param lMax - wartosc maksymalna
     * @param threshold - prog
     * @return
     */
    private int binaryThresholdingPixelColor(int color, int lMin, int lMax, int threshold){
        if(color <= threshold){
            return lMin;
        }else
            return lMax;
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
                            red = levelColorThresholdingPixelColor(red, thresholdP1, thresholdP2);
                            green = levelColorThresholdingPixelColor(green, thresholdP1, thresholdP2);
                            blue = levelColorThresholdingPixelColor(blue, thresholdP1, thresholdP2);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    /**
     * Funkcja progowania z zachowanie poziomow szarosci dla danego koloru
     * @param color - wartosc koloru dla danego kanalu
     * @param p1 - prog dolny
     * @param p2 - prog gorny
     * @return
     */
    private int levelColorThresholdingPixelColor(int color, int p1, int p2){
        //q = p dla p1<=p<=p2
        //    0 dla p<p1 , p>p2
        if(color >= p1 && color <= p2)
            return color;
        else
            return 0;
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
                    int pTab[] = calculateReductionThreshold(reductionLevel, 255);
                    //obliczanie wartosci dla progow
                    int qTab[] = calculateQ(reductionLevel, 255);

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
                            red = levelColorReductionPixelColor(red, pTab, qTab);
                            green = levelColorReductionPixelColor(green, pTab, qTab);
                            blue = levelColorReductionPixelColor(blue, pTab, qTab);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    /** Redukcja poziomów szarości dla danego koloru
     * @param color - wartosc koloru
     * @param pTab - wartosci progow p
     * @param qTab - wartosci q
     * @return
     */
    private int levelColorReductionPixelColor(int color, int[] pTab, int[] qTab){
        //redukcja poziomów szarości przez powtórną kawntyzację  z liczbą poziomów szarości wskazaną przez z uzytkownika
        // q = 0   dla p<=p1       64
         //    q2  dla p1<p<=p2    128
         //    q3  dla p2<p<=p3    192
         //    255 dla p3<p<=255
        for(int i=1; i<pTab.length; i++){
            if(color<=pTab[i]){
                return qTab[i-1];
            }
        }
        return 0;
    }

    private int[] calculateQ(int size, int lMax){
        int resultTab[] = new int[size];
        int temp = 0;
        resultTab[0] = 0;
        for(int i=1; i<(size-1); i++){
            temp = Math.round(   (lMax *i)/(size-1)    );
            resultTab[i] = temp;
        }
        resultTab[size-1] = 255;
        return resultTab;
    }

    /**
     * Funkcja oblicza wartosc progow
     * @param levels - ilosc poziomow szarosci wskazanych przez uzytkownika
     * @param lMax - maksymalny poziom szarosci
     * @return
     */
    private int[] calculateReductionThreshold(int levels, int lMax){
        int resultTab[] = new int[(levels+1)];
        int temp = 0;
        resultTab[0] = 0;
        for(int i=1; i<levels; i++){
            temp = Math.round((lMax*i)/levels);
            System.out.println("prog: " + temp);
            resultTab[i] = temp;
        }
        resultTab[levels] = lMax;
        return resultTab;
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
                            red = calculateScaleQ1Q2PixelColor(red, scaleHistogramP1, scaleHistogramP2, scaleHistogramQ1, scaleHistogramQ2);
                            green = calculateScaleQ1Q2PixelColor(green, scaleHistogramP1, scaleHistogramP2, scaleHistogramQ1, scaleHistogramQ2);
                            blue = calculateScaleQ1Q2PixelColor(blue, scaleHistogramP1, scaleHistogramP2, scaleHistogramQ1, scaleHistogramQ2);

                            rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                            newImage.setRGB(i, j, rgb);
                        }
                    }
                    changeImage(newImage, stage);
                    loadTabsInTabPane(imageViewStageController);
                }
            }
        }
    }

    /**
     * Funkcja przeliczajaca rozciaganie histogramu dla podanego zakresu dla danego kanalu
     * @param color - wartosc koloru piksela
     * @param p1 - początek zakresu ktory chce rozciagnac
     * @param p2 - koniec zakresu ktory chce rozciagnac
     * @param q1 - początek zakresu do ktorego chce rozciagnac
     * @param q2 - koniec zakresu do ktorego chce rozciagnac
     * @return
     */
    private int calculateScaleQ1Q2PixelColor(int color, int p1, int p2, int q1, int q2){
        //q =                          Lmin       dla p(i,j) < min
        //   ((p(i,j)-min)*Lmax) / (max-min)      dla min <= p(i,j) <= max
        //                             Lmax       dla p(i,j) > max
        //Lmin i Lmax - nowy zakres
        //min i max - rozciagany zakres
        if (color < p1){
            return q1;
        }else if (color > p2){
            return q2;
        }else{
            return Math.round((((color - p1) * q2) / (p2 - p1)));
        }
    }



}
