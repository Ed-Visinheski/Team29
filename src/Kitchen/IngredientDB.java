package Kitchen;

import java.util.List;

public class IngredientDB{

    private List<Ingredient> ingredientList;

    public IngredientDB(List<Ingredient> ingredientList){
        this.ingredientList = ingredientList;
    }

    public void addIngredient(Ingredient ingredient){
        ingredientList.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient){
        ingredientList.remove(ingredient);
    }

    public void updateIngredient(Ingredient ingredient){
        for(Ingredient i : ingredientList){
            if(i.getIngredientID() == ingredient.getIngredientID()){
                i.setIngredientName(ingredient.getIngredientName());
                i.setIngredientStockLevel(ingredient.getIngredientStockLevel());
                i.setLowStockThreshold(ingredient.getLowStockThreshold());
            }
        }
    }

    public void sendToDatabase(){
        // Code to send the ingredientList to the database
    }
}
