package com.presisco.shared.ui.framework.realtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.presisco.shared.ui.framework.monitor.MonitorPanelFragment;

/**
 * Created by presisco on 2017/4/26.
 */

/**
 * 实时监控模式基类
 */
public abstract class RealTimeMode extends BroadcastReceiver {
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
     * 返回测量模式
     *
     * @return 测量模式字符串
     */
    public abstract String getEventType();

    /**
     * 返回要显示的面板的类型
     *
     * @return 面板的类型字符串
     */
    public abstract String getPanelType();

    /**
     * 返回广播监听器的监听标签
     *
     * @return 监听标签字符串
     */
    public abstract String getBroadcastAction();

    /**
     * 返回模式要显示的名称
     *
     * @return 名称字符串
     */
    public abstract String getModeTitle();

    /**
     * 对面板的界面进行设置
     */
    public abstract void initPanelView();

    /**
     * 继承BroadcastReceiver的函数
     *
     * @param context
     * @param intent
     */
    public abstract void onReceive(Context context, Intent intent);
}
