package com.presisco.boxmeter.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.Data.EventData;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.storage.SQLiteManager;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.ui.fragment.BaseAnalyzeFragment;
import com.presisco.shared.ui.framework.mode.Analyze;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.PiePanelFragment;

import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by presisco on 2017/6/2.
 */

public class AnalyzeFragment extends BaseAnalyzeFragment implements BaseAnalyzeFragment.ActionListener {
    private static final String[] EVENT_TYPES = {Event.TYPE_DEFAULT, Event.TYPE_AEROBIC, Event.TYPE_ANAEROBIC, Event.TYPE_SLEEP};
    private SQLiteManager mDataManager;

    private AnalyzeMode[] mAnalyseModes = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDataManager == null) {
            mDataManager = new SQLiteManager(getContext());
        }
        initModes();
        setChildListener(this);
        setHistoryModes(mAnalyseModes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(
                R.layout.fragment_analyze,
                R.id.spinnerMode,
                R.id.spinnerEvent,
                R.id.monitorHost,
                inflater, container, savedInstanceState);
    }

    private void initModes() {
        mAnalyseModes = new AnalyzeMode[]{
                new AnalyzeMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
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
                new AnalyzeMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
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
                new AnalyzeMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
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
                new AnalyzeMode() {
                    private SliceValue[] result = new SliceValue[]{
                            new SliceValue(),
                            new SliceValue(),
                            new SliceValue()
                    };

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
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
    public BaseEvent[] loadEvents(int position) {
        return mDataManager.getEventsByType(EVENT_TYPES[position]);
    }

    @Override
    public BaseEventData[] loadEventData(long event_id) {
        return mDataManager.getAllDataInEvent(event_id);
    }

    public abstract static class AnalyzeMode extends Analyze<EventData> {
    }
}
