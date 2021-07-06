package com.skcoder.gomall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.skcoder.gomall.DBquries.lists;
import static com.skcoder.gomall.DBquries.loadFragmentData;
import static com.skcoder.gomall.DBquries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.ctoolbaar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        /////////// home page fake list
        List<SlideModel> bannerSliderFakeList = new ArrayList<>();
        bannerSliderFakeList.add(new SlideModel(R.drawable.loading_spinner, null, ScaleTypes.FIT));
        bannerSliderFakeList.add(new SlideModel(R.drawable.loading_spinner, null, ScaleTypes.FIT));
        bannerSliderFakeList.add(new SlideModel(R.drawable.loading_spinner, null, ScaleTypes.FIT));
        bannerSliderFakeList.add(new SlideModel(R.drawable.loading_spinner, null, ScaleTypes.FIT));
        bannerSliderFakeList.add(new SlideModel(R.drawable.loading_spinner, null, ScaleTypes.FIT));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,bannerSliderFakeList));
        homePageModelFakeList.add(new HomePageModel(1, ""));
        homePageModelFakeList.add(new HomePageModel(2, horizontalProductScrollModelFakeList, "", "#ffffff", new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3, horizontalProductScrollModelFakeList, "", "#ffffff"));
        /////////// home page fake list

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        LinearLayoutManager homepageLayoutManager = new LinearLayoutManager(CategoryActivity.this);
        homepageLayoutManager.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclerView.setLayoutManager(homepageLayoutManager);

        adapter = new HomePageAdapter(homePageModelFakeList);

        int listPosition = 0;
        for (int x=0; x < loadedCategoriesNames.size(); x++){
            if (loadedCategoriesNames.get(x).equals(title.toUpperCase())){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadedCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(categoryRecyclerView, this, loadedCategoriesNames.size() - 1, title);
        }else {
            adapter= new HomePageAdapter(lists.get(listPosition));
        }
        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_icon, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_menu:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}