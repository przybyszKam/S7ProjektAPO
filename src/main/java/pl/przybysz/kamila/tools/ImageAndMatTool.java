package pl.przybysz.kamila.tools;

import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import pl.przybysz.kamila.controller.ImageViewStageController;
import pl.przybysz.kamila.dialog.ImageViewStage;
import pl.przybysz.kamila.enums.BorderType;
import pl.przybysz.kamila.enums.EditMagnitudeType;
import pl.przybysz.kamila.enums.Mask;
import pl.przybysz.kamila.enums.SegmentationOption;
import pl.przybysz.kamila.utils.ImageHistogram;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ImageAndMatTool {

    public static int thresholdP1;
    public static int thresholdP2;
    public static int reductionLevel;
    public static int scaleHistogramP1;
    public static int scaleHistogramP2;
    public static int scaleHistogramQ1;
    public static int scaleHistogramQ2;
    public static int blurK;
    public static boolean valuePrewitt;//true - dokładna, false - Przybliżona
    public static Mask usingMask;
    public static Size averageSize;
    public static int medianSize;
    public static BorderType borderType;
    public static int arbitraryValue;
    public static SegmentationOption segmentationOption;
    public static EditMagnitudeType editMagnitudeType;

    //projekt
    public static int P1X;
    public static int P1Y;
    public static int P2X;
    public static int P2Y;

    public Mat createKernel3x3(int[][] mask){
        Mat kernel = new Mat(3,3, CvType.CV_32F) {
            {
                put(0,0, mask[0][0]);
                put(0,1, mask[0][1]);
                put(0,2, mask[0][2]);

                put(1,0, mask[1][0]);
                put(1,1,mask[1][1]);
                put(1,2,mask[1][2]);

                put(2,0, mask[2][0]);
                put(2,1, mask[2][1]);
                put(2,2, mask[2][2]);
            }
        };
        return kernel;
    }

    public String getFileExtension(String fileName){
        String[] tab = fileName.split("\\.");
        return tab[tab.length-1];
    }

    public String createFileName(ComboBox<ImageViewStage> imagesList, String imageName){
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

    /**
     * Funkcja podmieniajaca obraz
     * @param bufferedImage - obraz
     * @param stage - okno w ktorym ma byc podmieniona
     */
    public void changeImage(BufferedImage bufferedImage, ImageViewStage stage){
        ImageViewStageController imageViewStageController = stage.getImageViewStageController();
        stage.setBufferedImage(bufferedImage);
        imageViewStageController.setImageInScrollPaneImageLeft(bufferedImage);

        Image image = imageViewStageController.getImageViewLeft().getImage();
        ImageHistogram imageHistogram = new ImageHistogram(image);
        imageViewStageController.setImageHistogram(imageHistogram);
    }

    /**
     * Funkcja podmieniajaca obraz
     * @param mat - obraz
     * @param stage - okno w ktorym ma byc podmieniony
     */
    public void changeImage(Mat mat, ImageViewStage stage){
        ImageViewStageController imageViewStageController = stage.getImageViewStageController();
        stage.setMat(mat);
        Mat tmpMat = mat.clone();

        java.awt.Image bufferedImage = HighGui.toBufferedImage(tmpMat);
        imageViewStageController.setImageInScrollPaneImageLeft((BufferedImage) bufferedImage);
        stage.setBufferedImage((BufferedImage)bufferedImage);

        Image image = imageViewStageController.getImageViewLeft().getImage();
        ImageHistogram imageHistogram = new ImageHistogram(image);
        imageViewStageController.setImageHistogram(imageHistogram);
    }

    public void fillBorder(Mat primaryMat, Mat newMat, BorderType borderType, int borderSize, boolean isUniversalMedian, Mat kernel1, Mat kernel2){
        int iMax = primaryMat.rows()-1;//max wysokosc rows
        int jMax = primaryMat.cols()-1;//max szerokosc cols

        switch (borderType){
            case PIXEL_WITHOUT_CHANGE://przepisanie wartosci z pierwotnego obrazu
                setPixelWithoutChange(primaryMat, newMat, borderSize, iMax, jMax);
                break;
            case REFLECT://powielenie skrajnych wierszy i kolumn
                setPixelReflect(primaryMat,newMat, borderSize, iMax, jMax);
                break;
            case ARBITRARY_VALUE://arbitralna wartosc zadana przez uzytkownika
                setPixelArbitraryValue(primaryMat, newMat, borderSize, iMax, jMax, arbitraryValue);
                break;
            case PIXEL_FROM_PROXIMITY://sasiedztwo
                if(isUniversalMedian)
                    setPixelFromProximityUniversalMedian(primaryMat, newMat, borderSize, iMax, jMax);
//                else {
//
//                }
                break;
        }

    }

    /**
     * Piksele bez zmian
     * @param primaryMat - obraz pierwotny
     * @param newMat - obraz wynikowy
     * @param borderSize - rozmiar otoczenia
     */
    private void setPixelWithoutChange(Mat primaryMat, Mat newMat, int borderSize, int iMax, int jMax){
        //gora dol
        for(int j=0; j<primaryMat.cols(); j++) {
            if (borderSize >= 3) {
                setPixelWithoutChange(primaryMat, newMat, 0, j);
                setPixelWithoutChange(primaryMat, newMat, iMax, j);
            }
            if (borderSize >= 5){
                setPixelWithoutChange(primaryMat, newMat, 1, j);
                setPixelWithoutChange(primaryMat, newMat, iMax-1, j);
            }
            if (borderSize >= 7){
                setPixelWithoutChange(primaryMat, newMat, 2, j);
                setPixelWithoutChange(primaryMat, newMat, iMax-2, j);
            }
            if(borderSize >= 9){
                setPixelWithoutChange(primaryMat, newMat, 3, j);
                setPixelWithoutChange(primaryMat, newMat, iMax-3, j);
            }
        }

        //lewo prawo
            for(int i=0; i<primaryMat.rows(); i++){
                if (borderSize >= 3) {
                    setPixelWithoutChange(primaryMat, newMat, i, 0);
                    setPixelWithoutChange(primaryMat, newMat, i, jMax);
                }
                if (borderSize >= 5){
                    setPixelWithoutChange(primaryMat, newMat, i, 1);
                    setPixelWithoutChange(primaryMat, newMat, i, jMax-1);
                }
                if (borderSize >= 7){
                    setPixelWithoutChange(primaryMat, newMat, i, 2);
                    setPixelWithoutChange(primaryMat, newMat, i, jMax-2);
                }
                if(borderSize >= 9){
                    setPixelWithoutChange(primaryMat, newMat, i, 3);
                    setPixelWithoutChange(primaryMat, newMat, i, jMax-3);
                }
            }
    }

    /**
     * @param primaryMat - obraz podstawowy
     * @param newMat  - obraz wyniowy
     * @param row - numer wiersza
     * @param col - numer kolumny
     */
    private void setPixelWithoutChange( Mat primaryMat, Mat newMat, int row, int col){
        double[] tab = primaryMat.get(row,col);
        double[] resultTab = new double[tab.length];

        for(int k=0; k<tab.length; k++){
            resultTab[k] = tab[k];
        }
        newMat.put(row,col,resultTab);
    }

     /**
         * Piksele jako powielenie skrajnych wierszy i kolumn
         * @param primaryMat - obraz pierwotny
         * @param newMat - obraz wynikowy
         * @param borderSize - rozmiar otoczenia
         */
    private void setPixelReflect(Mat primaryMat, Mat newMat, int borderSize, int iMax, int jMax){
            //gora dol
        for(int j=0; j<primaryMat.cols(); j++) {
            if (borderSize >= 3) {
                setPixelReflect(primaryMat, newMat, borderSize-1, j, 0, j);
                setPixelReflect(primaryMat, newMat, iMax-(borderSize-1), j, iMax, j);
            }
            if (borderSize >= 5){
                setPixelReflect(primaryMat, newMat, borderSize-2, j, 1, j);
                setPixelReflect(primaryMat, newMat, iMax-(borderSize-2), j, iMax-1, j);
            }
            if (borderSize >= 7){
                setPixelReflect(primaryMat, newMat, borderSize-3, j, 2, j);
                setPixelReflect(primaryMat, newMat, iMax-(borderSize-3), j, iMax-2, j);
            }
            if(borderSize >= 9){
                setPixelReflect(primaryMat, newMat, borderSize-4, j, 3, j);
                setPixelReflect(primaryMat, newMat, iMax-(borderSize-4), j, iMax-3, j);
            }
        }

        //lewo prawo
        for(int i=0; i<primaryMat.rows(); i++){
            if (borderSize >= 3) {
                setPixelReflect(primaryMat, newMat, i, borderSize-1, i, 0);
                setPixelReflect(primaryMat, newMat, i, jMax-(borderSize-1), i, jMax);
            }
            if (borderSize >= 5){
                setPixelReflect(primaryMat, newMat, i, borderSize-2, i, 1);
                setPixelReflect(primaryMat, newMat, i, jMax-(borderSize-2), i, jMax-1);
            }
            if (borderSize >= 7){
                setPixelReflect(primaryMat, newMat, i, borderSize-3, i, 2);
                setPixelReflect(primaryMat, newMat, i, jMax-(borderSize-3), i, jMax-2);
            }
            if(borderSize >= 9){
                setPixelReflect(primaryMat, newMat, i, borderSize-4, i, 3);
                setPixelReflect(primaryMat, newMat, i, jMax-(borderSize-4), i, jMax-3);
            }
        }

    }

    /**
     *
     * @param primaryMat - obraz podstawowy
     * @param newMat - obraz wynikowy
     * @param getRow - numer wiersza do pobrania
     * @param getCol - numer kolumny do pobrania
     * @param destinationRow - numer wiersza docelowego
     * @param destinationCol - numer kolumny docelowej
     */
    private void setPixelReflect(Mat primaryMat, Mat newMat, int getRow, int getCol, int destinationRow, int destinationCol){
        //* BORDER_REFLECT BORDER_REFLECT: fedcba|abcdefgh|hgfedcb
            double[] tab = primaryMat.get(getRow,getCol);
            double[] resultTab = new double[tab.length];

            for(int k=0; k<tab.length; k++){
                resultTab[k] = tab[k];
            }
            newMat.put(destinationRow,destinationCol,resultTab);
    }

    /**
     * Ustawienie piksela na wskazana wartosc
     * @param primaryMat - obraz pierwotny
     * @param newMat - obraz wynikowy
     * @param borderSize - rozmiar otoczenia
     */
    private void setPixelArbitraryValue(Mat primaryMat, Mat newMat, int borderSize, int iMax, int jMax, int value){
        //gora dol
        for(int j=0; j<primaryMat.cols(); j++) {
            if (borderSize >= 3) {
                setPixelArbitraryValue(primaryMat, newMat, 0, j, value);
                setPixelArbitraryValue(primaryMat, newMat, iMax, j, value);
            }
            if (borderSize >= 5){
                setPixelArbitraryValue(primaryMat, newMat, 1, j, value);
                setPixelArbitraryValue(primaryMat, newMat, iMax-1, j, value);
            }
            if (borderSize >= 7){
                setPixelArbitraryValue(primaryMat, newMat, 2, j, value);
                setPixelArbitraryValue(primaryMat, newMat, iMax-2, j, value);
            }
            if(borderSize >= 9){
                setPixelArbitraryValue(primaryMat, newMat, 3, j, value);
                setPixelArbitraryValue(primaryMat, newMat, iMax-3, j, value);
            }
        }

        //lewo prawo
        for(int i=0; i<primaryMat.rows(); i++){
            if (borderSize >= 3) {
                setPixelArbitraryValue(primaryMat, newMat, i, 0, value);
                setPixelArbitraryValue(primaryMat, newMat, i, jMax, value);
            }
            if (borderSize >= 5){
                setPixelArbitraryValue(primaryMat, newMat, i, 1, value);
                setPixelArbitraryValue(primaryMat, newMat, i, jMax-1, value);
            }
            if (borderSize >= 7){
                setPixelArbitraryValue(primaryMat, newMat, i, 2, value);
                setPixelArbitraryValue(primaryMat, newMat, i, jMax-2, value);
            }
            if(borderSize >= 9){
                setPixelArbitraryValue(primaryMat, newMat, i, 3, value);
                setPixelArbitraryValue(primaryMat, newMat, i, jMax-3, value);
            }
        }
    }

    /**
     *
     * @param primaryMat - obraz podstawowy
     * @param newMat - obraz wyniowy
     * @param row - numer wiersza
     * @param col - numer kolumny
     * @param value - wartosc do wstawienia
     */
    private void setPixelArbitraryValue(Mat primaryMat, Mat newMat, int row, int col, int value){
        double[] tab = primaryMat.get(row,col);
        double[] resultTab = new double[tab.length];

        for(int k=0; k<tab.length; k++){
            resultTab[k] = value;
        }
        newMat.put(row,col,resultTab);
    }

    private void setPixelFromProximityUniversalMedian(Mat primaryMat, Mat newMat, int borderSize, int iMax, int jMax){
        if(borderSize >= 3){
            leftColumnMedian(primaryMat, newMat, iMax, 0, borderSize);
            rightColumnMedian(primaryMat, newMat, iMax, jMax, jMax, borderSize);
            topRowMedian(primaryMat, newMat, jMax, 0, borderSize);
            bottomRowMedian(primaryMat, newMat, iMax, jMax, iMax, borderSize);
        }
        if(borderSize >= 5){
            leftColumnMedian(primaryMat, newMat, iMax, 1, borderSize);
            rightColumnMedian(primaryMat, newMat, iMax, jMax, jMax-1, borderSize);
            topRowMedian(primaryMat, newMat, jMax, 1, borderSize);
            bottomRowMedian(primaryMat, newMat, iMax, jMax, iMax-1, borderSize);
        }
        if(borderSize >= 7){
            leftColumnMedian(primaryMat, newMat, iMax, 2, borderSize);
            rightColumnMedian(primaryMat, newMat, iMax, jMax, jMax-2, borderSize);
            topRowMedian(primaryMat, newMat, jMax, 2, borderSize);
            bottomRowMedian(primaryMat, newMat, iMax, jMax, iMax-2, borderSize);
        }
        if(borderSize >= 9){
            leftColumnMedian(primaryMat, newMat, iMax, 3, borderSize);
            rightColumnMedian(primaryMat, newMat, iMax, jMax, jMax-3, borderSize);
            topRowMedian(primaryMat, newMat, jMax, 3, borderSize);
            bottomRowMedian(primaryMat, newMat, iMax, jMax, iMax-3, borderSize);
        }

        leftColumnTopPixelMedian(primaryMat, newMat, borderSize);
        leftColumnBottomPixelMedian(primaryMat, newMat, borderSize, iMax);
        rightColumnTopPixelMedian(primaryMat, newMat, borderSize, jMax);
        rightColumnBottomPixelMedian(primaryMat,newMat, borderSize, iMax, jMax);
    }

    ////– Lewa skrajna kolumna (oprócz pikseli górnego i dolnego rogu) – kierunki 0,1,2,6,7,
    private void leftColumnMedian(Mat primaryMat, Mat newMat, int iMax, int destinationCol, int borderSize){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int startIndex = (borderSize-1)/2;

        for (int i = 0; i <= iMax; i++) {
            valueList = new ArrayList<>();
            if(i<startIndex){
                for(int ii=0 ;ii<=startIndex-i; ii++){
                    for(int jj=0; jj<=startIndex; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else if(i>(iMax-startIndex)){
                for(int ii=i-startIndex; ii<=iMax; ii++){
                    for(int jj=0; jj<=startIndex; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else{
                //otoczenie wlasciwe
                for(int ii=(i-startIndex); ii<=i+startIndex; ii++){
                    for(int jj=0; jj<=startIndex; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }
            newMat.put(i, destinationCol, calculateMedian(valueList, tab.length));
        }
    }

    ////– Prawa skrajna kolumna (oprócz pikseli górnego i dolnego rogu) – kierunki 2,3,4,5,6,
    private void rightColumnMedian(Mat primaryMat, Mat newMat, int iMax, int jMax, int destinationCol, int borderSize){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int startIndex = (borderSize-1)/2;

        for (int i = 0; i <= iMax; i++) {
            valueList = new ArrayList<>();
            if(i<startIndex){
                for(int ii=0 ;ii<=startIndex-i; ii++){
                    for(int jj=jMax-startIndex; jj<=jMax; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else if(i>(iMax-startIndex)){
                for(int ii=i-startIndex ;ii<=iMax; ii++){
                    for(int jj=jMax-startIndex; jj<=jMax; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else{
                //otoczenie wlasciwe
                for(int ii=(i-startIndex) ;ii<=i+startIndex; ii++){
                    for(int jj=jMax-startIndex; jj<=jMax; jj++){
                        if(ii==i && jj==destinationCol) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }
            newMat.put(i, destinationCol, calculateMedian(valueList, tab.length));
        }
    }

    ////– Lewa skrajna kolumna piksel w górnym rogu – kierunki 0, 6,7,
    private void leftColumnTopPixelMedian(Mat primaryMat, Mat newMat, int borderSize){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int endIndex = (borderSize-1)/2;
        //otoczenie
        for(int ii=0 ;ii<=endIndex; ii++){
            for(int jj=0; jj<=endIndex; jj++){
                if(ii==0 && jj==0)
                    continue;
                tab = primaryMat.get(ii, jj);
                for (int k = 0; k < tab.length; k++) {
                    valueList.add((int) tab[k]);
                }
            }
        }
        newMat.put(0, 0, calculateMedian(valueList, tab.length));
    }

    ////– Lewa skrajna kolumna (piksel w dolnym rogu) – kierunki 0,1,2,
    private void leftColumnBottomPixelMedian(Mat primaryMat, Mat newMat, int borderSize, int iMax){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int index = (borderSize-1)/2;
        //otoczenie
        for(int ii=iMax-index ;ii<=iMax; ii++){
            for(int jj=0; jj<=index; jj++){
                if(ii==iMax && jj==0)//pomijamy piksel srodkowy dla ktorwgo jest liczone otoczenie
                    continue;
                tab = primaryMat.get(ii, jj);
                for (int k = 0; k < tab.length; k++) {
                    valueList.add((int) tab[k]);
                }
            }
        }
        newMat.put(iMax, 0, calculateMedian(valueList, tab.length));
    }

    ////– Prawa skrajna kolumna piksel w górnym rogu – kierunki 4,5,6,
    private void rightColumnTopPixelMedian(Mat primaryMat, Mat newMat, int borderSize, int jMax){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int endIndex = (borderSize-1)/2;
        //otoczenie
        for(int ii=0 ;ii<=endIndex; ii++){
            for(int jj=jMax-endIndex; jj<=jMax; jj++){
                if(ii==0 && jj==jMax)//pomijamy piksel srodkowy dla ktorwgo jest liczone otoczenie
                    continue;
                tab = primaryMat.get(ii, jj);
                for (int k = 0; k < tab.length; k++) {
                    valueList.add((int) tab[k]);
                }
            }
        }
        newMat.put(0, jMax, calculateMedian(valueList, tab.length));
    }

    ////– Prawa skrajna kolumna (piksel w dolnym rogu) – kierunki 2,3,4,
    private void rightColumnBottomPixelMedian(Mat primaryMat, Mat newMat, int borderSize, int iMax, int jMax){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int index = (borderSize-1)/2;
        //otoczenie
        for(int ii=iMax-index ;ii<=iMax; ii++){
            for(int jj=jMax-index; jj<=jMax; jj++){
                if(ii==iMax && jj==jMax)//pomijamy piksel srodkowy dla ktorwgo jest liczone otoczenie
                    continue;
                tab = primaryMat.get(ii, jj);
                for (int k = 0; k < tab.length; k++) {
                    valueList.add((int) tab[k]);
                }
            }
        }
        newMat.put(iMax, jMax, calculateMedian(valueList, tab.length));
    }

    ////– Górny skrajny wiersz (oprócz pikseli z lewego i prawego rogu) – kierunki 4,5,6,7,0
    private void topRowMedian(Mat primaryMat, Mat newMat,  int jMax, int destinationRow, int borderSize){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int startIndex = (borderSize-1)/2;

        for (int j = 0; j <= jMax; j++) {
            valueList = new ArrayList<>();
            if(j<startIndex){//otoczenie dodatkowe
                for(int ii=0 ;ii<=startIndex; ii++){
                    for(int jj=0; jj<=j+startIndex; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else if(j>(jMax-startIndex)){
                for(int ii=0 ;ii<=startIndex; ii++){
                    for(int jj=j-startIndex; jj<=jMax; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else{
                //otoczenie wlasciwe
                for(int ii=0 ;ii<=startIndex; ii++){
                    for(int jj=j-startIndex; jj<=j+startIndex; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }
            newMat.put(destinationRow, j,calculateMedian(valueList, tab.length));
        }
    }

    ////– Dolny skrajny wiersz (oprócz pikseli z lewego i prawego rogu) – kierunki 0,1,2,3,4.
    private void bottomRowMedian(Mat primaryMat, Mat newMat,  int iMax, int jMax, int destinationRow, int borderSize){
        ArrayList<Integer> valueList = new ArrayList<>();
        double[] tab = null;
        int startIndex = (borderSize-1)/2;

        for (int j = 0; j <= jMax; j++) {
            valueList = new ArrayList<>();
            if(j<startIndex){//otoczenie dodatkowe
                for(int ii=iMax-startIndex ;ii<=iMax; ii++){
                    for(int jj=0; jj<=j+startIndex; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else if(j>(jMax-startIndex)){
                for(int ii=iMax-startIndex ;ii<=iMax; ii++){
                    for(int jj=j-startIndex; jj<=jMax; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }else{
                //otoczenie wlasciwe
                for(int ii=iMax-startIndex ;ii<=iMax; ii++){
                    for(int jj=j-startIndex; jj<=j+startIndex; jj++){
                        if(jj==j && ii==destinationRow) continue;
                        tab = primaryMat.get(ii, jj);
                        for (int k = 0; k < tab.length; k++) {
                            valueList.add((int) tab[k]);
                        }
                    }
                }
            }
            newMat.put(destinationRow, j, calculateMedian(valueList, tab.length));
        }
    }

    private double[] calculateMedian(ArrayList<Integer> valueList, int sizeTab){
        int[] median = new int[2];
        double[] resultTab = null;

        Collections.sort(valueList);//sortowanie
        int listSize = valueList.size();
        int average = 0;
        if(listSize%2 == 0){
            //parzysta liczba
            median[0] = valueList.get(listSize/2);
            median[1] = valueList.get(((listSize/2)-1));
            average = ((median[0] + median[1])/2);
        }else{
            //nieparzysta liczna
            average = valueList.get(listSize/2);
        }
        resultTab = new double[sizeTab];
        for (int k = 0; k < sizeTab; k++) {
            resultTab[k] = average;
        }
        return resultTab;
    }


}
