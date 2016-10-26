package com.example.andrew.tabtest;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView hydrationLvl;
    ArrayList<Integer> LThydration = new ArrayList<Integer>();  //LThydration = "long-term hydration"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment(), "Hydration");
        viewPagerAdapter.addFragments(new TopFreeFragment(), "Respiration");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /** Called when the user clicks the Send button. Must be public and void
     * Displays what the user entered into the edit_message widget*/
    public void dispMessage(View view){
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        try{
            Integer intHydrateLvl = Integer.parseInt(message);
            DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
            String mydate = df.format(Calendar.getInstance().getTime());
            hydrationLvl = (TextView)findViewById(R.id.hydrationLvl);
            hydrationLvl.setText(intHydrateLvl  + "% as of " + mydate);
            LThydration.add(intHydrateLvl);
        }
        catch(NumberFormatException e){
            hydrationLvl.setText("Invalid entry");
        }
    }
}
