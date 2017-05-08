package com.presisco.shared.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.presisco.shared.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseRealTimeFragment extends Fragment {


    public BaseRealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_real_time, container, false);
    }

}
