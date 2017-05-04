package com.example.heartmeter.UI.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartmeter.R;
import com.example.heartmeter.Service.BTService;
import com.example.heartmeter.UI.Activity.BTBoxActivity;
import com.example.heartmeter.UI.Activity.SurveyActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {
    private static final int REQUEST_ID_SIGN_UP = 1;

    public PersonalFragment() {
        // Required empty public constructor
    }

    public static PersonalFragment newInstance() {
        PersonalFragment fragment = new PersonalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        rootView.findViewById(R.id.buttonToolbox).setOnClickListener(new BTToolboxBtnListener());
        rootView.findViewById(R.id.buttonReconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BTService.ACTION_TARGET_CONNECT));
            }
        });
        rootView.findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), SurveyActivity.class), REQUEST_ID_SIGN_UP);
            }
        });
        return rootView;
    }

    private class BTToolboxBtnListener implements View.OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), BTBoxActivity.class));
        }
    }
}
