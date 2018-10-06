package skr_developer.bakingapp.Database;


import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {RecipeEntry.class, IngredientsEntry.class, StepsEntry.class}, version = 1,exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final String LOG_TAG = RecipeDatabase.class.getSimpleName();
    private static final Object OBJECT = new Object();
    private static final String DATABASE_NAME = "recipes";
    private static RecipeDatabase recipeDatabase;

    public static RecipeDatabase getInstance(Context context){
        if (recipeDatabase == null){
            synchronized (OBJECT){
                Log.d(LOG_TAG, "Creating new Database");
                recipeDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                        RecipeDatabase.class,
                                        RecipeDatabase.DATABASE_NAME)
                                        .build();
            }
        }
        return recipeDatabase;
    }

    public abstract RecipesDao recipesDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

}
