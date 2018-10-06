package skr_developer.bakingapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.bakingapp.Model.Ingredient;
import skr_developer.bakingapp.R;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private Context context;
    private List<Ingredient> ingredientList;

    public IngredientsAdapter(List<Ingredient> ingredients){
        this.ingredientList = ingredients;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutID = R.layout.ingredients_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,parent,false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {

        holder.tv_ingredient_name.setText(ingredientList.get(position).getIngredient());

        //Builing string to for the ingredient quantity and measure
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(ingredientList.get(position).getQuantity()))
                .append(" ")
                .append(ingredientList.get(position).getMeasure());

        holder.tv_ingredient_gty.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient_name)
        TextView tv_ingredient_name;
        @BindView(R.id.tv_ingredient_qty)
        TextView tv_ingredient_gty;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
