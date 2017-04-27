package com.example.heartmeter.UI.Fragment;

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
import android.widget.Button;
import android.widget.Spinner;

import com.example.heartmeter.Data.Event;
import com.example.heartmeter.R;
import com.example.heartmeter.Service.HubService;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;
import com.presisco.shared.ui.framework.monitor.StringPanelFragment;
import com.presisco.shared.ui.framework.realtime.RealTimeMode;
import com.presisco.shared.utils.ByteUtils;

import lecho.lib.hellocharts.util.ChartUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RealtimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealtimeFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {

    private static final int MODE_DEFAULT_HEART_RATE = 0;
    private static final int MODE_DEFAULT_HEART_RATE_MINUTE = 1;
    private static final int MODE_DEFAULT_ECG_FIVE_SEC = 2;
    private static final int MODE_AEROBIC = 3;
    private static final int MODE_ANAEROBIC = 4;
    private static final int MODE_SLEEP = 5;

    private LocalBroadcastManager mLocalBroadcastManager;
    private Spinner mModeSpinner;
    private MonitorHostFragment mMonitorHost;

    private MonitorPanelFragment mCurrentPanel;

    private Button mStartButton;
    private Button mStopButton;

    private RealTimeMode[] mRealTimeModes = null;
    private RealTimeMode mCurrentMode = null;

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

    private String[] getTitles() {
        String[] titles = new String[mRealTimeModes.length];
        for (int i = 0; i < mRealTimeModes.length; ++i) {
            titles[i] = mRealTimeModes[i].getModeTitle();
        }
        return titles;
    }

    private void initModes() {
        final String[] modes_title = getResources().getStringArray(R.array.realtime_modes);
        final String[] modes_hint = getResources().getStringArray(R.array.realtime_mode_hints);
        mRealTimeModes = new RealTimeMode[]{
                //普通模式
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_HEART_RATE];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_DEFAULT;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_STRING;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_HEART_RATE_REDUCED;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_HEART_RATE]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_HEART_RATE_MINUTE];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_DEFAULT;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_LINE;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_HEART_RATE_REDUCED;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_HEART_RATE_MINUTE]);
                        LinePanelFragment linePanel = (LinePanelFragment) getPanel();
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
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((LinePanelFragment) getPanel()).appendValue(intent.getIntExtra(HubService.KEY_DATA, 0));
                    }
                },
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_ECG_FIVE_SEC];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_DEFAULT;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_LINE;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_ECG;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_ECG_FIVE_SEC]);
                        LinePanelFragment linePanel = (LinePanelFragment) getPanel();
                        linePanel.setAxisYScale(200, 1500);
                        linePanel.setMaxPoints(500);
                        linePanel.setXStep(0.01f);
                        linePanel.setAxisXText("Time");
                        linePanel.setAxisYText("Voltage");
                        LinePanelFragment.LineStyle style = new LinePanelFragment.LineStyle();
                        style.line_color = ChartUtils.COLOR_BLUE;
                        style.has_points = false;
                        linePanel.setLineStyle(style);
                        linePanel.redraw();
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int[] ecg_data = intent.getIntArrayExtra(HubService.KEY_DATA);
                        float[] converted = ByteUtils.intArray2floatArray(ecg_data);
                        ((LinePanelFragment) getPanel()).appendValue(converted);
                    }
                },
                //有氧模式
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_AEROBIC];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_AEROBIC;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_STRING;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_HEART_RATE_REDUCED;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_AEROBIC]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
                //无氧模式
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_ANAEROBIC];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_ANAEROBIC;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_STRING;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_HEART_RATE_REDUCED;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_ANAEROBIC]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
                //睡眠模式
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_SLEEP];
                    }

                    @Override
                    public String getEventType() {
                        return Event.TYPE_SLEEP;
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_STRING;
                    }

                    @Override
                    public String getBroadcastAction() {
                        return HubService.ACTION_HEART_RATE_REDUCED;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_SLEEP]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        initModes();
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
                mLocalBroadcastManager.sendBroadcast(
                        new Intent(HubService.ACTION_START_EVENT)
                                .putExtra(HubService.KEY_EVENT_TYPE, Event.TYPE_DEFAULT));
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
            }
        });
        mStopButton = (Button) rootView.findViewById(R.id.buttonStop);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBroadcastManager.sendBroadcast(
                        new Intent(HubService.ACTION_STOP_EVENT));
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
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
        mCurrentMode.setPanel(mCurrentPanel);
        mCurrentMode.initPanelView();
    }
}
