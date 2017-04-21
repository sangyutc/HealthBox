package com.presisco.shared.ui.framework.monitor;


import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MonitorPanelFragment extends Fragment {
    protected ViewCreatedListener mViewCreated;

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
