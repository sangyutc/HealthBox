package com.presisco.shared.ui.framework.monitor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MonitorPanelFragment extends Fragment {
    protected ViewCreatedListener mViewCreated;

    public MonitorPanelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract void setTitle(String title);

    public abstract void setHint(String hint);

    public abstract void redraw();

    public abstract void clear();

    public void setViewCreatedListener(ViewCreatedListener viewCreatedListener) {
        mViewCreated = viewCreatedListener;
    }

    public interface ViewCreatedListener {
        void panelViewCreated(MonitorPanelFragment panel);
    }
}
