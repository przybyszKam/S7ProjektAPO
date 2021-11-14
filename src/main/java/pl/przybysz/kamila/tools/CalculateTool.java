package pl.przybysz.kamila.tools;

public class CalculateTool {

    /**
     * Funkcja przelicza equalizacje dla danego kanalu
     * @param tabD - tablica histogramu skumulowanego
     * @param m - liczba poziomow jasnosci
     * @param color - wartosc koloru
     * @return
     */
    public int calculateEqualizationPixelColor(double tabD[], int m, int color){
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
    public int calculateFullScalePixelColor(int color, int minColor, int maxColor){
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
    public double[] createTabD(int width, int height, long tabColor[], int m){
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

    public int setMinColorValue(long tabColor[]){
        for (int i=-1; i<255; i++){
            if(tabColor[i+1] != 0){
                int min = i+1;
                return min;
            }
        }
        return 0;
    }

    public int setMaxColorValue(long tabColor[]){
        for (int i=256; i>=0; i--){
            if(tabColor[i-1] != 0){
                int max = i-1;
                return max;
            }
        }
        return 0;
    }

    /**
     * Funkcja neguje wartosc dla danego kanalu
     * @param color - wartosc koloru dla danwgo kanalu
     * @param m - liczba poziomow jasnosci
     * @return
     */
    public int negativePixelColor(int color, int m){
        //q(i,j) = Lmax – p(i,j)
        return (m-1) - color;
    }

    /**
     * Funkcja progowania binarnego dla danego koloru
     * @param color - wartosc koloru dla danego kanalu
     * @param lMin - wartosc minimalna
     * @param lMax - wartosc maksymalna
     * @param threshold - prog
     * @return
     */
    public int binaryThresholdingPixelColor(int color, int lMin, int lMax, int threshold){
        if(color <= threshold){
            return lMin;
        }else
            return lMax;
    }
    /**
     * Funkcja progowania z zachowanie poziomow szarosci dla danego koloru
     * @param color - wartosc koloru dla danego kanalu
     * @param p1 - prog dolny
     * @param p2 - prog gorny
     * @return
     */
    public int levelColorThresholdingPixelColor(int color, int p1, int p2){
        //q = p dla p1<=p<=p2
        //    0 dla p<p1 , p>p2
        if(color >= p1 && color <= p2)
            return color;
        else
            return 0;
    }

    /** Redukcja poziomów szarości dla danego koloru
     * @param color - wartosc koloru
     * @param pTab - wartosci progow p
     * @param qTab - wartosci q
     * @return
     */
    public int levelColorReductionPixelColor(int color, int[] pTab, int[] qTab){
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

    public int[] calculateQ(int size, int lMax){
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
    public int[] calculateReductionThreshold(int levels, int lMax){
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

    /**
     * Funkcja przeliczajaca rozciaganie histogramu dla podanego zakresu dla danego kanalu
     * @param color - wartosc koloru piksela
     * @param p1 - początek zakresu ktory chce rozciagnac
     * @param p2 - koniec zakresu ktory chce rozciagnac
     * @param q1 - początek zakresu do ktorego chce rozciagnac
     * @param q2 - koniec zakresu do ktorego chce rozciagnac
     * @return
     */
    public int calculateScaleQ1Q2PixelColor(int color, int p1, int p2, int q1, int q2){
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
