package com.inovationware.toolkit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.inovationware.toolkit.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.meeting.model.Contribution;

import java.util.List;

public class BoardRecyclerViewAdapter extends RecyclerView.Adapter<BoardRecyclerViewAdapter.ViewHolder> {
    private Context context;

    private List<Contribution> contributions;

    public BoardRecyclerViewAdapter(Context context, List<Contribution> contributions){
        this.context = context;
        this.contributions = contributions;
    }

    @NonNull
    @Override
    public BoardRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new BoardRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardRecyclerViewAdapter.ViewHolder holder, int i) {
        holder.contributor.setText(contributions.get(holder.getAdapterPosition()).getContributor() + " says:");
        holder.contribution.setText(contributions.get(holder.getAdapterPosition()).getContribution());
        holder.when.setText("at " + contributions.get(holder.getAdapterPosition()).getContributedAt() + DomainObjects.NEW_LINE + "from " + contributions.get(holder.getAdapterPosition()).getTimezone());
    }

    @Override
    public int getItemCount() {
        return contributions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView contributor, when, contribution;

        public ViewHolder(@NonNull View view) {
            super(view);

            contributor = view.findViewById(R.id.contributorTextView);
            contribution = view.findViewById(R.id.contributionTextView);
            when = view.findViewById(R.id.contributedAtTextView);


            view.findViewById(R.id.boardRowRootLayout).setAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation));

        }
    }
}
