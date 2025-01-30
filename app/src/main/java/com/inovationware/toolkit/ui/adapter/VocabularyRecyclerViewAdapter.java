package com.inovationware.toolkit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.code.entity.VocabularyUnit;
import com.inovationware.toolkit.code.verb.Vocabulary;

import java.util.List;

public class VocabularyRecyclerViewAdapter extends RecyclerView.Adapter<VocabularyRecyclerViewAdapter.ViewHolder>{
    private Animation animation;
    private Context context;

    private List<VocabularyUnit> listing;


    public VocabularyRecyclerViewAdapter(Context context, List<VocabularyUnit> listing){
        this.context = context;
        this.listing = listing;
    }

    @NonNull
    @Override
    public VocabularyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vocabulary_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.keyword.setText(listing.get(holder.getAdapterPosition()).getKeyword());
        holder.description.setText(listing.get(holder.getAdapterPosition()).getDescription());
        holder.yoruba.setText(listing.get(holder.getAdapterPosition()).getInYoruba());
        holder.bulgarian.setText(listing.get(holder.getAdapterPosition()).getInBulgarian());
    }

    @Override
    public int getItemCount() {
        return listing.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView keyword;
        private TextView description;
        private TextView yoruba;
        private TextView bulgarian;
        private MaterialCardView rowRootLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            keyword = itemView.findViewById(R.id.vocabularyKeyword);
            description = itemView.findViewById(R.id.vocabularyDescription);
            yoruba = itemView.findViewById(R.id.vocabularyInYoruba);
            bulgarian = itemView.findViewById(R.id.vocabularyInBulgarian);
            rowRootLayout = itemView.findViewById(R.id.vocabularyRowRootLayout);
            animation = AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation);
            rowRootLayout.setAnimation(animation);

        }
    }
}
