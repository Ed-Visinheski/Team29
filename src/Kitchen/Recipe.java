package Kitchen;

import KitchenToManagement.DishObject;
import KitchenToManagement.Ingredient;

import java.util.HashMap;
import java.util.Map;

/**
 * The Recipe class represents a recipe in the kitchen.
 */
public class Recipe {

    private int recipeID; // The ID of the recipe
    private String recipeName; // The name of the recipe
    private Map<Ingredient, Integer> recipeIngredients = new HashMap<>(); // The ingredients and their quantities in the recipe

    /**
     * Constructs a new Recipe object with the specified attributes.
     *
     * @param recipeID          The ID of the recipe.
     * @param recipeName        The name of the recipe.
     * @param recipeIngredients The ingredients and their quantities in the recipe.
     */
    public Recipe(int recipeID, String recipeName, Map<Ingredient, Integer> recipeIngredients) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.recipeIngredients = recipeIngredients;
    }

    /**
     * Gets the ID of the recipe.
     *
     * @return The ID of the recipe.
     */
    public int getRecipeID() {
        return recipeID;
    }

    /**
     * Sets the ID of the recipe.
     *
     * @param recipeID The ID of the recipe.
     */
    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    /**
     * Gets the name of the recipe.
     *
     * @return The name of the recipe.
     */
    public String getRecipeName() {
        return recipeName;
    }

    /**
     * Sets the name of the recipe.
     *
     * @param recipeName The name of the recipe.
     */
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    /**
     * Gets the ingredients and their quantities in the recipe.
     *
     * @return A map containing the ingredients and their quantities.
     */
    public Map<Ingredient, Integer> getRecipe() {
        return recipeIngredients;
    }

    /**
     * Sets the ingredients and their quantities in the recipe.
     *
     * @param recipe A map containing the ingredients and their quantities.
     */
    public void setRecipe(Map<Ingredient, Integer> recipe) {
        this.recipeIngredients = recipe;
    }

    /**
     * Adds an ingredient to the recipe with the specified quantity.
     *
     * @param ingredient The ingredient to add.
     * @param quantity   The quantity of the ingredient.
     */
    public void addIngredient(Ingredient ingredient, int quantity) {
        recipeIngredients.put(ingredient, quantity);
    }

    /**
     * Removes an ingredient from the recipe.
     *
     * @param ingredient The ingredient to remove.
     */
    public void removeIngredient(Ingredient ingredient) {
        recipeIngredients.remove(ingredient);
    }

    /**
     * Updates the quantity of an ingredient in the recipe.
     *
     * @param ingredient  The ingredient to update.
     * @param newQuantity The new quantity of the ingredient.
     */
    public void updateIngredientQuantity(Ingredient ingredient, int newQuantity) {
        recipeIngredients.put(ingredient, newQuantity);
    }

    /**
     * Checks if the recipe contains the specified ingredient.
     *
     * @param ingredient The ingredient to check.
     * @return True if the recipe contains the ingredient, false otherwise.
     */
    public boolean containsIngredient(Ingredient ingredient) {
        return recipeIngredients.containsKey(ingredient);
    }

    /**
     * Gets the quantity of the specified ingredient in the recipe.
     *
     * @param ingredient The ingredient to get the quantity for.
     * @return The quantity of the ingredient in the recipe, or 0 if not found.
     */
    public int getIngredientQuantity(Ingredient ingredient) {
        return recipeIngredients.getOrDefault(ingredient, 0);
    }
}


