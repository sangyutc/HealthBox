package com.presisco.shared.ui.framework.monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presisco.shared.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Created by presisco on 2017/4/20.
 */

public class ScrollLinePanelFragment extends ChartPanelFragment {
    private int mMaxPoints = 10;
    private LinkedList<PointValue> mPoints = new LinkedList<>();

    private TextView mHint;

    private LineChartView mMainLineChart;
    private PreviewLineChartView mPreviewLineChart;

    private LineChartData mMainLineData;
    private LineChartData mPreviewLineData;
    private String mAxisXText;
    private String mAxisYText;
    private float mAxisYMin = 0;
    private float mAxisYMax = 100;
    private float mAxisXBaseline = 0;
    private float mLastXCoord = 0;
    private float mXStep = 1;

    private LineStyle mMainLineStyle = new LineStyle();
    private LineStyle mPreviewLineStyle = new LineStyle();

    public ScrollLinePanelFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scroll_line_panel, container, false);

        mHint = (TextView) rootView.findViewById(R.id.textHint);

        mMainLineChart = (LineChartView) rootView.findViewById(R.id.lineChart);
        mPreviewLineChart = (PreviewLineChartView) rootView.findViewById(R.id.previewLineChart);

        mPreviewLineChart.setViewportChangeListener(new ViewportChangeListener() {
            @Override
            public void onViewportChanged(Viewport viewport) {
                mMainLineChart.setCurrentViewport(viewport);
            }
        });

        mMainLineChart.setZoomEnabled(false);
        mMainLineChart.setScrollEnabled(false);

        mViewCreated.panelViewCreated(this);

        return rootView;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setHint(String hint) {

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

    public void setAxisXBaseline(float baseline) {
        mAxisXBaseline = baseline;
    }

    public void setMainLineStyle(LineStyle lineStyle) {
        mMainLineStyle = lineStyle;
    }

    public void setPreviewLineStyle(LineStyle lineStyle) {
        mPreviewLineStyle = lineStyle;
    }

    public void setXStep(float step) {
        mXStep = step;
    }

    @Override
    public void appendValue(float[] values) {
        for (int i = 1; i < values.length; ++i) {
            mPoints.add(new PointValue(mLastXCoord + mXStep, values[i]));
            mLastXCoord += mXStep;
            if (mPoints.size() > mMaxPoints) {
                mPoints.remove(0);
            }
        }
        initChart();
    }

    @Override
    public void appendValue(float value) {
        mPoints.add(new PointValue(mLastXCoord + mXStep, value));
        if (mPoints.size() > mMaxPoints) {
            mPoints.remove(0);
        }
        mLastXCoord += mXStep;
        initChart();
    }

    @Override
    protected void initChart() {
        mMainLineChart.setOnValueTouchListener(null);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mMainLineChart.setViewportCalculationEnabled(false);

        List<Line> lines = new ArrayList<>();

        Line line = new Line(mPoints);

        line.setColor(mMainLineStyle.line_color);
        line.setShape(mMainLineStyle.line_shape);
        line.setCubic(mMainLineStyle.is_cubic);
        line.setFilled(mMainLineStyle.is_filled);
        line.setHasLabels(mMainLineStyle.has_label);
        line.setHasLabelsOnlyForSelected(mMainLineStyle.has_label_selected);
        line.setHasLines(mMainLineStyle.has_lines);
        line.setHasPoints(mMainLineStyle.has_points);
        line.setPointColor(mMainLineStyle.point_color);
        lines.add(line);

        mMainLineData = new LineChartData(lines);

        List<Line> preview_lines = new ArrayList<>();
        Line preview_line = new Line(mPoints);

        preview_line.setColor(mPreviewLineStyle.line_color);
        preview_line.setShape(mPreviewLineStyle.line_shape);
        preview_line.setCubic(mPreviewLineStyle.is_cubic);
        preview_line.setFilled(mPreviewLineStyle.is_filled);
        preview_line.setHasLabels(mPreviewLineStyle.has_label);
        preview_line.setHasLabelsOnlyForSelected(mPreviewLineStyle.has_label_selected);
        preview_line.setHasLines(mPreviewLineStyle.has_lines);
        preview_line.setHasPoints(mPreviewLineStyle.has_points);
        preview_line.setPointColor(mPreviewLineStyle.point_color);
        preview_lines.add(preview_line);
        mPreviewLineData = new LineChartData(preview_lines);

        if (mAxisXText != null) {
            Axis axisX = new Axis();
            axisX.setName(mAxisXText);
            mMainLineData.setAxisXBottom(axisX);
        }
        if (mAxisYText != null) {
            Axis axisY = new Axis().setHasLines(true);
            axisY.setName(mAxisYText);
            mMainLineData.setAxisYLeft(axisY);
        }

        mMainLineData.setBaseValue(Float.NEGATIVE_INFINITY);

        mMainLineChart.setLineChartData(mMainLineData);
        mPreviewLineChart.setLineChartData(mPreviewLineData);
    }

    private void previewX() {
        Viewport tempViewport = new Viewport(mMainLineChart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        mPreviewLineChart.setCurrentViewport(tempViewport);
        //mPreviewLineChart.setCurrentViewportWithAnimation(tempViewport);
        mPreviewLineChart.setZoomType(ZoomType.HORIZONTAL);
    }

    @Override
    public void redraw() {
        initChart();
        previewX();
    }

    @Override
    public void clear() {

    }

    public static class LineStyle {
        public static final int COLOR_LINE_DEFAULT = Color.parseColor("#DFDFDF");
        public static final int COLOR_PREVIEW_LINE_DEFAULT = Color.parseColor("#BDBDBD");
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
