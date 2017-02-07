package com.dfrobot.angelo.blunobasicdemo.graphData;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfrobot.angelo.blunobasicdemo.R;

//import com.adafruit.bluefruit.le.connect.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RespirationFragment extends Fragment {


    public RespirationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.g_fragment_respiration, container, false);
    }

}
