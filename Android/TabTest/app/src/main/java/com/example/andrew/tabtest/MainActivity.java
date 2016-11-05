package com.example.andrew.tabtest;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;


    // hydration plot variables
   // LineChart chart;
    //LineData hLineData;
    //ArrayList<Integer> LThydration = new ArrayList<Integer>();  //LThydration = "long-term hydration"
    ArrayList<String> hXAXES = new ArrayList<String>();
    ArrayList<Entry> hYAXES = new ArrayList<Entry>();
    double startTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        //chart = (LineChart) findViewById(R.id.hydrateChart);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HydrationFragment(), "Hydration");
        viewPagerAdapter.addFragments(new RespirationFragment(), "Respiration");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
