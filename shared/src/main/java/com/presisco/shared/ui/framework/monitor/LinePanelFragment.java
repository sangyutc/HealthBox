package com.presisco.shared.ui.framework.monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presisco.shared.R;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private int mMaxPoints = 10;

    private LinkedList<PointValue> mPoints = new LinkedList<>();

    private String mTitleText;
    private String mHintText;

    private TextView mTitle;
    private TextView mHint;

    private LineChartView mLineChart;
    private LineChartData mLineData;
    private String mAxisXText;
    private String mAxisYText;
    private float mAxisYMin = 0;
    private float mAxisYMax = 100;
    private float mLastXCoord = 0;
    private float mXStep = 1;

    private LineStyle mLineStyle = new LineStyle();

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

    public void setMaxPoints(int count) {
        mMaxPoints = count;
    }

    public void setAxisXText(String text) {
        mAxisXText = text;
    }

    public void setAxisYText(String text) {
        mAxisYText = text;
    }

    public void setAxisYScale(float min, float max) {
        mAxisYMax = max;
        mAxisYMin = min;
    }

    public void setLineStyle(LineStyle lineStyle) {
        mLineStyle = lineStyle;
    }

    public void setXStep(float step) {
        mXStep = step;
    }

    public void appendValue(float value) {
        mPoints.add(new PointValue(mLastXCoord + mXStep, value));
        if (mPoints.size() > mMaxPoints) {
            mPoints.remove(0);
        }
        mLastXCoord += mXStep;
        scrollViewport();
        initLineChart();
    }

    public void appendValue(float[] values) {
        for (int i = 1; i < values.length; ++i) {
            mPoints.add(new PointValue(mLastXCoord + mXStep, values[i]));
            mLastXCoord += mXStep;
            if (mPoints.size() > mMaxPoints) {
                mPoints.remove(0);
            }
        }
        scrollViewport();
        initLineChart();
    }

    @Override
    public void redraw() {

    }

    @Override
    public void clear() {
        mPoints.clear();
        resetViewport();
        initLineChart();
    }

    private void initLineChart() {
        mLineChart.setOnValueTouchListener(null);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mLineChart.setViewportCalculationEnabled(false);

        List<Line> lines = new ArrayList<>();

        Line line = new Line(mPoints);
        line.setColor(mLineStyle.line_color);
        line.setShape(mLineStyle.line_shape);
        line.setCubic(mLineStyle.is_cubic);
        line.setFilled(mLineStyle.is_filled);
        line.setHasLabels(mLineStyle.has_label);
        line.setHasLabelsOnlyForSelected(mLineStyle.has_label_selected);
        line.setHasLines(mLineStyle.has_lines);
        line.setHasPoints(mLineStyle.has_points);
        line.setPointColor(mLineStyle.point_color);
        lines.add(line);

        mLineData = new LineChartData(lines);

        if (mAxisXText != null) {
            Axis axisX = new Axis();
            axisX.setName(mAxisXText);
            mLineData.setAxisXBottom(axisX);
        }
        if (mAxisYText != null) {
            Axis axisY = new Axis().setHasLines(true);
            axisY.setName(mAxisYText);
            mLineData.setAxisYLeft(axisY);
        }

        mLineData.setBaseValue(Float.NEGATIVE_INFINITY);

        mLineChart.setLineChartData(mLineData);
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

        mLineChart = (LineChartView) rootView.findViewById(R.id.lineChart);

        mViewCreated.panelViewCreated(this);

        initLineChart();

        resetViewport();

        return rootView;
    }

    private void scrollViewport() {
        final Viewport v = new Viewport(mLineChart.getMaximumViewport());
        v.bottom = mAxisYMin;
        v.top = mAxisYMax;
        if (mLastXCoord < mMaxPoints * mXStep) {
            v.left = 0;
            v.right = mMaxPoints * mXStep;
        } else {
            v.left = mLastXCoord - mMaxPoints * mXStep;
            v.right = mLastXCoord;
        }
        mLineChart.setMaximumViewport(v);
        mLineChart.setCurrentViewport(v);
    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mLineChart.getMaximumViewport());
        v.bottom = mAxisYMin;
        v.top = mAxisYMax;
        v.left = 0;
        v.right = mMaxPoints * mXStep;
        mLineChart.setMaximumViewport(v);
        mLineChart.setCurrentViewport(v);
    }

    public static class LineStyle {
        public static final int COLOR_LINE_DEFAULT = Color.parseColor("#DFDFDF");
        public static final int COLOR_POINT_DEFAULT = Color.parseColor("#DFDFDF");

        public int line_color = ChartUtils.DEFAULT_COLOR;
        public int point_color = ChartUtils.DEFAULT_DARKEN_COLOR;
        public ValueShape line_shape = ValueShape.CIRCLE;
        public boolean is_cubic = false;
        public boolean is_filled = false;
        public boolean has_label = false;
        public boolean has_label_selected = false;
        public boolean has_lines = true;
        public boolean has_points = false;
    }
}
