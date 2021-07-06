package com.skcoder.gomall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cuberto.liquid_swipe.LiquidPager;
import com.denzcoskun.imageslider.adapters.ViewPagerAdapter;

public class IntroductionActivity extends AppCompatActivity {

    LiquidPager pager;
    ViewPager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        pager = findViewById(R.id.pager);

        adapter = new ViewPager(getSupportFragmentManager(),1 );
        pager.setAdapter(adapter);
    }
}