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
    int maxSamples = 2000;
    ArrayList<String> samples = new ArrayList<String>(2000);

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
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                scanBtn.setText("Connected");   //TODO: FIX BUTTON CHANGES
                break;
            case isConnecting:
                scanBtn.setText("Connecting");
                break;
            case isToScan:
                scanBtn.setText("Scan");
                break;
            case isScanning:
                scanBtn.setText("Scanning");
                break;
            case isDisconnecting:
                scanBtn.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }
    public void scanDevices(View v){
        buttonScanOnClickProcess();
    }

    @Override
    public void onSerialReceived(final String theString) {//Once connection data received, this function will be called

       /* if (numSamples <  maxSamples ){
            samples.add(theString);
        }
        else{
            //processData(samples); */
            runOnUiThread(new Runnable() {
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
            });

        //}

    }
    //TODO: call filtereing method in this function
    private void processData(ArrayList<String> data){
        numSamples = 0;
        for (String s: data){
            s.replaceAll("(\\r\\n|\\r)", "\n");
            byte[] byteArr = s.getBytes();
            bytesToText(byteArr, true);
        }
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
