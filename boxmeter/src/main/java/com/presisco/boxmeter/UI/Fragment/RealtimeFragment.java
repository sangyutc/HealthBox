package com.presisco.boxmeter.UI.Fragment;

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

import com.presisco.boxmeter.R;
import com.presisco.boxmeter.Service.HubService;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;
import com.presisco.shared.ui.framework.monitor.StringPanelFragment;
import com.presisco.shared.utils.ByteUtils;
import com.presisco.shared.utils.LCAT;

import lecho.lib.hellocharts.util.ChartUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RealtimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealtimeFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private static final int MODE_DEFAULT_SPO2H = 0;
    private static final int MODE_DEFAULT_PULSE = 1;
    private static final int MODE_DEFAULT_SPO2H_MINUTE = 2;
    private static final int MODE_DEFAULT_PULSE_FIVE_SEC = 3;
    private static final int MODE_AEROBIC = 4;

    private int indicator = 0;

    private LocalBroadcastManager mLocalBroadcastManager;
    private String[] modes;
    private String[] hints;
    private Spinner mModeSpinner;
    private MonitorHostFragment mMonitorHost;

    private int currrent_mode_id = 0;
    private MonitorPanelFragment mCurrentPanel;
    private BroadcastReceiver mCurrentReceiver;

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
        LCAT.d(this, "created");
        LCAT.d(this, "indicator: " + indicator);
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

        mModeSpinner = (Spinner) rootView.findViewById(R.id.modeSpinner);
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                currrent_mode_id = pos;
                mLocalBroadcastManager.unregisterReceiver(mCurrentReceiver);
                String action = "";
                switch (pos) {
                    case MODE_DEFAULT_SPO2H:
                    case MODE_AEROBIC:
                        action = HubService.ACTION_SPO2H;
                        mCurrentReceiver = new SPO2HReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_STRING);
                        break;
                    case MODE_DEFAULT_PULSE:
                        action = HubService.ACTION_PULSE;
                        mCurrentReceiver = new PulseReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_STRING);
                        break;
                    case MODE_DEFAULT_SPO2H_MINUTE:
                        action = HubService.ACTION_SPO2H_VOLUME;
                        mCurrentReceiver = new SPO2HVolumeReceiver();
                        mMonitorHost.displayPanel(MonitorHostFragment.PANEL_LINE);
                        break;
                    case MODE_DEFAULT_PULSE_FIVE_SEC:
                        action = HubService.ACTION_PULSE_VOLUME;
                        mCurrentReceiver = new PulseVolumeReceiver();
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
                                .putExtra(HubService.KEY_DATA, HubService.SEND_START));
                mCurrentPanel.clear();
            }
        });
        rootView.findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBroadcastManager.sendBroadcast(
                        new Intent(HubService.ACTION_SEND_INSTRUCTION)
                                .putExtra(HubService.KEY_DATA, HubService.SEND_STOP));
            }
        });

        return rootView;
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentPanel.setTitle(modes[currrent_mode_id]);
        mCurrentPanel.setHint(hints[currrent_mode_id]);
        switch (currrent_mode_id) {
            case MODE_DEFAULT_SPO2H_MINUTE:
                LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
                linePanel.setAxisYScale(0, 110);
                linePanel.setMaxPoints(60);
                linePanel.setXStep(1f);
                linePanel.setAxisXText("Time");
                linePanel.setAxisYText("SPO2H");
                LinePanelFragment.LineStyle style = new LinePanelFragment.LineStyle();
                style.line_color = ChartUtils.COLOR_BLUE;
                style.has_points = false;
                linePanel.setLineStyle(style);
                linePanel.redraw();
                break;
            case MODE_DEFAULT_PULSE_FIVE_SEC:
                linePanel = (LinePanelFragment) mCurrentPanel;
                linePanel.setAxisYScale(0, 20);
                linePanel.setMaxPoints(500);
                linePanel.setXStep(0.01f);
                linePanel.setAxisXText("Time");
                linePanel.setAxisYText("Pulse");
                style = new LinePanelFragment.LineStyle();
                style.line_color = ChartUtils.COLOR_BLUE;
                style.has_points = false;
                linePanel.setLineStyle(style);
                linePanel.redraw();
                break;
        }
    }

    private class SPO2HReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (currrent_mode_id) {
                case MODE_DEFAULT_SPO2H:
                case MODE_AEROBIC:
                    ((StringPanelFragment) mCurrentPanel).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    break;
            }
        }
    }

    private class PulseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (currrent_mode_id) {
                case MODE_DEFAULT_PULSE:
                    ((StringPanelFragment) mCurrentPanel).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    break;
            }
        }
    }

    private class SPO2HVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
            switch (currrent_mode_id) {
                case MODE_DEFAULT_SPO2H_MINUTE:
                    int data = intent.getIntExtra(HubService.KEY_DATA, 25);
                    linePanel.appendValue(data);
                    break;
            }
        }
    }

    private class PulseVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinePanelFragment linePanel = (LinePanelFragment) mCurrentPanel;
            switch (currrent_mode_id) {
                case MODE_DEFAULT_PULSE_FIVE_SEC:
                    int[] data = intent.getIntArrayExtra(HubService.KEY_DATA);
                    float[] converted = ByteUtils.intArray2floatArray(data);
                    linePanel.appendValue(converted);
                    break;
            }
        }
    }

}
