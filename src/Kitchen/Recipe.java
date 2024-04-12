//package Kitchen;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Recipe{
//    private int recipeID;
//    private String recipeName;
//    private Map<Ingredient, Integer> recipe = new HashMap<>();
//
//    public Recipe(int RecipeId, String RecipeName, Map<Ingredient,Integer> recipe) {
//        this.recipeID = recipeID;
//        this.recipeName = recipeName;
//        this.recipe = recipe;
//    }
//
//    public int getRecipeID() {
//        return recipeID;
//    }
//
//    public void setRecipeID(int recipeID) {
//        this.recipeID = recipeID;
//    }
//
//    public String getRecipeName() {
//        return recipeName;
//    }
//
//    public void setRecipeName(String recipeName) {
//        this.recipeName = recipeName;
//    }
//
//    public Map<Ingredient, Integer> getRecipe() {
//        return recipe;
//    }
//
//    public void setRecipe(Map<Ingredient, Integer> recipe) {
//        this.recipe = recipe;
//    }
//
//    public void addIngredient(Ingredient ingredient, int quantity) {
//        recipe.put(ingredient, quantity);
//    }
//
//    public void removeIngredient(Ingredient ingredient) {
//        recipe.remove(ingredient);
//    }
//
//    public void updateIngredientQuantity(Ingredient ingredient, int newQuantity) {
//        recipe.put(ingredient, newQuantity);
//    }
//
//    public boolean containsIngredient(Ingredient ingredient) {
//        return recipe.containsKey(ingredient);
//    }
//
//    public int getIngredientQuantity(Ingredient ingredient) {
//        return recipe.getOrDefault(ingredient, 0);
//    }
//}
