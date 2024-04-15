package Kitchen;

import KitchenToManagement.DishObject;
import KitchenToManagement.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class Recipe{
    private int recipeID;
    private String recipeName;
    private Map<Ingredient, Integer> recipeIngredients = new HashMap<>();

    public Recipe(int RecipeId, String RecipeName, Map<Ingredient,Integer> recipeIngredients) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.recipeIngredients = recipeIngredients;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Map<Ingredient, Integer> getRecipe() {
        return recipeIngredients;
    }

    public void setRecipe(Map<Ingredient, Integer> recipe) {
        this.recipeIngredients = recipe;
    }

    public void addIngredient(Ingredient ingredient, int quantity) {
        recipeIngredients.put(ingredient, quantity);
    }

    public void removeIngredient(Ingredient ingredient) {
        recipeIngredients.remove(ingredient);
    }

    public void updateIngredientQuantity(Ingredient ingredient, int newQuantity) {
        recipeIngredients.put(ingredient, newQuantity);
    }

    public boolean containsIngredient(Ingredient ingredient) {
        return recipeIngredients.containsKey(ingredient);
    }

    public int getIngredientQuantity(Ingredient ingredient) {
        return recipeIngredients.getOrDefault(ingredient, 0);
    }
}
