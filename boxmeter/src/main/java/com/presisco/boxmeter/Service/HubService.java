package com.presisco.boxmeter.Service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.presisco.boxmeter.Data.SensorData;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.service.BaseHubService;
import com.presisco.shared.utils.LCAT;

public class HubService extends BaseHubService implements BaseBluetoothService.PacketReceivedListener {
    public static final int SEND_START = 0;
    public static final int SEND_STOP = 1;
    public static final String ACTION_SPO2H = "SPO2H";
    public static final String ACTION_PULSE = "PULSE";
    public static final String ACTION_SPO2H_VOLUME = "SPO2H_VOLUME";
    public static final String ACTION_PULSE_VOLUME = "PULSE_VOLUME";
    private static final int SAMPLE_RATE = 100;
    BTServiceConnection mConnection = new BTServiceConnection();
    BaseBluetoothService mBTService;
    private SensorData[] raw_data = new SensorData[SAMPLE_RATE];
    private int[] spo2h_volume = new int[SAMPLE_RATE];
    private int[] pulse_volume = new int[SAMPLE_RATE];
    private int data_packet_counter = 0;
    private boolean is_listening = false;

    public HubService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //registerLocalReceiver(new BTServiceReceiver(), new IntentFilter(BaseBluetoothService.ACTION_TARGET_DATA_RECEIVED));
        registerLocalReceiver(new HubHostReceiver(), new IntentFilter(ACTION_SEND_INSTRUCTION));
        bindService(new Intent(this, BTService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onReceived(byte[] packet) {
        if (!is_listening) {
            return;
        }
        if (data_packet_counter >= SAMPLE_RATE) {
            broadcast(ACTION_SPO2H_VOLUME, spo2h_volume);
            broadcast(ACTION_PULSE_VOLUME, pulse_volume);
            broadcast(ACTION_SPO2H, raw_data[0].spo2h);
            broadcast(ACTION_PULSE, raw_data[0].pulse_rate);
            data_packet_counter = 0;
        } else {
            SensorData data = SensorData.parseDataPacket(packet);
            raw_data[data_packet_counter] = data;
            spo2h_volume[data_packet_counter] = data.spo2h_volume;
            pulse_volume[data_packet_counter] = data.pulse_volume;
            data_packet_counter++;
        }
    }

    private class HubHostReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LCAT.d(this, "broadcast received: " + intent.getAction());
            if (intent.getAction() == ACTION_SEND_INSTRUCTION) {
                is_listening = intent.getIntExtra(KEY_DATA, SEND_START) == SEND_START;
                data_packet_counter = 0;
            }
        }
    }

    private class BTServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBTService = ((BaseBluetoothService.BaseBTServiceBinder) service).getService();
            mBTService.setPacketReceivedListener(HubService.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBTService = null;
        }
    }

}
