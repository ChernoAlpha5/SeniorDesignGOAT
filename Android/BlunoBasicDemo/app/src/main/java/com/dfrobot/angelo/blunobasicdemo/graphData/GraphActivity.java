package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.dfrobot.angelo.blunobasicdemo.BlunoLibrary;
import com.dfrobot.angelo.blunobasicdemo.Filter.filterData;
import com.dfrobot.angelo.blunobasicdemo.MainActivity;
import com.dfrobot.angelo.blunobasicdemo.R;
import com.github.mikephil.charting.data.Entry;
import android.view.View;
import android.widget.Button;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class GraphActivity extends BlunoLibrary {
    //UI elements
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    HydrationFragment hydrateFrag;
    RespirationFragment respirationFrag;
    ConfigFragment configFrag;
    Button scanBtn;

    // hydration plot variables
    ArrayList<String> hXAXES = new ArrayList<String>();
    ArrayList<Entry> hYAXES = new ArrayList<Entry>();
    double startTime = 0;

    //data collection variables
    int numSamples = 0;
    int maxSamples = 4000;
    int threshold = 1000; //minimum PPG data value. Anything lower than this is discarded
    ArrayList<String> samples = new ArrayList<String>(maxSamples/2);
    ArrayList<Float> rData = new ArrayList<Float>(maxSamples/2);
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateProcess();						//onCreate Process by BlunoLibrary
        serialBegin(115200);					//set the Uart Baudrate on BLE chip to 115200

        setContentView(R.layout.g_activity_graph);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.g_viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        hydrateFrag = new HydrationFragment();
        respirationFrag = new RespirationFragment();
        configFrag = new ConfigFragment();
        scanBtn = (Button) findViewById(R.id.scanBtn);
        viewPagerAdapter.addFragments(configFrag, "Configure");
        viewPagerAdapter.addFragments(hydrateFrag, "Hydration");
        viewPagerAdapter.addFragments(respirationFrag, "Respiration");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        requestLocationPermissionIfNeeded();
    }

    public ViewPagerAdapter getPagerAdapter() {
        return viewPagerAdapter;
    }

    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();														//onResume Process by BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        String connectionState = null;
        switch (theConnectionState) {											//Four connection state

            case isConnected:
                //scanBtn.setText("Connected");   //TODO: FIX BUTTON CHANGES
                connectionState = "Connected";
                break;
            case isConnecting:
                //scanBtn.setText("Connecting");
                connectionState = "Connecting";
                break;
            case isToScan:
                //scanBtn.setText("Scan");
                connectionState = "Scan";
                break;
            case isScanning:
                //scanBtn.setText("Scanning");
                connectionState = "Scanning";
                break;
            case isDisconnecting:
                connectionState = "isDisconnecting";
                //scanBtn.setText("isDisconnecting");
                break;
            default:
                break;
        }
        if (connectionState != null){
            configFrag = (ConfigFragment) viewPagerAdapter.getFragment(0);
            configFrag.setScanBtn(connectionState);
        }
    }
    public void scanDevices(View v){
        buttonScanOnClickProcess();
    }

    @Override
    public void onSerialReceived(final String theString) {//Once connection data received, this function will be called

        if (samples.size() <  maxSamples ){
            samples.add(theString);
        }
/*        else {
            int numErrors = processData(samples);
            System.out.println("Samples correct? " + numErrors);
        }*/

            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //hydrateFrag = (HydrationFragment) getSupportFragmentManager().findFragmentById(R.id.hydration_fragment);
                    hydrateFrag = (HydrationFragment) viewPagerAdapter.getFragment(1);  //is 0 the position in the hashmap??
                    if (hydrateFrag != null) {
                        //String byteStr = "";
                        //String byteStr = new String(theString, "ASCII"); // for ASCII encoding
                        String s = theString.replaceAll("(\\r\\n|\\r)", "\n");
                        byte[] byteArr = s.getBytes();
                        hydrateFrag.hydrateDispMsg2(bytesToText(byteArr, true));
                    }
                }
            });*/

        //}

    }

    public void sendToBluno(String text){
         //TODO: check if BT has been connected
        serialSend(text);				//send the data to the BLUNO

    }
    public connectionStateEnum getConnectionState(){
        return mConnectionState;
    }
    //TODO: call filtereing method in this function
    //this function tests if there is any data lost, where data goes from 0 to val, and then val to 0
    public void processData(int seconds){
        filterData fData = new filterData();
         //format data to remove \\r\\n characters and discard any data less than <threshold>
        for (int i = 1; i < samples.size(); i++){  //ensure no newline characters in string
            String s = samples.get(i);
            char delineator = '\r';
            //int index = s.indexOf(delineator);
             //data error - sometimes there are 2+ datas per entry in data ArrayList
            try{
                if (s.indexOf(delineator) > 0 && s.indexOf(delineator) < (s.length() - 3)){
                    //data.remove(i);
                    String[] sArr = s.split("\\r\\n");
                    int currInd = i;
                    for (int x = 0; x < sArr.length; x ++) {
                        Float f = Float.parseFloat(sArr[x]);
                        if (f >= threshold){
                            rData.add(/*currInd,*/ f);
                            currInd++;
                        }
                    }
                    i = currInd -1; //update index after inserting data
                }
                else{
                    Float f = Float.parseFloat(samples.get(i).replaceAll("\\r\\n", ""));
                    if (f >= threshold){
                        rData.add(f);
                    }
                }
            }
            catch(NumberFormatException e){e.printStackTrace();}    //in case the number is not a valid float
        }
        int breaths = 0;
        if (rData.size() > 0)
            breaths = fData.calculateRespRate(rData);

        rData.clear();
        samples.clear();
        final float breathsPerMin = breaths/((float)seconds/60);
        System.out.println(breathsPerMin);     //calculate breaths per minute

         //graph filtered result. TODO: PUT RESULT ON RESPIRATION GRAPH
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //hydrateFrag = (HydrationFragment) getSupportFragmentManager().findFragmentById(R.id.hydration_fragment);
                    hydrateFrag = (HydrationFragment) viewPagerAdapter.getFragment(1);  //is 0 the position in the hashmap??
                    if (hydrateFrag != null) {
                        //String byteStr = "";
                        //String byteStr = new String(theString, "ASCII"); // for ASCII encoding
                        //String s = theString.replaceAll("(\\r\\n|\\r)", "\n");
                        //byte[] byteArr = s.getBytes();
                        //hydrateFrag.hydrateDispMsg2(bytesToText(byteArr, true));
                        hydrateFrag.hydrateDispMsg2(Float.toString(breathsPerMin));
                    }
                }
            });

       /* int prev = Integer.parseInt(samples.get(1)); // start at 1 since data at 0 is garbage
        int errors = 0;
        for (int x = 2; x < samples.size(); x++){
            try{
                int curr = Integer.parseInt(samples.get(x));
                if (Math.abs(prev - curr) != 1)
                    errors++;
                prev = curr;
            }
            catch(NumberFormatException e){ //catch numberFormatException
                e.printStackTrace();
                errors++;
            }
        }
        samples.clear();
        return errors;*/
    }

    public void clearData(){
        rData.clear();
        samples.clear();
    }
    //convert byte array to string
    private String bytesToText(byte[] bytes, boolean simplifyNewLine) {
        String text = new String(bytes, Charset.forName("UTF-8"));
        if (simplifyNewLine) {
            text = text.replaceAll("(\\r\\n|\\r)", "\n");
        }
        return text;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestLocationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can scan for Bluetooth peripherals");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }
}
