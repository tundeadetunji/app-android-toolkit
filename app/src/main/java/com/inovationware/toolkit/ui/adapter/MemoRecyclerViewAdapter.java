package com.inovationware.toolkit.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.ui.fragment.MemoBSFragment;

import java.util.List;

public class MemoRecyclerViewAdapter extends RecyclerView.Adapter<MemoRecyclerViewAdapter.ViewHolder> {
    public MemoRecyclerViewAdapter memoRecyclerViewAdapter;
    public Context context;
    public List<Memo> memos;
    private FragmentManager fragmentManager;
    private SharedPreferencesManager store;
    private GroupManager machines;
    public RecyclerView recyclerView;

    public MemoRecyclerViewAdapter(Context context, List<Memo> memos, FragmentManager fragmentManager, RecyclerView recyclerView, SharedPreferencesManager store, GroupManager machines) {
        this.context = context;
        this.memos = memos;
        this.fragmentManager = fragmentManager;
        this.recyclerView = recyclerView;
        this.store = store;
        this.machines = machines;

        this.memoRecyclerViewAdapter = this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_row, parent, false));
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memoTextField.setText(memos.get(holder.getAdapterPosition()).getPostnote());
        holder.memoTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Support.isWebResource(holder.memoTextField.getText().toString())) Support.visit(context, holder.memoTextField.getText().toString());
            }
        });

        holder.titleTextField.setText(memos.get(holder.getAdapterPosition()).getNoteTitle());
        holder.dateTextField.setText(memos.get(holder.getAdapterPosition()).getNoteDate());
        holder.memoOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoBSFragment bsFragment = new MemoBSFragment();
                bsFragment.setMemo(memos.get(holder.getAdapterPosition()));
                bsFragment.setDevice(DeviceClient.getInstance());
                bsFragment.setStore(store);
                bsFragment.setMachines(machines);
                /*bsFragment.setRecyclerView(recyclerView);
                bsFragment.setMemos(memos);
                bsFragment.setCallerFragmentManager(fragmentManager);
                bsFragment.setCallerView(callerView);*/
                bsFragment.setMemoRecyclerViewAdapter(memoRecyclerViewAdapter);
                bsFragment.show(fragmentManager, MemoBSFragment.TAG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView memoOptionsButton;
        TextView memoTextField;
        TextView dateTextField;
        TextView titleTextField;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memoOptionsButton = itemView.findViewById(R.id.memoOptionsImageView);
            memoTextField = itemView.findViewById(R.id.memoTextView);
            dateTextField = itemView.findViewById(R.id.dateTextView);
            dateTextField.setVisibility(View.GONE);
            titleTextField = itemView.findViewById(R.id.contributedAtTextView);
            titleTextField.setVisibility(View.GONE);
            itemView.findViewById(R.id.memoRowRootLayout).setAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation));
        }
    }
}
