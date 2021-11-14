package pl.przybysz.kamila.enums;

public enum BorderType {
    PIXEL_WITHOUT_CHANGE("Pozostawienie wartosci pikseli bez zmian"),
    ARBITRARY_VALUE("Zadana wartość arbitralna"),
    REFLECT("Powielenie skrajnych wierszy i kolumn"),
    PIXEL_FROM_PROXIMITY("Wykorzystanie pikseli z istniejącego sąsiedztwa");

    public String description;

    private BorderType(String description) {
        this.description = description;
    }

}
