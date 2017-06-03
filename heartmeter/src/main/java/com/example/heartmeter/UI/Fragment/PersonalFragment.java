package com.example.heartmeter.UI.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartmeter.Service.BTService;
import com.example.heartmeter.UI.Activity.MonitorPreferenceActivity;
import com.example.heartmeter.UI.Activity.SignInActivity;
import com.example.heartmeter.UI.Activity.SurveyActivity;
import com.example.heartmeter.debug.BTBoxActivity;
import com.example.heartmeter.debug.DBDebugActivity;
import com.example.heartmeter.debug.MonitorDebugActivity;
import com.example.heartmeter.debug.NetTaskDebugActivity;
import com.presisco.shared.ui.fragment.BasePersonalFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends BasePersonalFragment implements BasePersonalFragment.ActionListener {
    private static final int REQUEST_ID_SIGN_UP = 1;
    private static final int REQUEST_ID_LOGIN = 2;

    private SharedPreferences mPreferences;

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
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setChildListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        String username = mPreferences.getString("username", "");
        if (username != "") {
            showUsername();
            setUsername(username);
        } else {
            showLogin();
        }
        return rootView;
    }

    @Override
    public void onSignIn() {
        startActivityForResult(new Intent(getActivity(), SignInActivity.class), REQUEST_ID_LOGIN);
    }

    @Override
    public void onSignUp() {
        startActivityForResult(new Intent(getActivity(), SurveyActivity.class), REQUEST_ID_SIGN_UP);
    }

    @Override
    public void onSignOut() {
        mPreferences.edit().putString("username", "").commit();
        showLogin();
    }

    @Override
    public void onBTBox() {
        startActivity(new Intent(getActivity(), BTBoxActivity.class));
    }

    @Override
    public void onInstantUpload() {
        startActivity(new Intent(getActivity(), NetTaskDebugActivity.class));
    }

    @Override
    public void onBTReconnect() {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BTService.ACTION_TARGET_CONNECT));
    }

    @Override
    public void onDBDebug() {
        startActivity(new Intent(getActivity(), DBDebugActivity.class));
    }

    @Override
    public void onMonitorSetting() {
        startActivity(new Intent(getActivity(), MonitorPreferenceActivity.class));
    }

    @Override
    public void onMonitorDebug() {
        startActivity(new Intent(getActivity(), MonitorDebugActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ID_LOGIN:
            case REQUEST_ID_SIGN_UP:
                if (resultCode == SurveyActivity.RESULT_PASSED) {
                    String username = data.getStringExtra("username");
                    Integer age = data.getIntExtra("age", 30);
                    mPreferences.edit()
                            .putString("username", username)
                            .putInt("age", age).commit();
                    setUsername(username);
                    showUsername();
                }
                break;
        }
    }
}
