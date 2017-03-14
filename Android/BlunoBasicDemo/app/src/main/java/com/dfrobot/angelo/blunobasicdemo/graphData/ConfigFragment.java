package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.dfrobot.angelo.blunobasicdemo.BlunoLibrary;
import com.dfrobot.angelo.blunobasicdemo.graphData.GraphActivity.measType;
import com.dfrobot.angelo.blunobasicdemo.R;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Andrew on 2/19/2017.
 */

public class ConfigFragment extends Fragment {
    //Button scanBtn;
    Button measureBtn, vitalGoBtn, vitalCancelBtn ;
    View rootView;
    Spinner vitalSpinner, timeSpinner;
    //TextView timer;
    TextView progMsg, loadingText;
    CountDownTimer cTimer = null;
    //ProgressBar progressBar;
    //Snackbar mySnackbar;
    ProgressWheel progWheel;
    private Bundle savedState = null;
    //String[] loadDots = {".", "..", "..."};     //used to "animate the progMsg string
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

        progMsg = (TextView)rootView.findViewById(R.id.progMsg);
        loadingText = (TextView)rootView.findViewById(R.id.loadingText);
        measureBtn = (Button)rootView.findViewById(R.id.measureBtn);

        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(((GraphActivity) getActivity()).getConnectionState() == BlunoLibrary.connectionStateEnum.isConnected)) {
                    ((GraphActivity) getActivity()).scanDevices(v);
                }
                //cancel measurements if button pressed for a 2nd time
                else if (runCounter > 0){
                    //else {
                        if (cTimer != null) {
                    /*if (wakeLock != null)
                        wakeLock.release();*/
                            progMsg.setText("Cancelled");
                            loadingText.setText("");
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

                    vitalCancelBtn = (Button) vitalDialog.findViewById(R.id.vitalCancelBtn);
                    vitalCancelBtn.setOnClickListener(new View.OnClickListener(){
                        public void onClick( View v){
                            vitalDialog.dismiss();
                        }

                    });
                    //setContentView(R.layout.activity_graph);

                }

            }
        });


       // mySnackbar = Snackbar.make(rootView, R.string.connect_first, Snackbar.LENGTH_SHORT);
        progWheel = (ProgressWheel) rootView.findViewById(R.id.pw_spinner);

         /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the savedState first */
         /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("configFragSavedState");
        }
        if(savedState != null) {
            measureBtn.setText(savedState.getString("measureBtnText"));
            progMsg.setText(savedState.getString("progMsgText"));
        }
        savedState = null;
        return rootView;
    }

    public void setScanBtn(String text) {
        measureBtn.setText(text);
    }

    public void setProgMsg(String text){
        progMsg.setText(text);
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
                String progMsgStr;
                final int cntInterval = 300;
                final measType mType;
                // convert spinner value (string) to milliseconds
                String[] minSec = timeSpinner.getSelectedItem().toString().split(":"); //format: "mm:ss" - separate minutes and seconds
                cTime = Integer.parseInt(minSec[0]) * 60000 + Integer.parseInt(minSec[1]) * 1000;    //cTime is time to sample in milliseconds

                if (vitalSpinner.getSelectedItem().toString().equals("Respiration")) {
                    ((GraphActivity) getActivity()).sendToBluno("r" + cTime / 1000);   //tell Bluno how long we are sampling for in seconds
                    mType = measType.RESPIRATION;
                    progMsgStr = "Measuring Respiration";
                } else {
                    ((GraphActivity) getActivity()).sendToBluno("h" + cTime / 1000);   //tell Bluno how long we are sampling for in seconds
                    mType = measType.RESPIRATION;
                    progMsgStr = "Measuring Hydration";
                }
                progMsg.setText(progMsgStr);
                cTimer = new CountDownTimer(cTime, cntInterval) { //1st arg: time length in ms, 2nd arg: interval to call onTick()
                    float currProg = 0;
                    int dotInd = 0;
                    String loadStr = "";
                    //String progMsgStr = progMsgStr;
                    //float prevProg = 0;

                    public void onTick(long millisUntilFinished) {
                        long mins = millisUntilFinished / 60000;
                        long secs = (millisUntilFinished % 60000) / 1000;

                         //"animate" message so it indicates that measurement is in progress with: ".", "..", "...", etc.
                        loadingText.setText(loadStr);
                        loadStr += '.';
                        dotInd++;
                        if (dotInd == 4) {
                            loadStr = "";
                            dotInd = 0;
                        }
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
                        progMsg.setText("Done!");
                        loadingText.setText("");
                        runCounter = 0;
                        ((GraphActivity) getActivity()).processData(mType, (int) (cTime / 1000));
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
     /*   if (savedInstanceState !=null && measureBtn != null)
            measureBtn.setText(savedInstanceState.getString("measureBtnText"));*/
         //TODO: CREATE COORDINATOR LAYOUT FOR POPUP???
        //mySnackbar = Snackbar.make(rootView, R.string.connect_first, Snackbar.LENGTH_LONG);
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

    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putString("measureBtnText", measureBtn.getText().toString());
        state.putString("progMsgText", progMsg.getText().toString());
        return state;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState(); /* vstup defined here for sure */
        measureBtn = null;
        /*if (cTimer != null) //avoid memory leak
            cTimer.cancel();*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString("measureBtnText", measureBtn.getText().toString());
        /* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        outState.putBundle("configFragSavedState", (savedState != null) ? savedState : saveState());
    }

}
