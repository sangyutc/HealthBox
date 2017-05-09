package com.presisco.shared.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.presisco.shared.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BasePersonalFragment extends Fragment {
    private ActionListener mChildListener;
    private LinearLayout mUserLayout;
    private LinearLayout mLoginLayout;
    private TextView mUsernameText;

    public BasePersonalFragment() {
        // Required empty public constructor
    }

    public void setChildListener(ActionListener listener) {
        mChildListener = listener;
    }

    public void showUsername() {
        mLoginLayout.setVisibility(View.GONE);
        mUserLayout.setVisibility(View.VISIBLE);
    }

    public void showLogin() {
        mUserLayout.setVisibility(View.GONE);
        mLoginLayout.setVisibility(View.VISIBLE);
    }

    public void setUsername(String username) {
        mUsernameText.setText(username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_personal, container, false);
        mLoginLayout = (LinearLayout) rootView.findViewById(R.id.loginLayout);
        mUserLayout = (LinearLayout) rootView.findViewById(R.id.userLayout);
        mUsernameText = (TextView) rootView.findViewById(R.id.textUsername);
        rootView.findViewById(R.id.buttonBTToolbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onBTBox();
            }
        });
        rootView.findViewById(R.id.buttonBTReconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onBTReconnect();
            }
        });
        rootView.findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onSignUp();
            }
        });
        rootView.findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onLogin();
            }
        });
        rootView.findViewById(R.id.buttonInstantUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onInstantUpload();
            }
        });
        rootView.findViewById(R.id.buttonDBDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.onDBDebug();
            }
        });
        return rootView;
    }

    public interface ActionListener {
        void onLogin();

        void onSignUp();

        void onBTBox();

        void onInstantUpload();

        void onBTReconnect();

        void onDBDebug();
    }

}
