package com.presisco.boxmeter.UI.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.Data.EventData;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.UI.history.TypedHistoryMode;
import com.presisco.boxmeter.storage.SQLiteManager;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.ui.fragment.BaseHistoryFragment;
import com.presisco.shared.ui.framework.monitor.LinePanelFragment;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;

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
                        panel.setLineStyle(main_line_style);
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

                    }

                    @Override
                    public void displayResult() {

                    }
                },
                new TypedHistoryMode() {
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

                    }

                    @Override
                    public void displayResult() {

                    }
                },
                new TypedHistoryMode() {
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

                    }

                    @Override
                    public void displayResult() {

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
    public void deleteEvent() {

    }

    private class AnalyseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
