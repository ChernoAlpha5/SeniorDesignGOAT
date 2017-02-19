package com.dfrobot.angelo.blunobasicdemo;

//import android.content.Context;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.dfrobot.angelo.blunobasicdemo.graphData.*;
import com.dfrobot.angelo.blunobasicdemo.graphData.GraphActivity;

public class MainActivity  extends BlunoLibrary {
	private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
	private Button buttonScan;
	private Button buttonSerialSend;
	private Button buttonLaunch;		//launch graph activity
	private EditText serialSendText;
	private TextView serialReceivedText;

	//GRAPH ACTIVITY VARIABLES
	/*Toolbar toolbar;
	TabLayout tabLayout;
	ViewPager viewPager;
	ViewPagerAdapter pagerAdapter; */
	HydrationFragment hydrateFrag;
	GraphHelper graphHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        final Intent intent = new Intent(MainActivity.this,GraphActivity.class);
		//intent.putExtra("score_key", score);
		startActivity(intent);
		graphHelp = new GraphHelper();
		//graphActivity = (GraphActivity)findViewById(R.id.g_activity);	//TODO: IMPORTANT!!!
		//toolbar = (Toolbar)findViewById(R.id.toolBar);
		/*tabLayout = (TabLayout)findViewById(R.id.tabLayout);
		viewPager = (ViewPager)findViewById(R.id.g_viewPager);*/
		//pagerAdapter = (ViewPagerAdapter)findViewById(R.id.g_viewPager);

		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary

        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

		//launch graph activity
		buttonLaunch = (Button) findViewById(R.id.buttonLaunch);		//initial the button for sending the data
		buttonLaunch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//setContentView(R.layout.g_activity_graph);
				//startActivity(intent);

			}
		});
		requestLocationPermissionIfNeeded();
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
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(final String theString) {							//Once connection data received, this function will be called
		hydrateFrag = (HydrationFragment)getFragmentManager().findFragmentByTag("hFrag");
		graphHelp.updateGraph(theString);
		serialReceivedText.append(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
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