package pl.przybysz.kamila.enums;

public enum Mask {
    A("uśrednianie", new int[][]{
            {1,1,1},
            {1,1,1},
            {1,1,1}
    }),
    B("K-pudełkowe", new int[][]{
            {1,1,1},
            {1,1,1},//w filtrze K-pudełkowym podmieniamy wartosc ([1][1]) na wartosc podana przez uzytkownika
            {1,1,1}
    }),
    G("podstawowy filtr gaussowski", new int[][]{
            {1,2,1},
            {2,4,2},
            {1,2,1}
    }),
    L1("laplasjan 1", new int[][]{
            {0,-1,0},
            {-1,4,-1},
            {0,-1,0}
    }),
    L2("laplasjan 2", new int[][]{
            {-1,-1,-1},
            {-1,8,-1},
            {-1,-1,-1}
    }),
    L3("laplasjan 3", new int[][]{
            {1,-2,1},
            {-2,4,-2},
            {1,-2,1}
    }),
    SobelE("Sobel - Wschód", new int[][]{
            {-1,0,1},
            {-2,0,2},
            {-1,0,1}
    }),
    SobelSE("Sobel - Południowy wschód", new int[][]{
            {-2,-1,0},
            {-1,0,1},
            {0,1,2}
    }),
    SobelS("Sobel - Południe", new int[][]{
            {-1,-2,-1},
            {0,0,0},
            {1,2,1}
    }),
    SobelSW("Sobel - Południowy zachód", new int[][]{
            {0,-2,-1},
            {1,0,-1},
            {1,2,0}
    }),
    SobelW("Sobel - Zachód", new int[][]{
            {1,0,-1},
            {2,0,-2},
            {1,0,-1}
    }),
    SobelNW("Sobel - Północny zachód", new int[][]{
            {2,1,0},
            {1,0,-1},
            {0,-1,-2}
    }),
    SobelN("Sobel - Północ", new int[][]{
            {1,2,1},
            {0,0,0},
            {-1,-2,-1}
    }),
    SobelNE("Sobel - Północny wschód", new int[][]{
            {0,1,2},
            {-1,0,1},
            {-2,-1,0}
    }),
    PrewittE("Prewitt - Wschód", new int[][]{
            {-1,0,1},
            {-1,0,1},
            {-1,0,1}
    }),
    PrewittS("Prewitt - Południe", new int[][]{
            {-1,-1,-1},
            {0,0,0},
            {1,1,1}
    });

    public String description;
    public int[][] tab;

    private Mask(String description, int[][] tab) {
        this.description = description;
        this.tab = tab;
    }

    public int[][] getBMaskK(int k){
        int [][] result = Mask.B.getTab();
        result[1][1] = k;
        return result;
    }

    public int[][] getTab() {
        return tab;
    }

}
