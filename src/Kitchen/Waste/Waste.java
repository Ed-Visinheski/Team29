package Kitchen.Waste;

import java.util.Date;

public class Waste extends WasteDB {
    private int wasteID;
    private int ingredientID;
    private int quantityWasted;
    private String reason;
    private Date dateWasted;

    public Waste(int wasteID, int ingredientID, int quantityWasted, String reason, Date dateWasted) {
        this.wasteID = wasteID;
        this.ingredientID = ingredientID;
        this.quantityWasted = quantityWasted;
        this.reason = reason;
        this.dateWasted = dateWasted;
    }

    // Getter and Setter methods
    public int getWasteID() {
        return wasteID;
    }

    public void setWasteID(int wasteID) {
        this.wasteID = wasteID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getQuantityWasted() {
        return quantityWasted;
    }

    public void setQuantityWasted(int quantityWasted) {
        this.quantityWasted = quantityWasted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDateWasted() {
        return dateWasted;
    }

    public void setDateWasted(Date dateWasted) {
        this.dateWasted = dateWasted;
    }
}
