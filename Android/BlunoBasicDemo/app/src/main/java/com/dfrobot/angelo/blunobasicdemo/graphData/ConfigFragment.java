package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dfrobot.angelo.blunobasicdemo.R;

/**
 * Created by Andrew on 2/19/2017.
 */

public class ConfigFragment extends Fragment {
    Button scanBtn, measureBtn;
    View rootView;
    Spinner vitalSpinner, timeSpinner;
    TextView timer;
    CountDownTimer cTimer = null;
    ProgressBar progressBar;
    int runCounter = 0; //if run counter = 0 (when entering section) run counter, else wait
    private Handler mHandler = new Handler();

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.g_fragment_config, container, false);

        scanBtn = (Button) rootView.findViewById(R.id.scanBtn);
        timer = (TextView) rootView.findViewById(R.id.timer);
        measureBtn = (Button) rootView.findViewById(R.id.measureBtn);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.g_activity_graph);
                countdown(v);
            }
        });
        // create vital spinner
        vitalSpinner = (Spinner) rootView.findViewById(R.id.vitalSpinner);      //must call getView (returns root view) if inside fragment
         // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> vitalAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.vitals_arr, android.R.layout.simple_spinner_item);
         // Specify the layout to use when the list of choices appears
        vitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         // Apply the adapter to the spinner
        vitalSpinner.setAdapter(vitalAdapter);
        // Inflate the layout for this fragment

        // create time spinner
        timeSpinner = (Spinner) rootView.findViewById(R.id.timeSpinner);      //must call getView (returns root view) if inside fragment
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.time_arr, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        timeSpinner.setAdapter(timeAdapter);
        // Inflate the layout for this fragment

        return rootView;
    }

    public void setScanBtn(String text) {
        scanBtn.setText(text);
    }

    public void countdown(View v){
        final long cTime;

        if (runCounter == 0){ //prevent 2nd timer from being created before 1st timer finishes
             // convert spinner value (string) to milliseconds
            runCounter++;     //REAL JANKY CODE OVER HERE
            ((GraphActivity)getActivity()).sendToBluno("h6");
            measureBtn.setText("cancel");
            String[] minSec = timeSpinner.getSelectedItem().toString().split(":"); //format: "mm:ss" - separate minutes and seconds
            cTime = Integer.parseInt(minSec[0]) * 60000 + Integer.parseInt(minSec[1]) * 1000;    //60000 milliseconds in one minute
            cTimer = new CountDownTimer(cTime, 500) { //1st arg: time length in ms, 2nd arg: interval to call onTick()

                public void onTick(long millisUntilFinished) {
                    long mins =  millisUntilFinished / 60000;
                    long secs = (millisUntilFinished % 60000) / 1000;
                    String strSecs = secs + "";
                    if (secs < 10)
                        strSecs = "0" + secs;
                    //timer.setText(/*"seconds remaining: " + */ millisUntilFinished/ 60000 + ":" + millisUntilFinished / 1000);
                    timer.setText(mins + ":" + strSecs);
                    //here you can have your logic to set text to edittext
                    progressBar.setProgress(progressBar.getMax() - (int)(100* (float)millisUntilFinished / cTime));        //not working!!!
                }

                public void onFinish() {
                    progressBar.setProgress(progressBar.getMax());
                    timer.setText("Done!");
                    runCounter = 0;
                }
            }.start();
        }
        else{   //cancel measurements
            if (cTimer != null)
                measureBtn.setText("measure");
                progressBar.setProgress(0);
                timer.setText("Timer");
                cTimer.cancel();
                runCounter = 0;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cTimer != null) //avoid memory leak
            cTimer.cancel();
            measureBtn.setText("measure");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*if (cTimer != null) //avoid memory leak
            cTimer.cancel();*/
    }
}
