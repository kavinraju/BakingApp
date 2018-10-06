package skr_developer.bakingapp.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class IngredientsEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int recipeId;
    private float quantity;
    private String recipeName;
    private String measure;
    private String ingredient;

    @Ignore
    public IngredientsEntry(int recipeId, float quantity, String recipeName, String measure, String ingredient) {
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.recipeName = recipeName;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public IngredientsEntry(int id, int recipeId, float quantity, String recipeName, String measure, String ingredient) {
        this.id = id;
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.recipeName = recipeName;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
