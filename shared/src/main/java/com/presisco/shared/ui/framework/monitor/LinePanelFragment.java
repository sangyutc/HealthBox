package com.presisco.shared.ui.framework.monitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presisco.shared.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinePanelFragment#} factory method to
 * create an instance of this fragment.
 */
public class LinePanelFragment extends MonitorPanelFragment {
    private ArrayList<PointValue> mPoints;

    private String mTitleText;
    private String mHintText;

    private TextView mTitle;
    private TextView mHint;

    private LineChartView chart;
    private LineChartData data;
    private String mAxisXText;
    private String mAxisYText;


    public LinePanelFragment() {
    }

    public static LinePanelFragment newInstance() {
        LinePanelFragment linePanelFragment = new LinePanelFragment();
        return linePanelFragment;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setHint(String hint) {
        mHint.setText(hint);
    }

    public void setAxisXText(String text) {
        mAxisXText = text;
    }

    public void setAxisYText(String text) {
        mAxisYText = text;
    }

    @Override
    public void redraw() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_line_panel, container, false);

        mTitle = (TextView) rootView.findViewById(R.id.textTitle);
        mHint = (TextView) rootView.findViewById(R.id.textHint);
        if (mTitleText != null) {
            mTitle.setText(mTitleText);
        }
        if (mHintText != null) {
            mHint.setText(mHintText);
        }

        mViewCreated.panelViewCreated(this);

        chart = (LineChartView) rootView.findViewById(R.id.lineChart);
        chart.setOnValueTouchListener(null);

        generateData();

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        chart.setLineChartData(data);

        resetViewport();

        return rootView;
    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 40;
        v.top = 180;
        v.left = 0;
        v.right = 100;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int j = 0; j < 100; ++j) {
            values.add(new PointValue(j, (float) (60 + Math.random() * 60f)));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(true);
        line.setHasPoints(false);
        //line.setHasGradientToTransparent(hasGradientToTransparent);
        lines.add(line);

        data = new LineChartData(lines);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(mAxisXText);
        axisY.setName(mAxisYText);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);

    }
}
