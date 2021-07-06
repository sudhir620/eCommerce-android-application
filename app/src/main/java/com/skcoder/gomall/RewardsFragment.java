package com.skcoder.gomall;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class RewardsFragment extends Fragment {
    private RecyclerView rewardRecyclerView;
    private Dialog loadingDialog;
    public static RewardsAdapter rewardsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        ////// loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////// loading Dialog

        rewardRecyclerView = view.findViewById(R.id.rewards_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        rewardRecyclerView.setLayoutManager(layoutManager);

        rewardsAdapter = new RewardsAdapter(DBquries.rewardModelList, false);
        rewardRecyclerView.setAdapter(rewardsAdapter);

        if (DBquries.rewardModelList.size()==0){
            DBquries.loadRewards(getContext(), loadingDialog, true);
        }else {
            loadingDialog.dismiss();
        }
        rewardsAdapter.notifyDataSetChanged();


        return view;
    }
}