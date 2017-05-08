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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.presisco.shared.R;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;
import com.presisco.shared.ui.framework.realtime.RealTimeMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseRealTimeFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private ActionListener mChildListener;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Spinner mModeSpinner;
    private MonitorHostFragment mMonitorHost;
    private MonitorPanelFragment mCurrentPanel;
    private Button mStartButton;
    private Button mStopButton;
    private RealTimeMode[] mRealTimeModes = null;
    private RealTimeMode mCurrentMode = null;
    public BaseRealTimeFragment() {
        // Required empty public constructor
    }

    protected void setChildListener(ActionListener listener) {
        mChildListener = listener;
    }

    protected void setRealTimeModes(RealTimeMode[] modes) {
        mRealTimeModes = modes;
    }

    private String[] getTitles() {
        String[] titles = new String[mRealTimeModes.length];
        for (int i = 0; i < mRealTimeModes.length; ++i) {
            titles[i] = mRealTimeModes[i].getModeTitle();
        }
        return titles;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_base_real_time, container, false);
        if (mMonitorHost == null) {
            mMonitorHost = MonitorHostFragment.newInstance();
        }
        mMonitorHost.setPanelViewCreatedListener(this);
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.replace(R.id.monitorHost, mMonitorHost);
        trans.commit();

        mModeSpinner = (Spinner) rootView.findViewById(R.id.spinnerMode);
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
        mModeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getTitles()));

        mStartButton = (Button) rootView.findViewById(R.id.buttonStart);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.startEvent(mCurrentMode);
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
            }
        });
        mStopButton = (Button) rootView.findViewById(R.id.buttonStop);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildListener.endEvent(mCurrentMode);
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
            }
        });
        return rootView;
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentMode.setPanel(mCurrentPanel);
        mCurrentMode.initPanelView();
    }

    protected LocalBroadcastManager getLocalBroadcastmanager() {
        return mLocalBroadcastManager;
    }

    public interface ActionListener {
        void startEvent(RealTimeMode mode);

        void endEvent(RealTimeMode mode);
    }
}
