package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.cachedMemos;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentNotesBinding;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.infrastructure.retrofit.Repo;
import com.inovationware.toolkit.common.infrastructure.retrofit.Retrofit;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.ui.adapter.MemoRecyclerViewAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesFragment extends Fragment {

    private View view;
    private FragmentNotesBinding binding;

    private RecyclerView memoRecyclerView;
    private Handler readNotesHandler;

    private SharedPreferencesManager store;
    private GroupManager machines;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        initializeComponents();
        setupNotes(view);

        return view;

    }

    private void initializeComponents(){
        memoRecyclerView = view.findViewById(R.id.memoRecyclerView);
        readNotesHandler = new Handler();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();

    }

    public void setupNotes(View view) {
        memoRecyclerView.setVisibility(View.GONE);

        if (cachedMemos != null) {
            memoRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            memoRecyclerView.setAdapter(new MemoRecyclerViewAdapter(view.getContext(), cachedMemos, getFragmentManager(), memoRecyclerView, store, machines));
            memoRecyclerView.setVisibility(View.VISIBLE);
            return;
        }

        readNotesHandler.post(new Runnable() {
            @Override
            public void run() {
                readNotes(view.getContext());
            }
        });
    }

    void readNotes(Context context) {
        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readNote(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.readNote),
                DomainObjects.POST_PURPOSE_READ_NOTE
        );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    List<Memo> memos;
                    //System.out.println("\n***\n" + response.body() + "\n***");
                    try {
                        memos = Memo.listing(response);
                        Collections.sort(memos, new Memo.MemoComparator());
                        cachedMemos = memos;
                    } catch (IOException ignored) {
                        return;
                    }

                    if (memos == null) return;
                    if (memos.isEmpty()) return;

                    memoRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    memoRecyclerView.setAdapter(new MemoRecyclerViewAdapter(view.getContext(), memos, getFragmentManager(), memoRecyclerView, store, machines));
                    memoRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}