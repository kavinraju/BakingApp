package skr_developer.bakingapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.bakingapp.Model.Step;
import skr_developer.bakingapp.R;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private List<Step> stepList;

    private Context context;
    private OnStepClickListener onStepClickListener;

    public StepsAdapter(List<Step> stepList, OnStepClickListener onStepClickListener){
        this.stepList = stepList;
        this.onStepClickListener = onStepClickListener;
    }

    public interface OnStepClickListener{
        public void onStepClicked(int position);
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.steps_item,parent,false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {

        holder.btnStep.setText(stepList.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.btn_step)
        Button btnStep;

        public StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            btnStep.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStepClickListener.onStepClicked(getAdapterPosition());
        }
    }
}
