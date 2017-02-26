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
    View rootView;
    Spinner vitalSpinner, timeSpinner;

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.g_fragment_config, container, false);

        scanBtn = (Button) rootView.findViewById(R.id.scanBtn);

        // create vital spinner
        vitalSpinner = (Spinner) rootView.findViewById(R.id.vitalSpinner);      //must call getView (returns root view) if inside fragment
         // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> vitalAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.vitals_arr, android.R.layout.simple_spinner_item);
         // Specify the layout to use when the list of choices appears
        vitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         // Apply the adapter to the spinner
        vitalSpinner.setAdapter(vitalAdapter);
        // Inflate the layout for this fragment

        // create time spinner
        timeSpinner = (Spinner) rootView.findViewById(R.id.timeSpinner);      //must call getView (returns root view) if inside fragment
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.time_arr, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        timeSpinner.setAdapter(timeAdapter);
        // Inflate the layout for this fragment

        return rootView;
    }

    public void setScanBtn(String text) {
        scanBtn.setText(text);
    }

}
