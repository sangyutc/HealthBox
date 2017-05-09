package com.presisco.boxmeter.UI.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.Data.EventData;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.storage.SQLiteManager;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.ui.fragment.BaseHistoryFragment;
import com.presisco.shared.ui.framework.history.HistoryMode;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.PiePanelFragment;

import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends BaseHistoryFragment implements BaseHistoryFragment.ActionListener {
    private static final int MODE_DEFAULT = 0;
    private static final int MODE_AEROBIC = 1;
    private static final int MODE_ANAEROBIC = 2;
    private static final int MODE_SLEEP = 3;
    private SQLiteManager mDataManager;

    private TypedHistoryMode[] mAnalyseModes = null;

    public HistoryFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    private void initModes() {
        final String[] modes_title = getResources().getStringArray(R.array.history_modes);
        final String[] modes_hint = getResources().getStringArray(R.array.history_mode_hints);
        mAnalyseModes = new TypedHistoryMode[]{
                new TypedHistoryMode() {
                    private EventData[] raw_data;
                    private int analyse_rate;

                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT];
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_LINE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT]);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        raw_data = data;
                        this.analyse_rate = analyse_rate;
                    }

                    @Override
                    public void displayResult() {
                        LinePanelFragment panel = (LinePanelFragment) getPanel();
                        panel.clear();
                        LinePanelFragment.LineStyle main_line_style = new LinePanelFragment.LineStyle();
                        main_line_style.line_color = ChartUtils.COLOR_BLUE;
                        panel.setStyle(main_line_style);
                        panel.setAxisYScale(0, 110);
                        panel.setMaxPoints(60);
                        panel.setXStep(1);
                        panel.setAxisXText("Time");
                        panel.setAxisYText("SPO2H");
                        panel.setScrollable(true);
                        // panel.redraw();
                        float[] data = new float[raw_data.length];
                        for (int i = 0; i < raw_data.length; ++i) {
                            data[i] = raw_data[i].spo2h;
                        }
                        panel.appendValue(data);
                    }
                },
                new TypedHistoryMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_AEROBIC];
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_AEROBIC]);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        int[] counter = new int[]{0, 0, 0};
                        for (int i = 0; i < data.length; ++i) {
                            if (data[i].spo2h > 95) {
                                counter[2]++;
                            } else if (data[i].spo2h > 85) {
                                counter[1]++;
                            } else {
                                counter[0]++;
                            }
                        }
                        result[0].setValue(counter[0]);
                        result[0].setLabel("Low");
                        result[0].setColor(ChartUtils.COLOR_RED);
                        result[1].setValue(counter[1]);
                        result[1].setLabel("Normal");
                        result[1].setColor(ChartUtils.COLOR_GREEN);
                        result[2].setValue(counter[2]);
                        result[2].setLabel("High");
                        result[2].setColor(ChartUtils.COLOR_VIOLET);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                        panel.appendSlices(result);
                    }
                },
                new TypedHistoryMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_ANAEROBIC];
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_ANAEROBIC]);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {

                        int[] counter = new int[]{0, 0, 0};
                        for (int i = 0; i < data.length; ++i) {
                            if (data[i].spo2h > 95) {
                                counter[2]++;
                            } else if (data[i].spo2h > 85) {
                                counter[1]++;
                            } else {
                                counter[0]++;
                            }
                        }
                        result[0].setValue(counter[0]);
                        result[0].setLabel("Low");
                        result[0].setColor(ChartUtils.COLOR_RED);
                        result[1].setValue(counter[1]);
                        result[1].setLabel("Normal");
                        result[1].setColor(ChartUtils.COLOR_GREEN);
                        result[2].setValue(counter[2]);
                        result[2].setLabel("High");
                        result[2].setColor(ChartUtils.COLOR_VIOLET);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                        panel.appendSlices(result);
                    }
                },
                new TypedHistoryMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_SLEEP];
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_SLEEP]);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {

                        int[] counter = new int[]{0, 0, 0};
                        for (int i = 0; i < data.length; ++i) {
                            if (data[i].spo2h > 95) {
                                counter[2]++;
                            } else if (data[i].spo2h > 85) {
                                counter[1]++;
                            } else {
                                counter[0]++;
                            }
                        }
                        result[0].setValue(counter[0]);
                        result[0].setLabel("Low");
                        result[0].setColor(ChartUtils.COLOR_RED);
                        result[1].setValue(counter[1]);
                        result[1].setLabel("Normal");
                        result[1].setColor(ChartUtils.COLOR_GREEN);
                        result[2].setValue(counter[2]);
                        result[2].setLabel("High");
                        result[2].setColor(ChartUtils.COLOR_VIOLET);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                        panel.appendSlices(result);
                    }
                },
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initModes();
        if (mDataManager == null) {
            mDataManager = new SQLiteManager(getContext());
        }
        setChildListener(this);
        setHistoryModes(mAnalyseModes);
    }

    @Override
    public BaseEvent[] loadEvents(int position) {
        String type = null;
        switch (position) {
            case MODE_DEFAULT:
                type = Event.TYPE_DEFAULT;
                break;
            case MODE_AEROBIC:
                type = Event.TYPE_AEROBIC;
                break;
            case MODE_ANAEROBIC:
                type = Event.TYPE_ANAEROBIC;
                break;
            case MODE_SLEEP:
                type = Event.TYPE_SLEEP;
                break;
        }
        return mDataManager.getEventsByType(type);
    }

    @Override
    public BaseEventData[] loadEventData(long event_id) {
        return mDataManager.getAllDataInEvent(event_id);
    }

    @Override
    public void deleteEvent(long event_id) {
        mDataManager.deleteEvent(event_id);
    }

    /**
     * Created by presisco on 2017/4/27.
     */

    public abstract static class TypedHistoryMode extends HistoryMode<EventData> {

    }
}
