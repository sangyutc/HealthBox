package com.presisco.shared.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.presisco.shared.bluetooth.BluetoothSerialPortManager;
import com.presisco.shared.utils.ByteUtils;
import com.presisco.shared.utils.LCAT;

public abstract class BaseBluetoothService extends Service {
    public static final String ACTION_TARGET_DATA_RECEIVED = "com.presisco.shared.service.TARGET_DATA_RECEIVED";
    public static final String ACTION_TARGET_DATA_SEND = "com.presisco.shared.service.TARGET_DATA_SEND";

    public static final String KEY_DATA = "KEY_DATA";
    protected static final int WARN_CODE_DISABLED_BT = 1;
    protected static final int WARN_CODE_NOT_PAIRED = 2;
    protected static final int WARN_CODE_DISCONNECTED = 3;
    private String mTargetName = "";
    private LocalBroadcastManager mBroadcastManager = null;
    private BluetoothSerialPortManager mBTManager = null;

    public BaseBluetoothService() {
    }

    public void sendWarnNotification(int code, int icon_res, String title, String content, PendingIntent pendingIntent) {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(code,
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(icon_res)
                                .setContentTitle(title)
                                .setContentText(content)
                                .setContentIntent(pendingIntent)
                                .build());
    }

    public void broadcastData(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(ACTION_TARGET_DATA_RECEIVED);
        intent.putExtra(KEY_DATA, data);
        sendBroadcast(intent);
        mBroadcastManager.sendBroadcast(intent);
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TARGET_DATA_SEND);
        mBroadcastManager.registerReceiver(new BTSendReceiver(), filter);
    }

    protected BluetoothSerialPortManager getBTManager() {
        return mBTManager;
    }

    protected void setTargetName(String name) {
        mTargetName = name;
    }

    @Override
    public void onCreate() {
        registerReceivers();
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBTManager = new BluetoothSerialPortManager(this);
    }

    @Override
    public void onDestroy() {
        mBTManager.disconnect();
        super.onDestroy();
    }

    private class BTSendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LCAT.d(this, "send: " + ByteUtils.bytes2hex(intent.getByteArrayExtra(KEY_DATA)));
            mBTManager.send(intent.getByteArrayExtra(KEY_DATA));
        }
    }

    private class BTReconnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBTManager.connectName(mTargetName);
        }
    }

    private class BTdisconnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBTManager.disconnect();
        }
    }

}
