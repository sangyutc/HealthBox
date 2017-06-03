package com.example.heartmeter.UI.Fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartmeter.Data.Event;
import com.example.heartmeter.Data.EventData;
import com.example.heartmeter.R;
import com.example.heartmeter.storage.SQLiteManager;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.ui.fragment.BaseAnalyzeFragment;
import com.presisco.shared.ui.framework.mode.Analyze;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.PiePanelFragment;
import com.presisco.shared.utils.Classifier;
import com.presisco.shared.utils.LCAT;
import com.presisco.shared.utils.ValueUtils;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.SliceValue;

/**
 * Created by presisco on 2017/6/3.
 */

public class AnalyzeFragment extends BaseAnalyzeFragment implements BaseAnalyzeFragment.ActionListener {
    private static final String[] EVENT_TYPES = {Event.TYPE_DEFAULT, Event.TYPE_AEROBIC, Event.TYPE_ANAEROBIC, Event.TYPE_SLEEP};
    private SQLiteManager mDataManager;
    private int user_age;

    private AnalyzeMode[] mAnalyseModes = null;

    private Resources res = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        if (mDataManager == null) {
            mDataManager = new SQLiteManager(getContext());
        }
        initModes();
        setChildListener(this);
        setHistoryModes(mAnalyseModes);
        user_age = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("age", 30);
        LCAT.d(this, "user age: " + user_age);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(
                R.layout.fragment_analyze,
                R.id.spinnerMode,
                R.id.spinnerEvent,
                R.id.analyzeHost,
                inflater, container, savedInstanceState);
    }

    private void initModes() {
        mAnalyseModes = new AnalyzeMode[]{
                new AnalyzeMode() {
                    private final double[] PARTITION = ValueUtils.convertStringArray2DoubleArray(res.getStringArray(R.array.default_partition));
                    private final int[] COLORS = ValueUtils.convertStringArray2ColorArray(res.getStringArray(R.array.default_colors));
                    private final String[] CLASSIFICATION = res.getStringArray(R.array.default_classification);
                    private String result_class = CLASSIFICATION[2];
                    private SliceValue[] distribution = new SliceValue[0];

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        int[] dist_value = new int[CLASSIFICATION.length];

                        int average = 0;
                        for (int i = 0; i < data.length; ++i) {
                            int class_index = Classifier.classify(data[i].heart_rate, PARTITION);
                            dist_value[class_index]++;
                            average += data[i].heart_rate;
                        }
                        average /= data.length;

                        result_class = Classifier.classify(average, PARTITION, CLASSIFICATION);

                        ArrayList<SliceValue> temp = new ArrayList<>();
                        for (int i = 0; i < dist_value.length; ++i) {
                            if (dist_value[i] > 0) {
                                temp.add(
                                        new SliceValue(dist_value[i])
                                                .setLabel(CLASSIFICATION[i])
                                                .setColor(COLORS[i]));
                            }
                        }
                        distribution = temp.toArray(new SliceValue[temp.size()]);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.setHint(result_class);
                        panel.appendSlices(distribution);
                    }
                },
                new AnalyzeMode() {
                    private final double[] PARTITION = ValueUtils.convertStringArray2DoubleArray(res.getStringArray(R.array.aerobic_partition));
                    private final int[] COLORS = ValueUtils.convertStringArray2ColorArray(res.getStringArray(R.array.aerobic_colors));
                    private final String[] CLASSIFICATION = res.getStringArray(R.array.aerobic_classification);
                    private String result_class = CLASSIFICATION[2];

                    private SliceValue[] distribution = new SliceValue[0];

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        int[] dist_value = new int[CLASSIFICATION.length];

                        int average = 0;
                        for (int i = 0; i < data.length; ++i) {
                            int class_index = Classifier.classify((double) data[i].heart_rate / (220 - user_age), PARTITION);
                            dist_value[class_index]++;
                            average += data[i].heart_rate;
                        }
                        average /= data.length;

                        result_class = Classifier.classify((double) average / (220 - user_age), PARTITION, CLASSIFICATION);

                        ArrayList<SliceValue> temp = new ArrayList<>();
                        for (int i = 0; i < dist_value.length; ++i) {
                            if (dist_value[i] > 0) {
                                temp.add(
                                        new SliceValue(dist_value[i])
                                                .setLabel(CLASSIFICATION[i])
                                                .setColor(COLORS[i]));
                            }
                        }
                        distribution = temp.toArray(new SliceValue[temp.size()]);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.setHint(result_class);
                        panel.appendSlices(distribution);
                    }
                },
                new AnalyzeMode() {
                    private final double[] PARTITION = ValueUtils.convertStringArray2DoubleArray(res.getStringArray(R.array.anaerobic_partition));
                    private final int[] COLORS = ValueUtils.convertStringArray2ColorArray(res.getStringArray(R.array.anaerobic_colors));
                    private final String[] CLASSIFICATION = res.getStringArray(R.array.anaerobic_classification);
                    private String result_class = CLASSIFICATION[2];
                    private SliceValue[] distribution = new SliceValue[0];

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        int[] dist_value = new int[CLASSIFICATION.length];

                        int average = 0;
                        for (int i = 0; i < data.length; ++i) {
                            int class_index = Classifier.classify(data[i].heart_rate / (220 - user_age), PARTITION);
                            dist_value[class_index]++;
                            average += data[i].heart_rate;
                        }
                        average /= data.length;

                        result_class = Classifier.classify(average / (220 - user_age), PARTITION, CLASSIFICATION);

                        ArrayList<SliceValue> temp = new ArrayList<>();
                        for (int i = 0; i < dist_value.length; ++i) {
                            if (dist_value[i] > 0) {
                                temp.add(
                                        new SliceValue(dist_value[i])
                                                .setLabel(CLASSIFICATION[i])
                                                .setColor(COLORS[i]));
                            }
                        }
                        distribution = temp.toArray(new SliceValue[temp.size()]);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.setHint(result_class);
                        panel.appendSlices(distribution);
                    }
                },
                new AnalyzeMode() {
                    private final double[] PARTITION = ValueUtils.convertStringArray2DoubleArray(res.getStringArray(R.array.sleep_partition));
                    private final int[] COLORS = ValueUtils.convertStringArray2ColorArray(res.getStringArray(R.array.sleep_colors));
                    private final String[] CLASSIFICATION = res.getStringArray(R.array.sleep_classification);
                    private String result_class = CLASSIFICATION[1];
                    private SliceValue[] distribution = new SliceValue[0];

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_PIE;
                    }

                    @Override
                    public void initPanelView() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.clear();
                        PiePanelFragment.PieStyle style = new PiePanelFragment.PieStyle();
                        style.has_label = true;
                        style.has_label_outside = false;
                        panel.setStyle(style);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {
                        int[] dist_value = new int[CLASSIFICATION.length];

                        int average = 0;
                        for (int i = 0; i < data.length; ++i) {
                            int class_index = Classifier.classify(data[i].heart_rate, PARTITION);
                            dist_value[class_index]++;
                            average += data[i].heart_rate;
                        }
                        average /= data.length;

                        result_class = Classifier.classify(average, PARTITION, CLASSIFICATION);

                        ArrayList<SliceValue> temp = new ArrayList<>();
                        for (int i = 0; i < dist_value.length; ++i) {
                            if (dist_value[i] > 0) {
                                temp.add(
                                        new SliceValue(dist_value[i])
                                                .setLabel(CLASSIFICATION[i])
                                                .setColor(COLORS[i]));
                            }
                        }
                        distribution = temp.toArray(new SliceValue[temp.size()]);
                    }

                    @Override
                    public void displayResult() {
                        PiePanelFragment panel = (PiePanelFragment) getPanel();
                        panel.setHint(result_class);
                        panel.appendSlices(distribution);
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
