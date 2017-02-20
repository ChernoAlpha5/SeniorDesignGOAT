package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.content.Intent;
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
                scanBtn.setText("Connected");
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
    public void onSerialReceived(final String theString) {               //Once connection data received, this function will be called
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

    }

    private String bytesToText(byte[] bytes, boolean simplifyNewLine) {
        String text = new String(bytes, Charset.forName("UTF-8"));
        if (simplifyNewLine) {
            text = text.replaceAll("(\\r\\n|\\r)", "\n");
        }
        return text;
    }
}
