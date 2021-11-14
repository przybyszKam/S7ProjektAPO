package pl.przybysz.kamila.tools;

public class TableRow {
    private String col1;
    private String col2;
    private String col3;

    public TableRow(){}

    public TableRow(String col1, String col2, String col3){
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
    }

    public TableRow(Integer col1, Integer col2, Integer col3){
        this.col1 = String.valueOf(col1);
        this.col2 = String.valueOf(col2);
        this.col3 = String.valueOf(col3);
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }
}
