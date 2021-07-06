package com.skcoder.gomall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationFragment extends Fragment {

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationModel> productSpecificationModelList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecyclerView = view.findViewById(R.id.product_specification_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);

//        list.add(new ProductSpecificationModel(0, "General"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(0, "Desplay"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(0, "General"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(0, "Desplay"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));
//        list.add(new ProductSpecificationModel(1, "RAM", "4GB"));
//        list.add(new ProductSpecificationModel(1, "ROM", "128GB"));

        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();

        return view;
    }
}