package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.dfrobot.angelo.blunobasicdemo.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.*;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HydrationFragment extends Fragment {
    int xAxisWindow = 30;          //(time in seconds) constrict x axis window to
    long referenceTimestamp = -1;   // initial time stamp represents time in seconds since 1970. Initially -1 to indicate it has not been set yet
    LineChart hydrChart;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.g_fragment_hydration, container, false);

        final View hydrSendBtn = rootView.findViewById(R.id.hydrSend);
        hydrSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                /* DO SOMETHING UPON THE CLICK */
                        hydrateDispMsg(v);
                    }
                }
        );

        hydrChart = (LineChart)rootView.findViewById(R.id.hydrChart);
        hydrChart.setTouchEnabled(true); // enable touch gestures

         // set y axis options
        YAxis leftAxis = hydrChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawLabels(true);

        YAxis rightAxis = hydrChart.getAxisRight();
        rightAxis.setEnabled(false);

        //set axis min and max to 0, 100
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);

        //set x axis options
        XAxis xaxis = hydrChart.getXAxis();
        xaxis.setAxisMinimum(0f);
        xaxis.setAxisMaximum(86400);  //set x axis maximum to number of seconds in a day
        xaxis.setPosition(XAxisPosition.BOTTOM);
        xaxis.setTextSize(12f);
        //format the x axis to display time
        referenceTimestamp = System.currentTimeMillis()/1000;
        xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        xaxis.setValueFormatter(xAxisFormatter);
        hydrChart.setVisibleXRange(0, xAxisWindow);  //x axis is in seconds


        /*hydrChart.getXAxis().setAxisMinimum(0);
        hydrChart.getAxisLeft().setAxisMinimum(0);
        hydrChart.getAxisLeft().setAxisMinimum(100);*/
        hydrationLvl = (TextView)rootView.findViewById(R.id.hydrationLvl);
        editText = (EditText)rootView.findViewById(R.id.edit_message);
        initHPlot();
        return rootView;
    }

    // plot hydration graph
    private void initHPlot(){
        entries = new ArrayList<Entry>();
        entries.add(new Entry(0, 0f));
        dataSet = new LineDataSet(entries, "Hydration Level"); // add entries to dataset
        dataSet.setColor(Color.RED);
        LineData lineData = new LineData(dataSet);
        hydrChart.setData(lineData);
        hydrChart.invalidate(); // refresh

        //test graph by adding points
        /*addHPoint(0.2f,0.6f);
        addHPoint(1,6f);
        addHPoint(0,6f);*/
    }



    // add one point to hydration plot
    private void addHPoint(float XVal, float YVal){
        LineData data = hydrChart.getData();
        //XAxis xaxis = hydrChart.getXAxis();
        //data.addEntry(new Entry(XVal, YVal));
        data.notifyDataChanged();
        dataSet.addEntry(new Entry (XVal, YVal));
        hydrChart.notifyDataSetChanged(); // let the chart know it's data has changed
        //hydrChart.setVisibleXRangeMinimum(4f);
        //hydrChart.setVisibleXRangeMaximum(8);
        //
        //hydrChart.zoom(1,1, XVal, YVal);
        hydrChart.moveViewToX(XVal - xAxisWindow/2);
        /*if (XVal > xAxisInitMax){

            //set x axis options
            //hydrChart.setVisibleXRange(XVal-3, XVal+3);
            hydrChart.moveViewToX(data.getEntryCount()-5);
            //xaxis.setAxisMinimum(XVal + 3f);
            //xaxis.setAxisMaximum(XVal - 3f);
        }*/
        //hydrChart.invalidate(); // refresh
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
            XAxis xAxis = hydrChart.getXAxis();
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

    /** Called when new UART data received. Must be public and void
     * Displays what the user entered into the edit_message widget
     * */
    public void hydrateDispMsg2(String data){
        //String message = editText.getText().toString();

        // set the start time in seconds the first time this function runs
        /*if (referenceTimestamp < 0){
            referenceTimestamp = System.currentTimeMillis()/1000;
            xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
            XAxis xAxis = hydrChart.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);

        }*/

        //subtract start time from every subsequent time measurement
        long currTime = System.currentTimeMillis()/1000 -  referenceTimestamp;
        String currMsg = hydrationLvl.getText().toString();
        try{

            Float intHydrateLvl = Float.parseFloat(data);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
