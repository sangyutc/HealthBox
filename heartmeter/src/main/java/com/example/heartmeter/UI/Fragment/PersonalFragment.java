package com.example.heartmeter.UI.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartmeter.Service.BTService;
import com.example.heartmeter.UI.Activity.BTBoxActivity;
import com.example.heartmeter.UI.Activity.DBDebugActivity;
import com.example.heartmeter.UI.Activity.SurveyActivity;
import com.example.heartmeter.constant.Constant;
import com.presisco.shared.ui.fragment.BasePersonalFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends BasePersonalFragment implements BasePersonalFragment.ActionListener {
    private static final int REQUEST_ID_SIGN_UP = 1;

    private boolean isLoggedIn = false;
    private String username = "";

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
        if (!isLoggedIn) {
            SharedPreferences preferences = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, 0);
            isLoggedIn = preferences.getBoolean(Constant.SHARED_PREF_KEY_IS_LOGGED_IN, false);
        }
        setChildListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (isLoggedIn) {
            showUsername();
            setUsername(Constant.SHARED_PREF_KEY_USERNAME);
        } else {
            showLogin();
        }
        return rootView;
    }

    @Override
    public void onLogin() {

    }

    @Override
    public void onSignUp() {
        startActivityForResult(new Intent(getActivity(), SurveyActivity.class), REQUEST_ID_SIGN_UP);
    }

    @Override
    public void onBTBox() {
        startActivity(new Intent(getActivity(), BTBoxActivity.class));
    }

    @Override
    public void onInstantUpload() {

    }

    @Override
    public void onBTReconnect() {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BTService.ACTION_TARGET_CONNECT));
    }

    @Override
    public void onDBDebug() {
        startActivity(new Intent(getActivity(), DBDebugActivity.class));
    }
}
