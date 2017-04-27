package com.presisco.boxmeter.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.presisco.boxmeter.R;
import com.presisco.shared.bluetooth.BluetoothSerialPortManager;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.utils.LCAT;

public class BTService extends BaseBluetoothService
        implements
        BluetoothSerialPortManager.StateListener,
        BluetoothSerialPortManager.BlockListener {

    private static final String TARGET_DEVICE_NAME = "SPO2H";

    private static String[] TEXT_WARNING_TITLE;
    private static String[] TEXT_WARNING_CONTENT;

    public BTService() {
    }

    private void loadStringRes() {
        Resources res = getResources();
        TEXT_WARNING_TITLE = res.getStringArray(R.array.title_warning_bt);
        TEXT_WARNING_CONTENT = res.getStringArray(R.array.text_warning_bt);
    }

    public void sendWarnNotification(int code) {
        sendWarnNotification(
                code,
                R.drawable.ic_launcher,
                TEXT_WARNING_TITLE[code],
                TEXT_WARNING_CONTENT[code],
                PendingIntent.getActivity(
                        this, 0, new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void setup() {
        BluetoothSerialPortManager mBTManager = getBTManager();
        if (!mBTManager.isBTEnabled()) {
            sendWarnNotification(WARN_CODE_DISABLED_BT);
            return;
        }
        if (!mBTManager.devicePaired(TARGET_DEVICE_NAME)) {
            sendWarnNotification(WARN_CODE_NOT_PAIRED);
        }
        mBTManager.setTargetDeviceType(BluetoothSerialPortManager.DEV_TYPE_BLOCK);
        mBTManager.setReceiveBufferSize(5);
        mBTManager.setBlockListener(this);
        mBTManager.setBluetoothStateListener(this);
        setTargetName(TARGET_DEVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LCAT.d(this, "created");
        loadStringRes();
        setup();
        startConnection();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = super.onBind(intent);
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void connectFailed() {
        sendWarnNotification(WARN_CODE_CONNECT_FAILED);
    }

    @Override
    public void connected() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_DISCONNECTED);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_CONNECT_FAILED);
    }

    @Override
    public void disconnected() {
        sendWarnNotification(WARN_CODE_DISCONNECTED);
    }

    @Override
    public void enabled() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_DISABLED_BT);
    }

    @Override
    public void disabled() {
        sendWarnNotification(WARN_CODE_DISABLED_BT);
    }

    /**
     * 蓝牙数据处理函数
     * 如果数据过于频繁请在此处进行初步筛选处理
     *
     * @param data 接收到的蓝牙数据
     */
    @Override
    public void receivedBlock(byte[] data) {
        if (data.length == 5) {
            getPacketReceivedListener().onPacketReceived(data);
        }
    }
}
