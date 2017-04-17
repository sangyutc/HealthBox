package com.presisco.shared.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.presisco.shared.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AnalyseFragment extends Fragment {
    private Spinner mEventSpinner;

    public AnalyseFragment() {

    }

    public abstract AnalyseFragment newInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initModeSpinner(View rootView, String[] modes) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analyse, container, false);

        return rootView;
    }
}
