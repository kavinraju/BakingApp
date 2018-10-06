package skr_developer.bakingapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.bakingapp.Model.Recipe;
import skr_developer.bakingapp.R;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>{

    private int[] recipeDrawables = {R.drawable.nutella_pie, R.drawable.brownies, R.drawable.yellow_cake, R.drawable.cheesecake};
    private List<Recipe> recipeList;

    private Context context;
    private OnClickRecipeCardListener onClickRecipeCardListener;

    public RecipeCardAdapter(OnClickRecipeCardListener onClickRecipeCardListener,  List<Recipe> recipeList){
        this.onClickRecipeCardListener = onClickRecipeCardListener;
        this.recipeList = recipeList;
    }

    public interface OnClickRecipeCardListener{
        public void onClickRecipeCard(View view, int clickedMoviePosition);
    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutID = R.layout.recipe_card_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,viewGroup,false);
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder recipeCardViewHolder, int position) {

        if (position< recipeList.size()){
            switch (recipeList.get(position).getName()){
                case "Nutella Pie":
                    recipeCardViewHolder.iv_recipeCard.setBackgroundResource(recipeDrawables[0]);
                    break;
                case "Brownies":
                    recipeCardViewHolder.iv_recipeCard.setBackgroundResource(recipeDrawables[1]);
                    break;
                case "Yellow Cake":
                    recipeCardViewHolder.iv_recipeCard.setBackgroundResource(recipeDrawables[2]);
                    break;
                case "Cheesecake":
                    recipeCardViewHolder.iv_recipeCard.setBackgroundResource(recipeDrawables[3]);
                    break;
            }
            recipeCardViewHolder.tv_recipeName.setText(recipeList.get(position).getName());
        }

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_recipecard)
        ImageView iv_recipeCard;
        @BindView(R.id.tv_recipe_name)
        TextView tv_recipeName;

        public RecipeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            iv_recipeCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickRecipeCardListener.onClickRecipeCard(view, getAdapterPosition());
        }
    }
}
