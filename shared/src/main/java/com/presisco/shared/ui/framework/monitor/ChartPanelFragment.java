package com.presisco.shared.ui.framework.monitor;

/**
 * Created by presisco on 2017/4/20.
 */

public abstract class ChartPanelFragment extends MonitorPanelFragment {

    public abstract void appendValue(float[] values);

    public abstract void appendValue(float value);

    protected abstract void initChart();
}
