package com.presisco.boxmeter.UI.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.presisco.boxmeter.Data.EventData;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.UI.history.TypedHistoryMode;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private static final int MODE_DEFAULT = 0;
    private static final int MODE_AEROBIC = 1;
    private static final int MODE_ANAEROBIC = 2;
    private static final int MODE_SLEEP = 3;
    private Spinner mModeSpinner;
    private Spinner mEventSpinner;
    private MonitorHostFragment mMonitorHost;

    private TypedHistoryMode[] mAnalyseModes = null;
    private TypedHistoryMode mCurrentMode = null;

    private MonitorPanelFragment mCurrentPanel = null;

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
        final String[] modes_title = getResources().getStringArray(R.array.realtime_modes);
        final String[] modes_hint = getResources().getStringArray(R.array.realtime_mode_hints);
        mAnalyseModes = new TypedHistoryMode[]{
                new TypedHistoryMode() {
                    @Override
                    public String getModeTitle() {
                        return modes_title[MODE_DEFAULT];
                    }

                    @Override
                    public String getPanelType() {
                        return MonitorHostFragment.PANEL_SCROLL_LINE;
                    }

                    @Override
                    public void initPanelView() {
                        getPanel().setHint(modes_hint[MODE_DEFAULT]);
                    }

                    @Override
                    public void analyseData(EventData[] data, int analyse_rate) {

                    }

                    @Override
                    public void displayData() {

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
                    public void displayData() {

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
                    public void displayData() {

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
                    public void displayData() {

                    }
                },

        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initModes();
    }

    private String[] getTitles() {
        String[] titles = new String[mAnalyseModes.length];
        for (int i = 0; i < mAnalyseModes.length; ++i) {
            titles[i] = mAnalyseModes[i].getModeTitle();
        }
        return titles;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
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
                mCurrentMode = mAnalyseModes[pos];
                mMonitorHost.displayPanel(mCurrentMode.getPanelType());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mModeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getTitles()));
        return rootView;
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentMode.initPanelView();
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
