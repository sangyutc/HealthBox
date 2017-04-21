package com.example.heartmeter.UI.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.heartmeter.Service.HubService;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;
import com.presisco.shared.ui.framework.monitor.StringPanelFragment;
import com.presisco.shared.utils.ByteUtils;

import lecho.lib.hellocharts.util.ChartUtils;

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
    private BroadcastReceiver mCurrentReceiver;

    private int current_mode_id = 0;
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

        mModeSpinner = (Spinner) rootView.findViewById(R.id.spinnerMode);
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                current_mode_id = pos;
                String action = "";
                mLocalBroadcastManager.unregisterReceiver(mCurrentReceiver);
                switch (pos) {
                    case 0:
                    case 3:
                        action = HubService.ACTION_HEART_RATE_REDUCED;
                        mCurrentReceiver = new HeartRateReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_STRING);
                        break;
                    case 1:
                        action = HubService.ACTION_HEART_RATE_VOLUME;
                        mCurrentReceiver = new HeartVolumeReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_LINE);
                        break;
                    case 2:
                        action = HubService.ACTION_ECG;
                        mCurrentReceiver = new ECGReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_LINE);
                        break;
                }
                mLocalBroadcastManager.registerReceiver(mCurrentReceiver, new IntentFilter(action));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mModeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, modes));

        rootView.findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBroadcastManager.sendBroadcast(
                        new Intent(HubService.ACTION_SEND_INSTRUCTION)
                                .putExtra(HubService.KEY_INSTRUCTION, HubService.SEND_START));
                mCurrentPanel.clear();
            }
        });
        rootView.findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBroadcastManager.sendBroadcast(
                        new Intent(HubService.ACTION_SEND_INSTRUCTION)
                                .putExtra(HubService.KEY_INSTRUCTION, HubService.SEND_STOP));
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentPanel.setTitle(modes[current_mode_id]);
        mCurrentPanel.setHint(hints[current_mode_id]);
        switch (current_mode_id) {
            case 1:
                LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
                linePanel.setAxisYScale(0, 250);
                linePanel.setMaxPoints(60);
                linePanel.setXStep(1);
                linePanel.setAxisXText("Time");
                linePanel.setAxisYText("Rate");
                LinePanelFragment.LineStyle style = new LinePanelFragment.LineStyle();
                style.line_color = ChartUtils.COLOR_BLUE;
                style.has_points = false;
                linePanel.setLineStyle(style);
                linePanel.redraw();
                break;
            case 2:
                linePanel = (LinePanelFragment) mCurrentPanel;
                linePanel.setAxisYScale(400, 600);
                linePanel.setMaxPoints(500);
                linePanel.setXStep(0.01f);
                linePanel.setAxisXText("Time");
                linePanel.setAxisYText("Voltage");
                style = new LinePanelFragment.LineStyle();
                style.line_color = ChartUtils.COLOR_BLUE;
                style.has_points = false;
                linePanel.setLineStyle(style);
                linePanel.redraw();
                break;
        }
    }

    private class HeartRateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (current_mode_id) {
                case 0:
                case 3:
                    ((StringPanelFragment) mCurrentPanel).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    break;
            }
        }
    }

    private class HeartVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
            switch (current_mode_id) {
                case 1:
                    int[] heart_rate_data = intent.getIntArrayExtra(HubService.KEY_DATA);
                    float[] converted = ByteUtils.intArray2floatArray(heart_rate_data);
                    linePanel.appendValue(converted);
                    break;
            }
        }
    }

    private class ECGReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
            switch (current_mode_id) {
                case 2:
                    int[] ecg_data = intent.getIntArrayExtra(HubService.KEY_DATA);
                    float[] converted = ByteUtils.intArray2floatArray(ecg_data);
                    linePanel.appendValue(converted);
                    break;
            }
        }
    }
}
