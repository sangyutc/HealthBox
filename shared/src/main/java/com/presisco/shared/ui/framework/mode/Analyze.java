package com.presisco.shared.ui.framework.mode;

import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

/**
 * Created by presisco on 2017/6/2.
 */

/**
 * 历史分析模式的基类
 *
 * @param <EVENT_DATA> 测量数据类型
 */
public abstract class Analyze<EVENT_DATA> {
    MonitorPanelFragment mPanel;

    /**
     * 获取当前显示的面板
     *
     * @return 面板的引用
     */
    public MonitorPanelFragment getPanel() {
        return mPanel;
    }

    /**
     * 设置当前显示的面板
     *
     * @param panel 面板的引用
     */
    public void setPanel(MonitorPanelFragment panel) {
        mPanel = panel;
    }

    /**
     * 返回要显示的面板的类型
     *
     * @return 面板的类型字符串
     */
    public abstract String getPanelType();

    /**
     * 对面板的界面进行设置
     */
    public abstract void initPanelView();

    /**
     * 对给定的数据进行分析
     *
     * @param data         测量到的数据数组
     * @param analyse_rate 每秒钟有多少次测量信息
     */
    public abstract void analyseData(EVENT_DATA[] data, int analyse_rate);

    /**
     * 对分析结果进行呈现
     */
    public abstract void displayResult();
}
