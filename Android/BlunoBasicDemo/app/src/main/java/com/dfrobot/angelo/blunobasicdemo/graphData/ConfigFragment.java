package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.dfrobot.angelo.blunobasicdemo.R;

/**
 * Created by Andrew on 2/19/2017.
 */

public class ConfigFragment extends Fragment {
    Button scanBtn;

    public ConfigFragment() {
        // Required empty public constructor
    }

    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //scanBtn = (Button) findViewById(R.id.scanBtn);

        spinner = (Spinner) getView().findViewById(R.id.vitalSpinner);      //must call getView (returns root view) if inside fragment
         // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.vitals_arr, android.R.layout.simple_spinner_item);
         // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.g_fragment_config, container, false);
    }

    public void setScanBtn(String text) {
        scanBtn.setText(text);
    }

}
