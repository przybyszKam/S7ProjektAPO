package pl.przybysz.kamila.dialog;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class DataRowTableLUT {

    private final LongProperty wart;
    private final LongProperty colorR;
    private final LongProperty colorG;
    private final LongProperty colorB;
    private final LongProperty colorMono;

    public DataRowTableLUT(SimpleLongProperty wart, SimpleLongProperty colorR, SimpleLongProperty colorG, SimpleLongProperty colorB, SimpleLongProperty colorMono) {
        this.wart = wart;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
        this.colorMono = colorMono;
    }

    public long getWart() {
        return wart.get();
    }

    public LongProperty wartProperty() {
        return wart;
    }

    public void setWart(long wart) {
        this.wart.set(wart);
    }

    public long getColorR() {
        return colorR.get();
    }

    public LongProperty colorRProperty() {
        return colorR;
    }

    public void setColorR(long colorR) {
        this.colorR.set(colorR);
    }

    public long getColorG() {
        return colorG.get();
    }

    public LongProperty colorGProperty() {
        return colorG;
    }

    public void setColorG(long colorG) {
        this.colorG.set(colorG);
    }

    public long getColorB() {
        return colorB.get();
    }

    public LongProperty colorBProperty() {
        return colorB;
    }

    public void setColorB(long colorB) {
        this.colorB.set(colorB);
    }

    public long getColorMono() {
        return colorMono.get();
    }

    public LongProperty colorMonoProperty() {
        return colorMono;
    }

    public void setColorMono(long colorMono) {
        this.colorMono.set(colorMono);
    }
}
