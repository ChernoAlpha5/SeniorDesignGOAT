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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    int xAxisInitMax = 3;
    LineChart hydrateChart;
    TextView hydrationLvl;
    View rootView;
    EditText editText;
    int x  = 0;
    private ArrayList<Entry> entries;
    private LineDataSet dataSet;
    public HomeFragment() {
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
        //set axis min and max to 0, 100
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = hydrateChart.getAxisRight();
        rightAxis.setEnabled(false);

        //set x axis options
        XAxis xaxis = hydrateChart.getXAxis();
        xaxis.setAxisMinimum(0f);
        xaxis.setAxisMaximum(100f);
        hydrateChart.setVisibleXRange(0, xAxisInitMax + 1);

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

        /*addHPoint(0.2f,0.6f);
        addHPoint(1,6f);
        addHPoint(0,6f);*/
    }

    // add one point to hydration plot
    private void addHPoint(float XVal, float YVal){
        LineData data = hydrateChart.getData();
        XAxis xaxis = hydrateChart.getXAxis();
        //data.addEntry(new Entry(XVal, YVal));
        data.notifyDataChanged();
        dataSet.addEntry(new Entry (XVal, YVal));
        hydrateChart.notifyDataSetChanged(); // let the chart know it's data changed
        //hydrateChart.setVisibleXRangeMinimum(4f);
        //hydrateChart.setVisibleXRangeMaximum(8);
        //
        //hydrateChart.zoom(1,1, XVal, YVal);
        hydrateChart.moveViewToX(data.getEntryCount()-5);
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

    /*private void addEn34try(Entry entry) {

        LineData data = hydrateChart.getData();

        if (data != null) {

            // get the dataset where you want to add the entry
            LineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                // create a new DataSet if there is none yet
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue("somestring");
            data.addEntry(entry, 0);

            // let the chart know it's data has changed
            hydrateChart.notifyDataSetChanged();

            // limit the number of visible entries
            hydrateChart.setVisibleXRange(0,6);

            // move to the latest entry
            hydrateChart.moveViewToX(data.getXValCount()-7);
        }
    } */

    /** Called when the user clicks the Send button. Must be public and void
     * Displays what the user entered into the edit_message widget
     * */
    public void hydrateDispMsg(View view){
        String message = editText.getText().toString();

        try{
            Integer intHydrateLvl = Integer.parseInt(message);
            DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
            String mydate = df.format(Calendar.getInstance().getTime());
            hydrationLvl.setText(intHydrateLvl  + "% as of " + mydate);

            addHPoint(x, intHydrateLvl);
            x++;
        }
        catch(NumberFormatException e){
            hydrationLvl.setText("Invalid entry");
        }
    }


}
