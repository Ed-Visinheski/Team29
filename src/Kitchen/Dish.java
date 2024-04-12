package Kitchen;

public class Dish extends KitchenToManagement.Dish{
    private int dishID;
    private String dishName;
   // private Recipe recipe;
    private String dishDescription;
    private int dishPrice;
    private int dishPreparationTime;

    public Dish(int dishID, String dishName, String dishDescription, int dishPrice, int dishPreparationTime) {
        this.dishID = dishID;
        this.dishName = dishName;
        //this.recipe = recipe;
        this.dishDescription = dishDescription;
        this.dishPrice = dishPrice;
        this.dishPreparationTime = dishPreparationTime;
    }

    public void setDishID(int dishID) {
        this.dishID = dishID;
    }

    public int getDishID() {
        return dishID;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishName() {
        return dishName;
    }

//    public void setRecipe(Recipe recipe) {
//        this.recipe = recipe;
//    }
//
//    public Recipe getRecipe() {
//        return recipe;
//    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public String getDishDescription() {
        return dishDescription;
    }

    public void setDishPrice(int dishPrice) {
        this.dishPrice = dishPrice;
    }

    public int getDishPrice() {
        return dishPrice;
    }

    public void setDishPreparationTime(int dishPreparationTime) {
        this.dishPreparationTime = dishPreparationTime;
    }

    public int getDishPreparationTime() {
        return dishPreparationTime;
    }
}
