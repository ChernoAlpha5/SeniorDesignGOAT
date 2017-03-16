package com.dfrobot.angelo.blunobasicdemo.graphData;


import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dfrobot.angelo.blunobasicdemo.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//import com.adafruit.bluefruit.le.connect.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RespirationFragment extends Fragment {

    int xAxisWindow = 30;          //(time in seconds) constrict x axis window to
    long referenceTimestamp = -1;   // initial time stamp represents time in seconds since 1970. Initially -1 to indicate it has not been set yet
    LineChart respChart;
    TextView breathsPerMin;
    View rootView;
    EditText editText;
    int x  = 0;
    private ArrayList<Entry> entries;
    private LineDataSet dataSet;
    IAxisValueFormatter xAxisFormatter;
    private Bundle savedState = null;
    DecimalFormat decFormat;
    DateFormat dateForm;
    ArrayList<DataTime> prevRespVals = new ArrayList<DataTime>();

    public RespirationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.g_fragment_respiration, container, false);

        final View respSendBtn = rootView.findViewById(R.id.respSend);
        respSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        respDispUserMsg(v);
                    }
                }
        );
        respChart = (LineChart)rootView.findViewById(R.id.respChart);
        respChart.setTouchEnabled(true); // enable touch gestures

        // set y axis options
        YAxis leftAxis = respChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawLabels(true);

        YAxis rightAxis = respChart.getAxisRight();
        rightAxis.setEnabled(false);

        //set axis min and max to 0, 100
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);

        //set x axis options
        XAxis xaxis = respChart.getXAxis();
        xaxis.setAxisMinimum(0f);
        xaxis.setAxisMaximum(86400);  //set x axis maximum to number of seconds in a day
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setTextSize(12f);

        //format the x axis to display time
        if (referenceTimestamp == -1){
            referenceTimestamp = System.currentTimeMillis()/1000;
        }
        xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        xaxis.setValueFormatter(xAxisFormatter);
        respChart.setVisibleXRange(0, xAxisWindow);  //x axis is in seconds

        breathsPerMin = (TextView)rootView.findViewById(R.id.respRate);
        editText = (EditText)rootView.findViewById(R.id.Redit_message);
        initRPlot();

        dateForm = new SimpleDateFormat("EEE, d MMM, HH:mm");    //for displaying vital reading
         //round respiration rate to 2 decimal places for displaying purposes
        decFormat = new DecimalFormat("##.##");
        decFormat.setRoundingMode(RoundingMode.DOWN);

        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("respFragSavedState");
        }
        if(savedState != null) {
           restoreData(savedState);
        }
        savedState = null;
        return rootView;
    }

    private void initRPlot(){
        entries = new ArrayList<Entry>();
        entries.add(new Entry(0, 0f));
        dataSet = new LineDataSet(entries, "Breaths per minute"); // add entries to dataset
        dataSet.setColor(Color.RED);
        LineData lineData = new LineData(dataSet);
        respChart.setData(lineData);
        respChart.invalidate(); // refresh
    }

    // add one point to resp plot
    private void addRPoint(float XVal, float YVal){
        LineData data = respChart.getData();
        data.notifyDataChanged();
        dataSet.addEntry(new Entry (XVal, YVal));
        respChart.notifyDataSetChanged(); // let the chart know it's data has changed
        respChart.moveViewToX(XVal - xAxisWindow/2);

    }

    /** Called when new UART data received. Must be public and void
     * Displays what the user entered into the edit_message widget
     * */
    public void respDispMsg(String data){
        //subtract start time from every subsequent time measurement
        long currTime = System.currentTimeMillis()/1000 -  referenceTimestamp;
        //String currMsg = breathsPerMin.getText().toString();
        try{
            Float fRespRate = Float.parseFloat(data);
            String mydate = dateForm.format(Calendar.getInstance().getTime());  //format date
            String vMsg = decFormat.format(fRespRate) + " BPM as of " + mydate; //round float to 2 decimal places
            breathsPerMin.setText(vMsg);
            prevRespVals.add(new DataTime(currTime, fRespRate,vMsg));//save data in ArrayList
            addRPoint(currTime, fRespRate);
        }
        catch(NumberFormatException e){
            breathsPerMin.setText("Invalid entry");
        }
    }

    /** Called when the user clicks the Send button. Must be public and void
     * Displays what the user entered into the edit_message widget
     * */
    //TODO: REMOVE METHOD? IT'S ONLY USED FOR DEBUGGING
    public void respDispUserMsg(View view){
        String message = editText.getText().toString();

        //subtract start time from every subsequent time measurement
        long currTime = System.currentTimeMillis()/1000 -  referenceTimestamp;

        try{
            Integer intRespRate = Integer.parseInt(message);
            DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
            String mydate = df.format(Calendar.getInstance().getTime());
            breathsPerMin.setText(intRespRate + " as of " + mydate);
            addRPoint(currTime, intRespRate);
        }
        catch(NumberFormatException e){
            breathsPerMin.setText("Invalid entry");
        }
    }

     //methods below save/restore graph state of fragment
    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelableArrayList("pastRespData", prevRespVals);
        return state;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState(); /* vstup defined here for sure */
        /*if (cTimer != null) //avoid memory leak
            cTimer.cancel();*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        outState.putBundle("respFragSavedState", (savedState != null) ? savedState : saveState());
    }

    private void restoreData(Bundle savedData){
        ArrayList<DataTime> prevResp  = savedData.getParcelableArrayList("pastRespData");
        for (int i = 0; i < prevResp.size(); i++){
            DataTime dt = prevResp.get(i);
            addRPoint(dt.getCurrTime(), dt.getVitalVal());
            if (i == prevResp.size() - 1){
                //update the vital status message if it is the most recent message
                breathsPerMin.setText(dt.getVitalMsg());
            }
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
