package com.presisco.shared.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.presisco.shared.bluetooth.BluetoothSerialPortManager;
import com.presisco.shared.utils.ByteUtils;
import com.presisco.shared.utils.LCAT;

public abstract class BaseBluetoothService extends Service {
    public static final String ACTION_TARGET_DATA_RECEIVED = "com.presisco.shared.service.TARGET_DATA_RECEIVED";
    public static final String ACTION_TARGET_DATA_SEND = "com.presisco.shared.service.TARGET_DATA_SEND";
    public static final String ACTION_TARGET_CONNECT = "com.presisco.shared.service.TARGET_CONNECT";

    public static final String KEY_DATA = "KEY_DATA";
    protected static final int WARN_CODE_DISABLED_BT = 1;
    protected static final int WARN_CODE_NOT_PAIRED = 2;
    protected static final int WARN_CODE_DISCONNECTED = 3;
    protected static final int WARN_CODE_CONNECT_FAILED = 4;
    protected BaseBTServiceBinder mBinder = new BaseBTServiceBinder();
    private String mTargetName = "";
    private LocalBroadcastManager mBroadcastManager = null;
    private BluetoothSerialPortManager mBTManager = null;
    private PacketReceivedListener mPacketReceivedListener;

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
        mBroadcastManager.registerReceiver(new BTConnectReceiver(), new IntentFilter(ACTION_TARGET_CONNECT));
    }

    protected BluetoothSerialPortManager getBTManager() {
        return mBTManager;
    }

    protected void setTargetName(String name) {
        mTargetName = name;
    }

    public PacketReceivedListener getPacketReceivedListener() {
        return mPacketReceivedListener;
    }

    public void setPacketReceivedListener(PacketReceivedListener listener) {
        mPacketReceivedListener = listener;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        LCAT.d(this, "created");
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        registerReceivers();
        mBTManager = new BluetoothSerialPortManager(this);
    }

    protected void startConnection() {
        mBTManager.connectName(mTargetName);
    }

    public void send(byte[] data) {
        mBTManager.send(data);
    }

    @Override
    public void onDestroy() {
        mBTManager.disconnect();
        super.onDestroy();
    }

    public interface PacketReceivedListener {
        void onReceived(byte[] packet);
    }

    private class BTSendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra(KEY_DATA);
            LCAT.d(this, "send: " + ByteUtils.bytes2hex(data));
            send(data);
        }
    }

    private class BTConnectReceiver extends BroadcastReceiver {
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

    public class BaseBTServiceBinder extends Binder {
        public BaseBluetoothService getService() {
            return BaseBluetoothService.this;
        }
    }

}
