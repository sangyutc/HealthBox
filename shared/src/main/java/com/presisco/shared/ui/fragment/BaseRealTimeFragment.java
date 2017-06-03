package com.presisco.shared.ui.fragment;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.presisco.shared.ui.framework.mode.RealTime;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseRealTimeFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private ActionListener mChildListener;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Spinner mEventTypeSpinner;
    private Spinner mModeSpinner;
    private MonitorHostFragment mMonitorHost;
    private MonitorPanelFragment mCurrentPanel;
    private Button mStartButton;
    private Button mStopButton;
    private RealTime[] mRealTimeModes = null;
    private RealTime mCurrentMode = null;
    public BaseRealTimeFragment() {
        // Required empty public constructor
    }

    protected void setChildListener(ActionListener listener) {
        mChildListener = listener;
    }

    protected void setRealTimeModes(RealTime[] modes) {
        mRealTimeModes = modes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    protected View onCreateView(
            int layout_id,
            int type_spinner_id,
            int display_spinner_id,
            int monitor_host_id,
            int start_btn_id,
            int stop_btn_id,
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(layout_id, container, false);
        if (mMonitorHost == null) {
            mMonitorHost = MonitorHostFragment.newInstance();
        }
        mMonitorHost.setPanelViewCreatedListener(this);
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.replace(monitor_host_id, mMonitorHost);
        trans.commit();

        mEventTypeSpinner = (Spinner) rootView.findViewById(type_spinner_id);

        mModeSpinner = (Spinner) rootView.findViewById(display_spinner_id);
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mLocalBroadcastManager.unregisterReceiver(mCurrentMode);
                mCurrentMode = mRealTimeModes[pos];
                mMonitorHost.displayPanel(mCurrentMode.getPanelType());
                mLocalBroadcastManager.registerReceiver(
                        mCurrentMode, new IntentFilter(mCurrentMode.getBroadcastAction()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mStartButton = (Button) rootView.findViewById(start_btn_id);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventTypeSpinner.setEnabled(false);
                mChildListener.startEvent(mEventTypeSpinner.getSelectedItemPosition());
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
            }
        });

        mStopButton = (Button) rootView.findViewById(stop_btn_id);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventTypeSpinner.setEnabled(true);
                mChildListener.endEvent();
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
            }
        });
        mStopButton.setEnabled(false);
        return rootView;
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentMode.setPanel(mCurrentPanel);
        mCurrentMode.initPanelView();
    }

    protected LocalBroadcastManager getLocalBroadcastManager() {
        return mLocalBroadcastManager;
    }

    public interface ActionListener {
        void startEvent(int event_type_index);

        void endEvent();
    }

}
