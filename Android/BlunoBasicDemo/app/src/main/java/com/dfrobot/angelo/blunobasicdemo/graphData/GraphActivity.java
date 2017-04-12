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
import android.support.v7.widget.Toolbar;
import com.dfrobot.angelo.blunobasicdemo.BlunoLibrary;
import com.dfrobot.angelo.blunobasicdemo.Filter.filterData;
import com.dfrobot.angelo.blunobasicdemo.R;
import com.github.mikephil.charting.data.Entry;
import android.view.View;

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
    boolean badData = false;
     //elements should be in the order that the fragments are added
    public enum fragT {FRAG_RESP, FRAG_CONF, FRAG_HYDR}

    // hydration plot variables
    ArrayList<String> hXAXES = new ArrayList<String>();
    ArrayList<Entry> hYAXES = new ArrayList<Entry>();
    double startTime = 0;

    //data collection variables
    measType mType = null;
    int numSamples = 0;
    int maxSamples = 4000;
    int threshold = 1000; //minimum PPG data value. Anything lower than this is discarded
    ArrayList<Float> IRsamples = new ArrayList<Float>(maxSamples/2);
    ArrayList<Float> BPMdata = new ArrayList<Float>(maxSamples/2);
    ArrayList<Float> hydrSamples = new ArrayList<Float>(maxSamples/2);
    //ArrayList<Float> rData = new ArrayList<Float>(maxSamples/2);
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public enum measType {HYDRATION, RESPIRATION}

    //vars for debugging hydration fragment
    int[] fakeHydrVals = {10, 40, 85, 100, 4};
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         //restore state of config fragment if it exists
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            configFrag = (ConfigFragment)getSupportFragmentManager().getFragment(savedInstanceState, "savedConfigFragment");
        }
        else{
            configFrag = new ConfigFragment();
        }

        onCreateProcess();						//onCreate Process by BlunoLibrary
        serialBegin(115200);					//set the Uart Baudrate on BLE chip to 115200

        setContentView(R.layout.activity_graph);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.g_viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //configFrag = new ConfigFragment();
        hydrateFrag = new HydrationFragment();
        respirationFrag = new RespirationFragment();

    /*    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.g_viewPager, configFrag);
        fragmentTransaction.add(R.id.g_viewPager, hydrateFrag);
        fragmentTransaction.add(R.id.g_viewPager, respirationFrag);
        fragmentTransaction.commit();*/


        //scanBtn = (Button) findViewById(R.id.scanBtn);
        viewPagerAdapter.addFragments(respirationFrag, "Respiration");
        viewPagerAdapter.addFragments(configFrag, "Configure");
        viewPagerAdapter.addFragments(hydrateFrag, "Hydration");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(fragT.FRAG_CONF.ordinal());
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         //Save the config fragment's instance
        if (configFrag != null)
            getSupportFragmentManager().putFragment(outState, "savedConfigFragment", configFrag);
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        String connectionState = null;
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                connectionState = "Measure";
                configFrag.setProgMsg("Tap Measure to Proceed");
                break;
            case isConnecting:
                connectionState = "Connecting";
                break;
            case isToScan:
                connectionState = "Scan";
                break;
            case isScanning:
                connectionState = "Scanning";
                break;
            case isDisconnecting:
                connectionState = "isDisconnecting";
                break;
            default:
                break;
        }
        if (connectionState != null){
            configFrag = (ConfigFragment) viewPagerAdapter.getFragment(1);
            if (configFrag !=null){
                configFrag.setScanBtn(connectionState);
            }

        }
    }
    public void scanDevices(View v){
        buttonScanOnClickProcess();

    }

    public void setMeasType(measType m){mType = m;}

    public measType getMeasType(){return mType;}

    //returns the most recent BPM in BPMdata array
    public float getBPM(){
        if (BPMdata.size() > 0){
           return BPMdata.get(BPMdata.size() - 1);
        }
        return 0;
    }

     //returns the most recent IR data in IR array
    public float getIR(){
        if (IRsamples.size() > 0){
            return IRsamples.get(IRsamples.size() - 1);
        }
        return 0;
    }

    //returns the most recent hydration data in hydr array
    public float getHydr(){
        if (hydrSamples.size() > 0){
            return hydrSamples.get(hydrSamples.size() - 1);
        }
        return 0;
    }

    public boolean dataBad(){
        return badData;
    }

    @Override
    public void onSerialReceived(final String theString) {//Once connection data received, this function will be called
        if (IRsamples.size() <  maxSamples ){
            String data = theString.replaceAll("\\r\\n", "");
            if (mType == measType.RESPIRATION){
                try {
                    String[] splitDat = data.split(" "); //string format: "BPM rawIRdata"
                    if (splitDat.length == 2){
                        Float BPM = Float.parseFloat(splitDat[0]);
                        Float IRdata = Float.parseFloat(splitDat[1]);
                        //check if values are in correct range
                        if (BPM > 20 && BPM < 220)
                            BPMdata.add(BPM);
                        if (IRdata > 1000 && IRdata < 1000000)
                            IRsamples.add(IRdata);
                        badData = (IRdata < 1000 || IRdata > 1000000);
                    }
                }
                catch(NumberFormatException e){  //if the number is not a valid float
                    //e.printStackTrace();
                }
            }
            if (mType == measType.HYDRATION){
                //// TODO: 3/21/2017 implement hydration onSerialReceive
                try{
                     //convert ADC value (Uno has 10 bit resolution, so value between 0 - 1023) to voltage between 0 - 5V
                    float ADCval = Float.parseFloat(data);
                    ADCval *= (5.0 / 1023.0);
                    hydrSamples.add(ADCval);
                }
                catch(NumberFormatException e) {
                    e.printStackTrace();
                }

            }

        }
    }
    //send the data to the BLUNO
    public void sendToBluno(String text){serialSend(text);}

    public connectionStateEnum getConnectionState(){
        return mConnectionState;
    }

    public void graphData(int seconds){

        if (mType == measType.RESPIRATION){
            /*
            //format data to remove \\r\\n characters and discard any data less than <threshold>
            for (int i = 1; i < IRsamples.size(); i++){  //ensure no newline characters in string
                String s = IRsamples.get(i);
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
                                rData.add(*//*currInd,*//* f);
                                currInd++;
                            }
                        }
                        i = currInd -1; //update index after inserting data
                    }
                    else{
                        Float f = Float.parseFloat(IRsamples.get(i).replaceAll("\\r\\n", ""));
                        if (f >= threshold){
                            rData.add(f);
                        }
                    }
                }
                catch(NumberFormatException e){e.printStackTrace();}    //in case the number is not a valid float
            }*/
            filterData fData = new filterData();
            int breaths = 0;
            if (IRsamples.size() > 0)
                breaths = fData.calculateRespRate(IRsamples);

            clearData();

            final float breathsPerMin = breaths/((float)seconds/60);
            System.out.println(breathsPerMin);     //calculate breaths per minute

            //graph filtered result on respiration graph
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    respirationFrag = (RespirationFragment) viewPagerAdapter.getFragment(fragT.FRAG_RESP.ordinal());  //respiration fragment is at position 1 in hashmap
                    if (respirationFrag != null) {
                        respirationFrag.respDispMsg(Float.toString(breathsPerMin));
                    }
                    /*hydrateFrag = (HydrationFragment) viewPagerAdapter.getFragment(2);  //hydration fragment is at position 2 in hashmap
                    if (hydrateFrag != null) {
                        hydrateFrag.hydrateDispMsg("42");
                    }*/
                }
            });
            if (viewPager != null)
                viewPager.setCurrentItem(fragT.FRAG_RESP.ordinal());

        }
        if (mType == measType.HYDRATION){ //TODO: IMPLEMENT RESPIRATION PROCESSING
            //graph filtered result on hydration graph
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    hydrateFrag = (HydrationFragment) viewPagerAdapter.getFragment(fragT.FRAG_HYDR.ordinal()); //is this needed?
                    if (hydrateFrag != null){
                        //average values in hydration sample array
                        float sum = 0;
                        if(!hydrSamples.isEmpty()) {
                            for (float f : hydrSamples) {
                                sum += f;
                            }
                            hydrateFrag.hydrateDispMsg(sum/hydrSamples.size() + "");
                        }
                        else
                            hydrateFrag.hydrateDispMsg("0");
                        /*//TODO: CURRENTLY DISPLAYS FAKE VALUES. CHANGE THIS!!!
                        hydrateFrag.hydrateDispMsg(fakeHydrVals[i] + "");
                        i = (i+1) % fakeHydrVals.length;*/
                    }
                }
            });
            if (viewPager != null)
                viewPager.setCurrentItem(fragT.FRAG_HYDR.ordinal());


        }
    }

    public void clearData(){
        //rData.clear();
        BPMdata.clear();
        IRsamples.clear();
        hydrSamples.clear();
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
