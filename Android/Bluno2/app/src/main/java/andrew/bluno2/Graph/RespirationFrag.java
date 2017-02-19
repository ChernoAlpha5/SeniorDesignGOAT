package andrew.bluno2.Graph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import andrew.bluno2.R;

/**
 * Created by Andrew on 2/18/2017.
 */

public class RespirationFrag extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView textView;

    public RespirationFrag() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(int sectionNumber) {
        RespirationFrag fragment = new RespirationFrag();
       /* Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_respiration, container, false);
        textView = (TextView) rootView.findViewById(R.id.msgSent);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        textView.setText("Respiration");
        return rootView;
    }

    public void setData(String data){
        //TextView textView = (TextView)getView().findViewById(R.id.msgSent);
        if (textView != null){
            textView.setText(data);
        }

    }
}
