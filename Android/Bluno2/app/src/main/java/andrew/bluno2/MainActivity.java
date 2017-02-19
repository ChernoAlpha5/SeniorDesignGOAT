package andrew.bluno2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import andrew.bluno2.Graph.*;
import andrew.bluno2.BluetoothLeService;

public class MainActivity extends AppCompatActivity {
    private RespirationFrag respFrag;
    private RespirationFrag hydrFrag;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //private GraphActivity gActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //gActivity = new GraphActivity();
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
        EditText editText = (EditText) findViewById(R.id.msgBox);
        String message = editText.getText().toString();
        //TODO: ACCESS FRAGMENT AND UPDATE DATA
        //SectionsPagerAdapter adapter = (SectionsPagerAdapter)getContext();
        //sendMessage(0, message);
        //gActivity.sendMessage();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }

    public void scanDevices(View view){
        //buttonScanOnClickProcess();
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



}
