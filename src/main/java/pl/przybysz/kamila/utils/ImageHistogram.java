package pl.przybysz.kamila.utils;

import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class ImageHistogram {
    private Image image;

    private long alpha[] = new long[256];
    private long red[] = new long[256];
    private long green[] = new long[256];
    private long blue[] = new long[256];

    private long mono[] = new long[256];

    XYChart.Series seriesAlpha;//okresla przezroczystosc 0% - niewidoczny, 100%- widoczny w całości
    XYChart.Series seriesRed;
    XYChart.Series seriesGreen;
    XYChart.Series seriesBlue;

    XYChart.Series seriesMono;

    private boolean success;

    public ImageHistogram(Image src) {
        image = src;
        success = false;

        for (int i = 0; i < 256; i++) {
            alpha[i] = red[i] = green[i] = blue[i] = mono[i] = 0;
        }

        PixelReader pixelReader = image.getPixelReader();
        if (pixelReader == null) {
            return;
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixelReader.getArgb(x, y);
                int a = (0xff & (argb >> 24));
                int r = (0xff & (argb >> 16));
                int g = (0xff & (argb >> 8));
                int b = (0xff & argb);

                alpha[a]++;
                red[r]++;
                green[g]++;
                blue[b]++;
            }
        }

        seriesAlpha = new XYChart.Series();
        seriesRed = new XYChart.Series();
        seriesGreen = new XYChart.Series();
        seriesBlue = new XYChart.Series();
        seriesMono = new XYChart.Series();

        seriesAlpha.setName("alpha");
        seriesRed.setName("red");
        seriesGreen.setName("green");
        seriesBlue.setName("blue");
        seriesMono.setName("mono");

        for(int i=0; i<256; i++){
            mono[i] = (long) (red[i]*0.3 + green[i]*0.6 + blue[i]*0.1);
        }

        for (int i = 0; i < 256; i++) {
            seriesAlpha.getData().add(new XYChart.Data(String.valueOf(i), alpha[i]));
            seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
            seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
            seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
            seriesMono.getData().add(new XYChart.Data(String.valueOf(i), mono[i]));
        }

        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public XYChart.Series getSeriesAlpha() {
        return seriesAlpha;
    }

    public XYChart.Series getSeriesRed() {
        return seriesRed;
    }

    public XYChart.Series getSeriesGreen() {
        return seriesGreen;
    }

    public XYChart.Series getSeriesBlue() {
        return seriesBlue;
    }

    public XYChart.Series getSeriesMono(){
        return seriesMono;
    }

    public long[] getAlpha() {
        return alpha;
    }

    public void setAlpha(long[] alpha) {
        this.alpha = alpha;
    }

    public long[] getRed() {
        return red;
    }

    public void setRed(long[] red) {
        this.red = red;
    }

    public long[] getGreen() {
        return green;
    }

    public void setGreen(long[] green) {
        this.green = green;
    }

    public long[] getBlue() {
        return blue;
    }

    public void setBlue(long[] blue) {
        this.blue = blue;
    }

    public long[] getMono() {
        return mono;
    }

    public void setMono(long[] mono) {
        this.mono = mono;
    }

}
