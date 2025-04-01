package com.inovationware.toolkit.ui.activity;

import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Code.stringToList;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.meeting.model.Contribution;
import com.inovationware.toolkit.meeting.service.impl.MeetingServiceImpl;
import com.inovationware.toolkit.databinding.ActivityBoardBinding;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.ui.adapter.BoardRecyclerViewAdapter;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivity extends BaseActivity {
    private MeetingServiceImpl service;
    private ActivityBoardBinding binding;
    private Context context;
    private SharedPreferencesManager store;
    private SignInManager user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupVariables();
        setupListeners();
    }

    private void setupVariables() {
        context = binding.getRoot().getContext();
        store = SharedPreferencesManager.getInstance();
        service = new MeetingServiceImpl(Factory.getInstance(), store, GroupManager.getInstance());
        user = SignInManager.getInstance();
    }

    private void setupListeners() {

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (binding.detail.getText().toString().isEmpty()) return;
                if (binding.meetingIdTextView.getText().toString().isEmpty()) return;

                service.contribute(context,
                        Contribution.create(
                                binding.meetingIdTextView.getText().toString(),
                                service.getDisplayName(context) + DomainObjects.NEW_LINE + "(" + service.getUsername(context) + ")",
                                LocalDateTime.now(),
                                Code.getTimezone(),
                                binding.contribution.getText().toString()
                        ));
            }
        });
        binding.loadMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.meetingIdTextView.getText().toString().isEmpty()) return;
                service.getMeeting(context, binding.meetingIdTextView.getText().toString(), binding.detail);
                binding.detail.setInputType(InputType.TYPE_NULL);

                //deal with recycler view
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.manageBoardMenuItem) {
            if (!user.isLoggedIn(context)) {
                SignInManager.getInstance().beginLoginProcess(context, ManageBoardActivity.class.getSimpleName());
                return true;
            }
            startActivity(new Intent(context, ManageBoardActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    Runnable getContributionsRoutine = new Runnable() {
        @Override
        public void run() {

            Retrofit retrofitImpl = Repo.getInstance().create(context, store);
            Call<String> navigate = retrofitImpl.readText(
                    HTTP_TRANSFER_URL(context, store),
                    store.getUsername(context),
                    store.getID(context),
                    String.valueOf(Transfer.Intent.meetingGetContributions),
                    binding.meetingIdTextView.getText().toString());
            navigate.enqueue(new Callback<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 401) {
                        //Todo more descriptive message
                        Toast.makeText(context, "Authorization Error!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.isSuccessful()) {
                        if (response.body() == null) return;

                        if (response.body().trim().isEmpty()) {
                            Toast.makeText(context, "No contributions yet", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (responseStringIsValid(response.body())) {
                            try {
                                setupConversationsRecyclerView(Contribution.getListing(response.body()));
                            } catch (Exception ignored) {
                                if (!store.shouldDisplayErrorMessage(context)) {
                                    return;
                                }
                                Toast.makeText(context, "The information received appear to be unusable, you can try again after a while.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "The information received appear to be unusable, you can try again after a while.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (store.shouldDisplayErrorMessage(context)) {
                            Toast.makeText(context, "Couldn't retrieve conversations at the moment.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!store.shouldDisplayErrorMessage(context)) {
                        return;
                    }
                    Toast.makeText(context, DEFAULT_FAILURE_MESSAGE_SUFFIX, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void setupConversationsRecyclerView(List<Contribution> contributions){
        hideConversationsRecyclerView();

        binding.boardRecyclerView.setAdapter(new BoardRecyclerViewAdapter(context, contributions));
        binding.boardRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        showConversationsRecyclerView();
    }

    private void hideConversationsRecyclerView(){
        binding.boardRecyclerView.setVisibility(View.GONE);
    }
    private void showConversationsRecyclerView(){
        binding.boardRecyclerView.setVisibility(View.VISIBLE);
    }
}