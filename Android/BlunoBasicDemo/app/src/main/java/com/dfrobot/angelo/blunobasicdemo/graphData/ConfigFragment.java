package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.dfrobot.angelo.blunobasicdemo.R;

/**
 * Created by Andrew on 2/19/2017.
 */

public class ConfigFragment extends Fragment{
    Button scanBtn;
    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //scanBtn = (Button) findViewById(R.id.scanBtn);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.g_fragment_config, container, false);
    }

    public void setScanBtn(String text){
        scanBtn.setText(text);
    }

}
