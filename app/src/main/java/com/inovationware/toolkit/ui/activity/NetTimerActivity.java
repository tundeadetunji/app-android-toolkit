package com.inovationware.toolkit.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.common.infrastructure.retrofit.Retrofit;
import com.inovationware.toolkit.common.infrastructure.retrofit.Repo;
import com.inovationware.toolkit.ui.adapter.NetTimerObjectRecyclerViewAdapter;
import com.inovationware.toolkit.features.nettimer.model.NetTimerObject;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.net_timer_objects;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.common.utility.Support.responseStringIsValid;

public class NetTimerActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Handler ntoListRoutineHandler;
    private Feedback feedback;
    private SharedPreferencesManager store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_timer);
        feedback = new Feedback(getApplicationContext());
        store = SharedPreferencesManager.getInstance();

        recyclerView = findViewById(R.id.ntRecyclerView);
        progressBar = findViewById(R.id.ntProgressBar);
        ntoListRoutineHandler = new Handler();

        progressBar.setIndeterminate(true);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (thereIsInternet(getApplicationContext()) && initialParamsAreSet(NetTimerActivity.this, store, GroupManager.getInstance())) {
            ntoListRoutineHandler.post(ntoListRoutine);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
    }


    Runnable ntoListRoutine = new Runnable() {
        @Override
        public void run() {

            Retrofit retrofitImpl = Repo.getInstance().create(NetTimerActivity.this, store);
            Call<String> navigate = retrofitImpl.readText(
                    HTTP_TRANSFER_URL(NetTimerActivity.this, store),
                    store.getUsername(NetTimerActivity.this),
                    store.getID(NetTimerActivity.this),
                    String.valueOf(Transfer.Intent.readNetTimerTasks),
                    DomainObjects.EMPTY_STRING
            );
            navigate.enqueue(new Callback<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (response.body().trim().length() < 1) {
                            feedback.toast("Nothing from home at the moment.", Toast.LENGTH_SHORT);
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (!responseStringIsValid(response.body())) {
                            if (store.shouldDisplayErrorMessage(NetTimerActivity.this)) {
                                feedback.toast("that resulted in an error.", Toast.LENGTH_SHORT);
                            }
                            return;
                        }
                        try {
                            net_timer_objects = NetTimerObject.listing(response.body(), getApplicationContext());
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setAdapter(new NetTimerObjectRecyclerViewAdapter(getApplicationContext(), net_timer_objects));
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        }
    };

}