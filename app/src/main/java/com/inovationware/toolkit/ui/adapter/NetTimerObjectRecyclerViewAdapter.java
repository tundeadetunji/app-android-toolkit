package com.inovationware.toolkit.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.features.nettimer.model.NetTimerObject;
import com.inovationware.toolkit.ui.activity.NetTimerResponseActivity;

import java.util.List;

import static com.inovationware.toolkit.common.domain.DomainObjects.DETAILS;
import static com.inovationware.toolkit.common.domain.DomainObjects.HEADLINE;
import static com.inovationware.toolkit.common.domain.DomainObjects.TIME_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.ZONE_STRING;

public class NetTimerObjectRecyclerViewAdapter extends RecyclerView.Adapter<NetTimerObjectRecyclerViewAdapter.ViewHolder> {
    Animation animation;
    Context context;
    List<NetTimerObject> objects;

    public NetTimerObjectRecyclerViewAdapter(Context context, List<NetTimerObject> objects) {
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_net_timer_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //revert
        //holder.headlineTextView.setText(objects.get(position).headline);
        holder.headlineTextView.setText(objects.get(holder.getAdapterPosition()).headline);
        holder.detailsTextView.setText("\n" + objects.get(position).details + "\n\n" + "Sent\n" + objects.get(position).time_string + "\n" + objects.get(position).zone_string);
        holder.menuPictureBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReplyActivity(position);
            }
        });
        holder.headlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReplyActivity(position);
            }
        });
    }

    void openReplyActivity( int position){
        Intent intent = new Intent(context, NetTimerResponseActivity.class);
        intent.putExtra(HEADLINE, objects.get(position).headline.trim());
        intent.putExtra(DETAILS, objects.get(position).details.trim());
        intent.putExtra(TIME_STRING, objects.get(position).time_string.trim());
        intent.putExtra(ZONE_STRING, objects.get(position).zone_string.trim());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePictureBox, menuPictureBox;
        TextView headlineTextView, detailsTextView;
        MaterialCardView rowRootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePictureBox = itemView.findViewById(R.id.NetTimerImagePictureBox);
            headlineTextView = itemView.findViewById(R.id.NetTimerHeadlineTextView);
            detailsTextView = itemView.findViewById(R.id.NetTimerDetailsTextView);
            menuPictureBox = itemView.findViewById(R.id.NetTimerMenuPictureBox);
            //menuPictureBox.setVisibility(View.INVISIBLE);
            rowRootLayout = itemView.findViewById(R.id.net_timer_recycler_view_row_root);
            animation = AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation);
            rowRootLayout.setAnimation(animation);
            //headlineTextView.setTextColor(Color.parseColor("#42173b"));

        }
    }
}
