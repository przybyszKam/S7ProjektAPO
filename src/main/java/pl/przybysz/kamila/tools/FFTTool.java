package pl.przybysz.kamila.tools;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import pl.przybysz.kamila.dialog.ImageViewStage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class FFTTool {
    public Mat optimizeImageDim(Mat image)
    {
        Mat padded = new Mat();
        int addPixelRows = Core.getOptimalDFTSize(image.rows());//wielokrotnosc 2, 3 lub 5
        int addPixelCols = Core.getOptimalDFTSize(image.cols());//wielokrotnosc 2, 3 lub 5
        //kopia obrazu do zmiennej padded o optymalnych wartosciach
        Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),Core.BORDER_CONSTANT, Scalar.all(0));

        return padded;
    }

    /**
     * Optymalizacja widma amplitudowego z obrazu zlozonego z dft
     * do poprawy jego wizualizacji
     * @param complexImage - obraz zlozony uzyskany z dft
     * @return zoptymalizowany obraz
     */
    public Mat createOptimizedMagnitude(Mat complexImage, ImageViewStage stage)
    {
        List<Mat> newPlanes = new ArrayList<>();
        Mat mag = new Mat();
        // => log(1 + sqrt(Re(DFT(I))^2 + Im(DFT(I))^2))
        //podzial obrazu na plaszczyzny
        Core.split(complexImage, newPlanes);
        // planes.get(0) = Re(DFT(I) - wartosci rzeczywiste
        // planes.get(1) = Im(DFT(I)) - wartosci urojone
        //obliczanie widma
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);// planes.get(0) = magnitude
        //przeniesienie do skali logarytmicznej
        Core.add(mag, Scalar.all(1), mag);//Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
        Core.log(mag, mag);
        //przestawienie ćwiartek widma
        this.shiftDFT(mag);
        //normalizacja
        Mat magStage = new Mat();
        magStage = mag.clone();
//        Core.normalize(mag, magStage, 0, 255, Core.NORM_MINMAX);//zachowane 5 kanałow
        stage.setMagnitude(magStage);//bez normalizacji
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);//konwersja do pozazania obrazu do jednego kanalu
        return mag;
    }

    /**
     * Przestawienie czterech ćwiartek obrazu reprezentującego widmo po dft
     * @param image - obiekt zawierający ćwiartki do zamiany miejscami
     */
    public static void shiftDFT(Mat image)
    {
        image = image.submat(new Rect(0, 0, image.cols() & -2, image.rows() & -2));
        int cx = image.cols() / 2;
        int cy = image.rows() / 2;

        Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    /**
     *
     * @param startX - punkt poczatkowy X
     * @param startY - punkt poczatkowy Y
     * @param endX - punkt koncowy X
     * @param endY - punkt koncowy Y
     * @param newMatComplexImage - obraz na którymm są zapsywane zmiany
     * @return
     */
    public void modifyMagnitude(int startX, int startY, int endX, int endY, Mat newMatComplexImage){
        //zmeinna dla wartosci która bedzie przypisywana
        double value = 0;
        for(int i = startX; i <= endX; i++){//szerokosc
            for(int j = startY; j <= endY; j++){//wysokosc
                //pobranie tablicy spd danej lokalizacji
                double[] tab = newMatComplexImage.get(j,i);//wiersz , kolumna
                //utworzenie nowej tablicy wynikowej o tym samym rozmiarze co pobrana tablica
                double[] resultTab = new double[tab.length];

                //operacja na kazdym elemencie tablicy pierwotnej
                for(int k=0; k<tab.length; k++){
                    //ustalenie sposobu obliczania wartosci
                    switch(ImageAndMatTool.editMagnitudeType){
                        case WHITE:
                            value = 255;
                            break;
                        case BLACK:
                            value = 0;
                            break;
                        case DOUBLE:
                            value = tab[k] * 2;
                            break;
                    }
                    //uwupelnienie tablicy wynikowej
                    resultTab[k] = value;
                }
                //podstawienie tablicy wynikowej do obrazu
                newMatComplexImage.put(j,i,resultTab);
            }
        }
    }

    public static Image mat2Image(Mat frame)
    {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

}
