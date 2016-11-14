package com.adafruit.bluefruit.le.connect.app.graphData;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.adafruit.bluefruit.le.connect.R;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;


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
        setContentView(R.layout.g_activity_graph);
        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.g_viewPager);
        //chart = (LineChart) findViewById(R.id.hydrateChart);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(new HydrationFragment(), "Hydration");
        pagerAdapter.addFragments(new RespirationFragment(), "Respiration");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
