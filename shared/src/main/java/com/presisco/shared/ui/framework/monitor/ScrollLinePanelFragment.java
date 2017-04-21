package com.presisco.shared.ui.framework.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.presisco.shared.R;

/**
 * Created by presisco on 2017/4/20.
 */

public class ScrollLinePanelFragment extends ChartPanelFragment {

    public ScrollLinePanelFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scroll_line_panel, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setHint(String hint) {

    }

    @Override
    public void appendValue(float[] values) {

    }

    @Override
    public void appendValue(float value) {

    }

    @Override
    protected void initChart() {

    }

    @Override
    public void redraw() {

    }

    @Override
    public void clear() {

    }
}
