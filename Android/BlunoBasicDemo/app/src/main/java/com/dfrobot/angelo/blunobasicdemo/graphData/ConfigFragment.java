package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.dfrobot.angelo.blunobasicdemo.BlunoLibrary;
import com.dfrobot.angelo.blunobasicdemo.R;
import com.todddavies.components.progressbar.ProgressWheel;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Andrew on 2/19/2017.
 */

public class ConfigFragment extends Fragment {
    //Button scanBtn;
    Button measureBtn, vitalGoBtn;
    View rootView /*, popupView*/;
    Spinner vitalSpinner, timeSpinner;
    //TextView timer;
    CountDownTimer cTimer = null;
    //ProgressBar progressBar;
    Snackbar mySnackbar;
    ProgressWheel progWheel;
    //WakeLock wakeLock;
    int runCounter = 0; //if run counter = 0 (when entering section) run counter, else wait
    private Handler mHandler = new Handler();

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.g_fragment_config, container, false);
        //popupView = inflater.inflate(R.layout.vital_popup, container, false);
        //scanBtn = (Button) rootView.findViewById(R.id.scanBtn);
        //timer = (TextView) rootView.findViewById(R.id.timer);
        measureBtn = (Button) rootView.findViewById(R.id.measureBtn);
        //progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(((GraphActivity) getActivity()).getConnectionState() == BlunoLibrary.connectionStateEnum.isConnected)) {
                    ((GraphActivity) getActivity()).scanDevices(v);
                }
                else if (runCounter > 0){
                    //else {   //cancel measurements
                        if (cTimer != null) {
                    /*if (wakeLock != null)
                        wakeLock.release();*/
                            ((GraphActivity) getActivity()).sendToBluno("c");   //tell Bluno to cancel measurements
                            ((GraphActivity) getActivity()).clearData();
                            measureBtn.setText("measure");
                            //progressBar.setProgress(0);

                            //timer.setText("Timer");
                            progWheel.setProgress(0);
                            progWheel.setText("0:00");
                            cTimer.cancel();
                            runCounter = 0;
                        }
                    //}
                }
                else{
                    final Dialog vitalDialog = new Dialog(getContext());
                    vitalDialog.setTitle("Measurement Setup");
                    vitalDialog.setContentView(R.layout.vital_popup);
                    vitalDialog.show();

                    // create vital spinner
                    vitalSpinner = (Spinner) vitalDialog.findViewById(R.id.vitalSpinner);      //must call getView (returns root view) if inside fragment
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> vitalAdapter = ArrayAdapter.createFromResource(getContext(), R.array.vitals_arr, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    vitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    vitalSpinner.setAdapter(vitalAdapter);

                    // create time spinner (same process as vital spinner)
                    timeSpinner = (Spinner) vitalDialog.findViewById(R.id.timeSpinner);
                    ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.time_arr, android.R.layout.simple_spinner_item);
                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timeSpinner.setAdapter(timeAdapter);

                    vitalGoBtn = (Button) vitalDialog.findViewById(R.id.vitalGoBtn);
                    vitalGoBtn.setOnClickListener(new View.OnClickListener(){
                        public void onClick( View v){
                            vitalDialog.dismiss();
                            countdown(v);
                        }

                    });
                    //setContentView(R.layout.g_activity_graph);


                }

            }
        });


       // mySnackbar = Snackbar.make(rootView, R.string.connect_first, Snackbar.LENGTH_SHORT);
        progWheel = (ProgressWheel) rootView.findViewById(R.id.pw_spinner);
        return rootView;
    }

    public void setScanBtn(String text) {
        measureBtn.setText(text);
    }

    public void countdown(View v) {
        //ensure smartphone is connected to Bluno before measuring
       /* if (!(((GraphActivity) getActivity()).getConnectionState() == BlunoLibrary.connectionStateEnum.isConnected)) {
            ((GraphActivity) getActivity()).scanDevices(v);
        }*/
        if ((((GraphActivity) getActivity()).getConnectionState() == BlunoLibrary.connectionStateEnum.isConnected)) {
            if (runCounter == 0) { //prevent 2nd timer from being created before 1st timer finished
                //acquire wakelock to prevent CPU from going to sleep during BT transmission
                /*PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BTwakelock");
                wakeLock.acquire();*/

                final long cTime;
                runCounter++;
                measureBtn.setText("cancel");
                final int cntInterval = 500;
                // convert spinner value (string) to milliseconds
                String[] minSec = timeSpinner.getSelectedItem().toString().split(":"); //format: "mm:ss" - separate minutes and seconds
                cTime = Integer.parseInt(minSec[0]) * 60000 + Integer.parseInt(minSec[1]) * 1000;    //cTime is time to sample in milliseconds

                if (vitalSpinner.getSelectedItem().toString().equals("Respiration")) {
                    ((GraphActivity) getActivity()).sendToBluno("r" + cTime / 1000);   //tell Bluno how long we are sampling for in seconds
                } else {
                    ((GraphActivity) getActivity()).sendToBluno("h" + cTime / 1000);   //tell Bluno how long we are sampling for in seconds
                }
                cTimer = new CountDownTimer(cTime, cntInterval) { //1st arg: time length in ms, 2nd arg: interval to call onTick()
                    float currProg = 0;
                    float prevProg = 0;

                    public void onTick(long millisUntilFinished) {
                        long mins = millisUntilFinished / 60000;
                        long secs = (millisUntilFinished % 60000) / 1000;
                        int divider = 1000 / cntInterval;
                        String strSecs = secs + "";
                        if (secs < 10)
                            strSecs = "0" + secs;

                        //timer.setText(mins + ":" + strSecs);
                        //progressBar.setProgress(progressBar.getMax() - (int)(100* (float)millisUntilFinished / cTime));
                        currProg = 360 - 360 * ((float) millisUntilFinished / cTime);
                        progWheel.setProgress((int) (currProg + 0.5));   //round to nearest int
                        progWheel.setText(mins + ":" + strSecs);
                    }

                    public void onFinish() {
                        measureBtn.setText("measure");
                        //progressBar.setProgress(progressBar.getMax());
                        progWheel.setProgress(360);
                        progWheel.setText("0:00");
                        //timer.setText("Done!");
                        runCounter = 0;
                        ((GraphActivity) getActivity()).processData((int) (cTime / 1000));
                        /*if (wakeLock != null)
                            wakeLock.release();*/
                    }
                }.start();
            }
        }

 /*       else{ //display error message telling user to connect before measuring
            mySnackbar.show();
        }*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         //TODO: CREATE COORDINATOR LAYOUT FOR POPUP???
        mySnackbar = Snackbar.make(rootView, R.string.connect_first, Snackbar.LENGTH_LONG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cTimer != null){
            //avoid memory leak
            cTimer.cancel();
        }
        /*if (wakeLock != null){
            wakeLock.release();
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*if (cTimer != null) //avoid memory leak
            cTimer.cancel();*/
    }
}
