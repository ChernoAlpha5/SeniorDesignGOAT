package com.example.andrew.tabtest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.*;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HydrationFragment extends Fragment {
    int xAxisWindow = 30;          //(time in seconds) constrict x axis window to
    long referenceTimestamp = -1;   // initial time stamp represents time in seconds since 1970. Initially -1 to indicate it has not been set yet
    LineChart hydrateChart;
    TextView hydrationLvl;
    View rootView;
    EditText editText;
    int x  = 0;
    private ArrayList<Entry> entries;
    private LineDataSet dataSet;
    IAxisValueFormatter xAxisFormatter;

    public HydrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        final View button1 = rootView.findViewById(R.id.button1);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                /* DO SOMETHING UPON THE CLICK */
                        hydrateDispMsg(v);
                    }
                }
        );

        hydrateChart = (LineChart)rootView.findViewById(R.id.hydrateChart);
        hydrateChart.setTouchEnabled(true); // enable touch gestures

         // set y axis options
        YAxis leftAxis = hydrateChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawLabels(true);

        YAxis rightAxis = hydrateChart.getAxisRight();
        rightAxis.setEnabled(false);

        //set axis min and max to 0, 100
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);

        //set x axis options
        XAxis xaxis = hydrateChart.getXAxis();
        xaxis.setAxisMinimum(0f);
        xaxis.setAxisMaximum(86400);  //set x axis maximum to number of seconds in a day
        xaxis.setPosition(XAxisPosition.BOTTOM);
        xaxis.setTextSize(12f);
        //format the x axis to display time
        referenceTimestamp = System.currentTimeMillis()/1000;
        xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        xaxis.setValueFormatter(xAxisFormatter);
        hydrateChart.setVisibleXRange(0, xAxisWindow);  //x axis is in seconds


        /*hydrateChart.getXAxis().setAxisMinimum(0);
        hydrateChart.getAxisLeft().setAxisMinimum(0);
        hydrateChart.getAxisLeft().setAxisMinimum(100);*/
        hydrationLvl = (TextView)rootView.findViewById(R.id.hydrationLvl);
        editText = (EditText)rootView.findViewById(R.id.edit_message);
        initHPlot();
        return rootView;
    }

    // plot hydration graph
    private void initHPlot(){
        entries = new ArrayList<>();
        entries.add(new Entry(0, 0f));
        dataSet = new LineDataSet(entries, "Hydration Level"); // add entries to dataset
        dataSet.setColor(Color.RED);
        LineData lineData = new LineData(dataSet);
        hydrateChart.setData(lineData);
        hydrateChart.invalidate(); // refresh

        //test graph by adding points
        /*addHPoint(0.2f,0.6f);
        addHPoint(1,6f);
        addHPoint(0,6f);*/
    }

    // add one point to hydration plot
    private void addHPoint(float XVal, float YVal){
        LineData data = hydrateChart.getData();
        //XAxis xaxis = hydrateChart.getXAxis();
        //data.addEntry(new Entry(XVal, YVal));
        data.notifyDataChanged();
        dataSet.addEntry(new Entry (XVal, YVal));
        hydrateChart.notifyDataSetChanged(); // let the chart know it's data has changed
        //hydrateChart.setVisibleXRangeMinimum(4f);
        //hydrateChart.setVisibleXRangeMaximum(8);
        //
        //hydrateChart.zoom(1,1, XVal, YVal);
        hydrateChart.moveViewToX(XVal - xAxisWindow/2);
        /*if (XVal > xAxisInitMax){

            //set x axis options
            //hydrateChart.setVisibleXRange(XVal-3, XVal+3);
            hydrateChart.moveViewToX(data.getEntryCount()-5);
            //xaxis.setAxisMinimum(XVal + 3f);
            //xaxis.setAxisMaximum(XVal - 3f);
        }*/
        //hydrateChart.invalidate(); // refresh
        //entries.add(new Entry (XVal, YVal));
    }


    /** Called when the user clicks the Send button. Must be public and void
     * Displays what the user entered into the edit_message widget
     * */
    public void hydrateDispMsg(View view){
        String message = editText.getText().toString();

        // set the start time in seconds the first time this function runs
        /*if (referenceTimestamp < 0){
            referenceTimestamp = System.currentTimeMillis()/1000;
            xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
            XAxis xAxis = hydrateChart.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);

        }*/

        //subtract start time from every subsequent time measurement
        long currTime = System.currentTimeMillis()/1000 -  referenceTimestamp;

        try{
            Integer intHydrateLvl = Integer.parseInt(message);
            DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
            String mydate = df.format(Calendar.getInstance().getTime());
            hydrationLvl.setText(intHydrateLvl  + "% as of " + mydate);
            addHPoint(currTime, intHydrateLvl);
            //addHPoint(x, intHydrateLvl);
            //x++;
        }
        catch(NumberFormatException e){
            hydrationLvl.setText("Invalid entry");
        }
    }


}
