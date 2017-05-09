package com.presisco.boxmeter.UI.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.Service.HubService;
import com.presisco.shared.ui.fragment.BaseRealTimeFragment;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.StringPanelFragment;
import com.presisco.shared.ui.framework.realtime.RealTimeMode;
import com.presisco.shared.utils.ByteUtils;

import lecho.lib.hellocharts.util.ChartUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RealTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealTimeFragment extends BaseRealTimeFragment implements BaseRealTimeFragment.ActionListener {
    private static final int MODE_DEFAULT_SPO2H = 0;
    private static final int MODE_DEFAULT_PULSE = 1;
    private static final int MODE_DEFAULT_SPO2H_MINUTE = 2;
    private static final int MODE_DEFAULT_PULSE_FIVE_SEC = 3;
    private static final int MODE_AEROBIC = 4;
    private static final int MODE_ANAEROBIC = 5;
    private static final int MODE_SLEEP = 6;

    private RealTimeMode[] mRealTimeModes = null;

    public RealTimeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static RealTimeFragment newInstance() {
        RealTimeFragment fragment = new RealTimeFragment();
        return fragment;
    }

    private void initModes() {
        final String[] modes_title = getResources().getStringArray(R.array.realtime_modes);
        final String[] modes_hint = getResources().getStringArray(R.array.realtime_mode_hints);
        mRealTimeModes = new RealTimeMode[]{
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_SPO2H];
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
                        return HubService.ACTION_SPO2H;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_SPO2H]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_PULSE];
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
                        return HubService.ACTION_PULSE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_PULSE]);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ((StringPanelFragment) getPanel()).setValue(intent.getIntExtra(HubService.KEY_DATA, 0) + "");
                    }
                },
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_SPO2H_MINUTE];
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
                        return HubService.ACTION_SPO2H_VOLUME;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_SPO2H_MINUTE]);
                        LinePanelFragment linePanel = (LinePanelFragment) getPanel();
                        linePanel.setAxisYScale(0, 110);
                        linePanel.setMaxPoints(60);
                        linePanel.setXStep(1f);
                        linePanel.setAxisXText("Time");
                        linePanel.setAxisYText("SPO2H");
                        LinePanelFragment.LineStyle style = new LinePanelFragment.LineStyle();
                        style.line_color = ChartUtils.COLOR_BLUE;
                        style.has_points = false;
                        linePanel.setStyle(style);
                        linePanel.redraw();
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int data = intent.getIntExtra(HubService.KEY_DATA, 25);
                        ((LinePanelFragment) getPanel()).appendValue(data);
                    }
                },
                new RealTimeMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT_PULSE_FIVE_SEC];
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
                        return HubService.ACTION_PULSE_VOLUME;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT_PULSE_FIVE_SEC]);
                        LinePanelFragment linePanel = (LinePanelFragment) getPanel();
                        linePanel.setAxisYScale(0, 20);
                        linePanel.setMaxPoints(500);
                        linePanel.setXStep(0.01f);
                        linePanel.setAxisXText("Time");
                        linePanel.setAxisYText("Pulse");
                        LinePanelFragment.LineStyle style = new LinePanelFragment.LineStyle();
                        style.line_color = ChartUtils.COLOR_BLUE;
                        style.has_points = false;
                        linePanel.setStyle(style);
                        linePanel.redraw();
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int[] data = intent.getIntArrayExtra(HubService.KEY_DATA);
                        float[] converted = ByteUtils.intArray2floatArray(data);
                        ((LinePanelFragment) getPanel()).appendValue(converted);
                    }
                },
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
                        return HubService.ACTION_SPO2H;
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
                        return HubService.ACTION_SPO2H;
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
                        return HubService.ACTION_SPO2H;
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
        initModes();
        setChildListener(this);
        setRealTimeModes(mRealTimeModes);
    }

    @Override
    public void startEvent(RealTimeMode mode) {
        getLocalBroadcastmanager().sendBroadcast(
                new Intent(HubService.ACTION_START_EVENT)
                        .putExtra(HubService.KEY_EVENT_TYPE, mode.getEventType()));
    }

    @Override
    public void endEvent(RealTimeMode mode) {
        getLocalBroadcastmanager().sendBroadcast(
                new Intent(HubService.ACTION_STOP_EVENT));
    }
}
