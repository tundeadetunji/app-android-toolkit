package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.net_timer_objects;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentNotesBinding;
import com.inovationware.toolkit.databinding.FragmentPreviousTasksBinding;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.nettimer.model.NetTimerObject;
import com.inovationware.toolkit.ui.activity.NetTimerActivity;
import com.inovationware.toolkit.ui.adapter.NetTimerObjectRecyclerViewAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviousTasksFragment extends Fragment {
    private View view;
    private FragmentPreviousTasksBinding binding;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Handler ntoListRoutineHandler;
    private Feedback feedback;
    private SharedPreferencesManager store;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreviousTasksBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        context = view.getContext();

        feedback = new Feedback(context);
        store = SharedPreferencesManager.getInstance();

        recyclerView = view.findViewById(R.id.ntRecyclerView);
        progressBar = view.findViewById(R.id.ntProgressBar);
        ntoListRoutineHandler = new Handler();

        progressBar.setIndeterminate(true);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (thereIsInternet(context) && initialParamsAreSet(context, store, GroupManager.getInstance())) {
            ntoListRoutineHandler.post(ntoListRoutine);
        }



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    Runnable ntoListRoutine = new Runnable() {
        @Override
        public void run() {

            Retrofit retrofitImpl = Repo.getInstance().create(context, store);
            Call<String> navigate = retrofitImpl.readText(
                    HTTP_TRANSFER_URL(context, store),
                    store.getUsername(context),
                    store.getID(context),
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
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if (!responseStringIsValid(response.body())) {
                            if (store.shouldDisplayErrorMessage(context)) {
                                feedback.toast("that resulted in an error.", Toast.LENGTH_SHORT);
                            }
                            return;
                        }
                        try {
                            net_timer_objects = NetTimerObject.listing(response.body(), context);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(new NetTimerObjectRecyclerViewAdapter(context, net_timer_objects));
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}