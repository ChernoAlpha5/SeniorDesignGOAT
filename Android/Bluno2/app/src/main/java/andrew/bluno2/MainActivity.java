package andrew.bluno2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import andrew.bluno2.Graph.*;
import andrew.bluno2.BluetoothLeService;

public class MainActivity extends BlunoLibrary /*AppCompatActivity*/ {
    private RespirationFrag respFrag;
    private RespirationFrag hydrFrag;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private EditText editText;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        editText = (EditText) findViewById(R.id.msgBox);
        title = (TextView) findViewById(R.id.title);
        onCreateProcess();						    //onCreate Process by BlunoLibrary
        serialBegin(115200);						//set the Uart Baudrate on BLE chip to 115200
        requestLocationPermissionIfNeeded();        //request permission from user to enable location access. Required on newer versions of Android

    }

    /** For debugging - Called when the user clicks the Send button (See activity_main.xml)*/
    public void launchActivity(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        /*Bundle bundle = new Bundle();
        bundle.putSerializable("g_fragment_manager", mSectionsPagerAdapter);
        intent.putExtras(bundle);*/
        //EditText editText = (EditText) findViewById(R.id.msgBox);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** For debugging - Called when the user clicks the Send button (See activity_main.xml)*/
    public void sendBtnMsg(View view) {
        //Intent intent = new Intent(this, GraphActivity.class);
        editText = (EditText) findViewById(R.id.msgBox);
        String message = editText.getText().toString();
        //TODO: ACCESS FRAGMENT AND UPDATE DATA
        //SectionsPagerAdapter adapter = (SectionsPagerAdapter)getContext();
        //sendMessage(0, message);
        //gActivity.sendMessage();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }

    // scans for BT devices
    public void scanDevices(View view){
        buttonScanOnClickProcess();     // Method from BlunoLibrary
    }

    public void sendMessage(int position, String data) {
        // do work on the referenced Fragments, but first check if they
        // even exist yet, otherwise you'll get an NPE.
        respFrag = (RespirationFrag) getSupportFragmentManager().findFragmentById(R.id.respFragment);
        //hydrFrag = (RespirationFrag) getSupportFragmentManager().findFragmentById();      //TODO: KEEP THIS!
        //respFrag = (RespirationFrag)mSectionsPagerAdapter.getItem(0);
        //hydrFrag = (RespirationFrag)mSectionsPagerAdapter.getItem(1);
        if (respFrag!= null && position == 0 ){
            respFrag.setData(data);
        }
        if (hydrFrag!= null && position == 1 ){
            hydrFrag.setData(data);
        }
    }

    // below functions are from Bluno basic demo
    @Override
    public void onSerialReceived(final String theString) {	//Once connection data received, this function will be called
        //do nothing FML
        title.setText(theString);       //TODO: REMOVE, ONLY FOR DEBUGGING
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{        //TODO: kind of janky. Correct this?
            onPauseProcess();
        }
        catch(Exception e) {//onPause Process by BlunoLibrary
        }
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
                title.setText("Connected");
                break;
            case isConnecting:
                title.setText("Connecting");
                break;
            case isToScan:
                title.setText("Scan");
                break;
            case isScanning:
                title.setText("Scanning");
                break;
            case isDisconnecting:
                title.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    // method copied from Adafruit Bluefruit App. Used to request permission from user for location access for Android M or above.
    // App won't scan for BT devieces if this is not called
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
