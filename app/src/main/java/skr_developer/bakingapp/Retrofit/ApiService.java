package skr_developer.bakingapp.Retrofit;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import skr_developer.bakingapp.Model.Recipe;

public interface ApiService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Single<List<Recipe>> fetchAllRecipes();
}
