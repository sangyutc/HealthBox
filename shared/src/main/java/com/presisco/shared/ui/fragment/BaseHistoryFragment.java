package com.presisco.shared.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.presisco.shared.R;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.data.BaseEventData;
import com.presisco.shared.ui.framework.history.HistoryMode;
import com.presisco.shared.ui.framework.monitor.MonitorHostFragment;
import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseHistoryFragment extends Fragment implements MonitorPanelFragment.ViewCreatedListener {
    private ActionListener mChildListener;
    private Spinner mModeSpinner;
    private Spinner mEventSpinner;
    private EventAdapter mEventAdapter;
    private MonitorHostFragment mMonitorHost;
    private MonitorPanelFragment mCurrentPanel = null;
    private HistoryMode[] mHistoryModes = null;
    private HistoryMode mCurrentMode = null;
    private BaseEvent[] mEvents = null;
    private ArrayList<String> mEventTitles = new ArrayList<>();

    public BaseHistoryFragment() {
        // Required empty public constructor
    }

    protected void setChildListener(ActionListener listener) {
        mChildListener = listener;
    }

    protected void setHistoyModes(HistoryMode[] modes) {
        mHistoryModes = modes;
    }

    private String[] getTitles() {
        String[] titles = new String[mHistoryModes.length];
        for (int i = 0; i < mHistoryModes.length; ++i) {
            titles[i] = mHistoryModes[i].getModeTitle();
        }
        return titles;
    }

    private void refreshEventTitles() {
        mEventAdapter.clear();
        for (BaseEvent event : mEvents) {
            mEventAdapter.add(event.start_time);
        }
        mEventAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_history, container, false);
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
                mCurrentMode = mHistoryModes[pos];

                mMonitorHost.displayPanel(mCurrentMode.getPanelType());
                mEvents = mChildListener.loadEvents(pos);

                refreshEventTitles();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mModeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getTitles()));

        mEventAdapter = new EventAdapter(getContext());
        mEventSpinner = (Spinner) rootView.findViewById(R.id.spinnerEvent);
        mEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BaseEventData[] event_data = mChildListener.loadEventData(mEvents[position].id);
                mCurrentMode.analyseData(event_data, mEvents[position].analyse_rate);
                mCurrentMode.displayResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mEventSpinner.setAdapter(mEventAdapter);
        /*
        rootView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        return rootView;
    }

    @Override
    public void panelViewCreated(MonitorPanelFragment panel) {
        mCurrentPanel = panel;
        mCurrentPanel.clear();
        mCurrentMode.setPanel(mCurrentPanel);
        mCurrentMode.initPanelView();
    }

    /**
     * 子类监听器，使基类能调用子类代码
     */
    public interface ActionListener {
        BaseEvent[] loadEvents(int position);

        BaseEventData[] loadEventData(long event_id);

        void deleteEvent();
    }

    private class EventAdapter extends ArrayAdapter<String> {
        public EventAdapter(Context _context) {
            super(_context, R.layout.item_event_spinner, R.id.itemTitle, mEventTitles);
        }
    }
}
