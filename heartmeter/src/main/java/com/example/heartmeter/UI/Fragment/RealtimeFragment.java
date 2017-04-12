package com.example.heartmeter.UI.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.heartmeter.R;
import com.example.heartmeter.Service.BTService;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RealtimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealtimeFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private LocalBroadcastManager mLocalBroadcastManager;
    private String[] modes;
    private String[] hints;
    private Spinner mModeSpinner;
    private MonitorHostFragment mMonitorHost;
    private BTService mBTService;

    private int currrent_mode_id = 0;
    private MonitorPanelFragment mCurrentPanel;


    public RealtimeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static RealtimeFragment newInstance() {
        RealtimeFragment fragment = new RealtimeFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //mLocalBroadcastManager.registerReceiver();
        modes = getResources().getStringArray(R.array.realtime_modes);
        hints = getResources().getStringArray(R.array.realtime_mode_hints);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
        mMonitorHost = MonitorHostFragment.newInstance();
        mMonitorHost.setPanelViewCreatedListener(this);
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.replace(R.id.monitorHost, mMonitorHost);
        trans.commit();

        mModeSpinner = (Spinner) rootView.findViewById(R.id.modeSpinner);
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                currrent_mode_id = pos;
                switch (pos) {
                    case 0:
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_VALUE);
                        break;
                    case 1:
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_VALUE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mModeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, modes));
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.setTitle(modes[currrent_mode_id]);
        mCurrentPanel.setHint(hints[currrent_mode_id]);
        switch (currrent_mode_id) {
            case 0:
                break;
            case 1:
                break;
        }
    }
}
