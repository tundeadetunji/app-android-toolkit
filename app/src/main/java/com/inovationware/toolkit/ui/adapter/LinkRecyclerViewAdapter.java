package com.inovationware.toolkit.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.repository.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_APP;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.ViewHolder> {
    Animation animation;
    private Context context;
    private List<String> apps;
    private SharedPreferencesManager store;
    private GroupManager machines;

    public LinkRecyclerViewAdapter(Context context, List<String> apps) {
        this.context = context;
        this.apps = apps;
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //revert
        //holder.textHeadlineR.setText(apps.get(position));
        holder.textHeadlineR.setText(apps.get(holder.getAdapterPosition()));
        holder.textHeadlineR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText(apps.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }


    void sendText(String path) {
        if (!thereIsInternet(context) || !initialParamsAreSet(context,store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(context),
                determineTarget(context, store, machines),
                POST_PURPOSE_APP,
                determineMeta(context, store),
                path.trim(),
                Strings.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    new Feedback(context).toast(response.body());
                } else {
                    if (!store.shouldDisplayErrorMessage(context)){
                        return;
                    }
                    new Feedback(context).toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(context)){
                    return;
                }
                new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePicR, imageMoreR;
        TextView textHeadlineR, textDetailR;
        MaterialCardView customRowRootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePicR = itemView.findViewById(R.id.imagePic);
            textHeadlineR = itemView.findViewById(R.id.textHeadline);
            textDetailR = itemView.findViewById(R.id.NetTimerDetailsTextView);
            imageMoreR = itemView.findViewById(R.id.imageMore);
            imageMoreR.setVisibility(View.INVISIBLE);
            customRowRootLayout = itemView.findViewById(R.id.customRowRootLayout);
            animation = AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation);
            customRowRootLayout.setAnimation(animation);
            //textHeadlineR.setTextColor(Color.parseColor("#42173b"));

        }
    }
}
