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
import com.presisco.boxmeter.R;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.service.BaseHubService;
import com.presisco.shared.utils.LCAT;
import com.presisco.shared.utils.ValueUtils;

public class HubService extends BaseHubService implements BaseBluetoothService.PacketReceivedListener {
    public static final int SEND_START = 0;
    public static final int SEND_STOP = 1;
    public static final String ACTION_SPO2H = "SPO2H";
    public static final String ACTION_PULSE = "PULSE";
    public static final String ACTION_SPO2H_VOLUME = "SPO2H_VOLUME";
    public static final String ACTION_PULSE_VOLUME = "PULSE_VOLUME";
    public static final String ACTION_PROBE_TIMEOUT = "PROBE_TIMEOUT";

    private static final int ID_PROBE_TIMEOUT = 0;

    private static final int ROOF_SPO2H = 99;
    private static final int ROOF_PULSE = 250;
    private static final int FLOOR_SPO2H = 25;
    private static final int FLOOR_PULSE = 0;

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
    protected void analyseGroup() {
        int probe_timeout = 0;
        int spo2h_sum = 0;
        int pulse_sum = 0;
        for (int i = 0; i < SAMPLE_RATE; ++i) {
            if (raw_data[i].search_timeout) {
                probe_timeout++;
            }

            spo2h_sum += ValueUtils.limit(raw_data[i].spo2h, ROOF_SPO2H, FLOOR_SPO2H);
            spo2h_volume[i] = ValueUtils.limit(spo2h_volume[i], ROOF_SPO2H, FLOOR_SPO2H);

            pulse_sum += ValueUtils.limit(raw_data[i].pulse_rate, ROOF_PULSE, FLOOR_PULSE);
        }

        if (probe_timeout < SAMPLE_RATE / 5) {
            broadcast(ACTION_SPO2H_VOLUME, spo2h_volume);
            broadcast(ACTION_PULSE_VOLUME, pulse_volume);
            broadcast(ACTION_SPO2H, spo2h_sum / SAMPLE_RATE);
            broadcast(ACTION_PULSE, pulse_sum / SAMPLE_RATE);
        } else {
            sendNotification(ID_PROBE_TIMEOUT, R.drawable.ic_launcher, "Probe Timeout", "Make sure there's a finger");
            broadcast(ACTION_PROBE_TIMEOUT);
        }
    }

    @Override
    public void onReceived(byte[] packet) {
        if (!is_listening) {
            return;
        }
        if (data_packet_counter >= SAMPLE_RATE) {
            analyseGroup();
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
