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
    //登陆
    public void onSignIn() {
        startActivityForResult(new Intent(getActivity(), SignInActivity.class), REQUEST_ID_LOGIN);
    }

    //注册
    @Override
    public void onSignUp() {
        startActivityForResult(new Intent(getActivity(), SurveyActivity.class), REQUEST_ID_SIGN_UP);
    }

    //注销
    @Override
    public void onSignOut() {
        mPreferences.edit().putString("username", "").commit();
        showLogin();
    }

    //蓝牙测试
    @Override
    public void onBTBox() {
        startActivity(new Intent(getActivity(), BTBoxActivity.class));
    }

    //上传数据到服务器
    @Override
    public void onInstantUpload() {
        startActivity(new Intent(getActivity(), NetTaskDebugActivity.class));
    }

    //蓝牙链接
    @Override
    public void onBTReconnect() {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BTService.ACTION_TARGET_CONNECT));
    }

    //模拟数据测试
    @Override
    public void onDBDebug() {
        startActivity(new Intent(getActivity(), DBDebugActivity.class));
    }

    //监护设置
    @Override
    public void onMonitorSetting() {
        startActivity(new Intent(getActivity(), MonitorPreferenceActivity.class));
    }

    //测试监护
    @Override
    public void onMonitorDebug() {
        startActivity(new Intent(getActivity(), MonitorDebugActivity.class));
    }

    //结果信息反馈
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
