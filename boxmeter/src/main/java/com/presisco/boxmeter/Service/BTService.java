package com.presisco.boxmeter.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;

import com.presisco.boxmeter.R;
import com.presisco.shared.bluetooth.BluetoothSerialPortManager;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.utils.LCAT;

public class BTService extends BaseBluetoothService
        implements
        BluetoothSerialPortManager.StateListener,
        BluetoothSerialPortManager.BlockListener {
    public static final String ACTION_TARGET_DATA_RECEIVED = "TARGET_DATA_RECEIVED";
    public static final String ACTION_TARGET_DATA_SEND = "TARGET_DATA_SEND";
    public static final String ACTION_TARGET_RECONNECT = "TARGET_RECONNECT";

    public static final String KEY_DATA = "KEY_DATA";

    private static final int WARN_CODE_DISABLED_BT = 1;
    private static final int WARN_CODE_NOT_PAIRED = 2;
    private static final int WARN_CODE_DISCONNECTED = 3;
    private static final int WARN_CODE_CONNECT_FAILED = 4;

    private static final String TARGET_DEVICE_NAME = "SPO2H";
    private static final String KEY_TARGET_ADDRESS = "TARGET_ADDRESS";

    private static String[] TEXT_WARNING_TITLE;
    private static String[] TEXT_WARNING_CONTENT;

    private final IBinder mBinder = new BTServiceBinder();
    private String mTargetAdress = "";
    private long mTimeLimiter = 0;

    public BTService() {
    }

    private void loadStringRes() {
        Resources res = getResources();
        TEXT_WARNING_TITLE = res.getStringArray(R.array.title_warning_bt);
        TEXT_WARNING_CONTENT = res.getStringArray(R.array.text_warning_bt);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
        mBTManager.setListeners(this);
        mBTManager.setReceiveBufferSize(5);
        setTargetName(TARGET_DEVICE_NAME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadStringRes();
        setup();
        connected();
    }

    @Override
    public void onDestroy() {
        LCAT.d(this, "destroyed");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void connectFailed() {
        sendWarnNotification(WARN_CODE_CONNECT_FAILED);
    }

    @Override
    public void connected() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_DISCONNECTED);
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
    public void received(byte[] data) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mTimeLimiter > 100) {
            broadcastData(data);
            mTimeLimiter = currentTime;
        }
    }

    public class BTServiceBinder extends Binder {
        public BTService getService() {
            return BTService.this;
        }
    }

}
