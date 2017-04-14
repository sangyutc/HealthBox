package com.example.heartmeter.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.heartmeter.R;
import com.presisco.shared.bluetooth.BluetoothSerialPortManager;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.utils.ByteUtils;
import com.presisco.shared.utils.LCAT;

public class BTService extends BaseBluetoothService
        implements
        BluetoothSerialPortManager.StateListener,
        BluetoothSerialPortManager.ByteListener {

    private static final String TARGET_DEVICE_NAME = "ECG";
    private static final String DEFAULT_TARGET_ADDRESS = "98:D3:31:00:02:25";
    private static final String KEY_TARGET_ADDRESS = "TARGET_ADDRESS";
    private static final int MIN_PACKET_SIZE = 5;
    private static final int MAX_PACKET_SIZE = 250;

    private static final int INDEX_PACKET_HEAD = 0;
    private static final int INDEX_PACKET_TYPE = 1;
    private static final int INDEX_PACKET_LOADSIZE_HIGH = 2;
    private static final int INDEX_PACKET_LOADSIZE_LOW = 3;
    private static final int INDEX_OTHERS = -1;

    private static final int HEAD_RESPONSE = 0xC0;
    private static final int HEAD_DATA = 0xA0;

    private static String[] TEXT_WARNING_TITLE;
    private static String[] TEXT_WARNING_CONTENT;
    //private String mTargetAdress="98:D3:31:00:02:25";
    private int mBTByteCursor = 0;
    private int mBTPacketSize = MIN_PACKET_SIZE;
    private int mPacketType = HEAD_RESPONSE;
    private byte[] mBTBuffer = new byte[MAX_PACKET_SIZE];

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
        mBTManager.setTargetDeviceType(BluetoothSerialPortManager.DEV_TYPE_BYTE);
        mBTManager.setByteListener(this);
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
    public void received(byte data) {
        if (mBTByteCursor == INDEX_PACKET_HEAD) {
            mBTBuffer[INDEX_PACKET_HEAD] = data;
            mPacketType = data & 0xF0;
            switch (mPacketType) {
                case HEAD_RESPONSE:
                    mBTPacketSize = 7;
                    break;
                case HEAD_DATA:
                    mBTPacketSize = 6;
                    break;
            }
        } else {
            if (mBTByteCursor == mBTPacketSize - 1) {
                byte xor = data;
                if (xor == ByteUtils.getXOR(mBTBuffer, mBTPacketSize - 1)) {
                    byte[] packet = new byte[mBTPacketSize - 1];
                    for (int i = 0; i < mBTPacketSize - 1; ++i) {
                        packet[i] = mBTBuffer[i];
                    }
                    //LCAT.d(this,"receiverd packet: "+ByteUtils.bytes2hex(packet));
                    //broadcastData(packet);
                    getPacketReceivedListener().onReceived(packet);
                }
                mBTByteCursor = INDEX_PACKET_HEAD - 1;
                mBTPacketSize = MIN_PACKET_SIZE;
            } else {
                mBTBuffer[mBTByteCursor] = data;
            }
        }
        mBTByteCursor++;
    }
}
