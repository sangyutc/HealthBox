package com.presisco.shared.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.presisco.shared.utils.LCAT;

public abstract class BaseHubService extends Service {
    public static final String ACTION_SEND_INSTRUCTION = "com.presisco.shared.service.SEND_INSTRUCTION";
    public static final String ACTION_RECEIVE_DATA = "com.presisco.shared.service.RECEIVE_DATA";
    public static final String ACTION_DATA_RAW = "com.presisco.shared.service.DATA_RAW";
    public static final String ACTION_DATA_FILTERED = "com.presisco.shared.service.DATA_FILTERED";
    public static final String ACTION_DATA_REDUCED = "com.presisco.shared.service.DATA_REDUCED";

    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_DATA = "DATA";

    LocalBroadcastManager mLocalBroadcastManager;

    public BaseHubService() {
    }

    protected void broadcast(int type, String action, Integer data) {
        Intent intent = new Intent(action);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_DATA, data);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    protected void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        mLocalBroadcastManager.registerReceiver(receiver, filter);
    }

    protected void broadcast(int type, String action, int[] data) {
        Intent intent = new Intent(action);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_DATA, data);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    protected void broadcastRaw(int type, int[] data) {
        broadcast(type, ACTION_DATA_RAW, data);
    }

    protected void broadcastFiltered(int type, int[] data) {
        broadcast(type, ACTION_DATA_FILTERED, data);
    }

    protected void broadcastReduced(int type, Integer data) {
        broadcast(type, ACTION_DATA_REDUCED, data);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        LCAT.d(this, "created");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called when all clients have disconnected from a particular interface
     * published by the service.  The default implementation does nothing and
     * returns false.
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return true if you would like to have the service's
     * {@link #onRebind} method later called when new clients bind to it.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Called when new clients have connected to the service, after it had
     * previously been notified that all had disconnected in its
     * {@link #onUnbind}.  This will only be called if the implementation
     * of {@link #onUnbind} was overridden to return true.
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    /**
     * 发送设备控制指令
     *
     * @param instruction 指令数据
     */
    protected void sendInstruction(byte[] instruction) {
        Intent intent = new Intent(BaseBluetoothService.ACTION_TARGET_DATA_SEND);
        intent.putExtra(BaseBluetoothService.KEY_DATA, instruction);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

}
