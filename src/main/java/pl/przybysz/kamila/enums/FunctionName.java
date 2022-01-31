package pl.przybysz.kamila.enums;

public enum FunctionName {
    FULL_SCALE_HISTOGRAM("liniowe rozciaganie histogramu"),
    EQUALIZATION_HISTOGRAM("Wyrownanie histogramu typu equalizacja"),
    NEGATIVE_IMAGE("Negacja obrazu"),
    BINARY_THRESHOLDING_IMAGE("Progowanie binarne z progiem wskazywanym suwakiem"),
    LEVEL_COLOR_THRESHOLDING("Progowanie z zachowaniem poziomow szarosci"),
    REDUCTION_LEVELS_COLORS("Redukcja poziomów szarości"),
    SCALE_HISTOGRAM_Q1_Q2("Rozciaganie hostogramu ze wskazaniem zakresu"),
    LINEAR_BLUR("Wygładzania liniowego"),
    SHARPEN_MASK("Wyostrzania liniowego oparte na maskach laplasjanowych"),
    EDGE_DIRECTION_SOBEL("Kierunkowa detekcja krawedzi - Sobel"),
    EDGE_DIRECTION_PREWITT("Operator detekcji krawedzi - Prewitt"),
    EDGE_DIRECTION_CANNYEGO("Operator detekcji krawedzi - Cannyego"),
    UNIVERSAL_MEDIAN("Uniwersalna operacja medianowa"),
    FAST_FOURIER_TRANSFORMATION("FFT - Transformata Fouriera"),
    INVERSE_FAST_FOURIER_TRANSFORMATION("iFFT - Odwrotna Transformata Fouriera"),
    MODIFY_FAST_FOURIER_TRANSFORMATION("Modyfikacja transformaty"),
    BINARY_OPERATION_AND("Operacja binarna AND"),
    BINARY_OPERATION_OR("Operacja binarna OR"),
    BINARY_OPERATION_XOR("Operacja binarna XOR"),
    SEGMENT_IMAGE_ADAPTATION("Segmentacja obrazu - metoda adaptacyjna"),
    SEGMENT_IMAGE_OTSU("Segmentacja obrazu - metoda Otsu"),
    WATERSHED("Segmentacja obrazu - metoda wododziałowa"),
    SEGMENTATION_IMAGE("Segmentacja obrazu")
    ;
    public String description;

    private FunctionName(String description) {
        this.description = description;
    }

}
