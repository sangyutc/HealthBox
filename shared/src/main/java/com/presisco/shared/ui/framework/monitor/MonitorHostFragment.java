package com.presisco.shared.ui.framework.monitor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.presisco.shared.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MonitorHostFragment extends Fragment {
    public static final String PANEL_STRING = "STRING";
    public static final String PANEL_LINE = "LINE";
    public static final String PANEL_SCROLL_LINE = "SCROLL_LINE";
    public static final String PANEL_PIE = "PIE";

    private HashMap<String, MonitorPanelFragment> mPanels;
    private MonitorPanelFragment mCurrentPanel;
    private String mCurrentType = "";
    private MonitorPanelFragment.ViewCreatedListener mPanelViewCreatedListener;

    public static MonitorHostFragment newInstance() {
        return new MonitorHostFragment();
    }

    private void preparePanels() {
        mPanels = new HashMap<>();
        mPanels.put(PANEL_STRING, new StringPanelFragment());
        mPanels.put(PANEL_LINE, new LinePanelFragment());
        mPanels.put(PANEL_SCROLL_LINE, new ScrollLinePanelFragment());
        mPanels.put(PANEL_PIE, new PiePanelFragment());

        for (Map.Entry<String, MonitorPanelFragment> set : mPanels.entrySet()) {
            set.getValue().setViewCreatedListener(mPanelViewCreatedListener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_monitor, container, false);
        preparePanels();

        return rootView;
    }

    public void displayPanel(String type) {
        if (mCurrentType != type) {
            mCurrentPanel = mPanels.get(type);
            mCurrentType = type;
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.monitorContent, mCurrentPanel);
            trans.commit();
        } else {
            mPanelViewCreatedListener.panelViewCreated(mCurrentPanel);
        }
    }

    public MonitorPanelFragment getCurrentFragment() {
        return mCurrentPanel;
    }

    public void setPanelViewCreatedListener(MonitorPanelFragment.ViewCreatedListener listener) {
        mPanelViewCreatedListener = listener;
    }

}
