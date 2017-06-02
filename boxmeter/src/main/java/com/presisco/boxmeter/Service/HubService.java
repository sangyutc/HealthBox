package com.presisco.boxmeter.Service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.Data.EventData;
import com.presisco.boxmeter.Data.SensorData;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.storage.SQLiteManager;
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

    public static final int SAMPLE_RATE_MAX = 100;

    private static final int ID_PROBE_TIMEOUT = 0;

    private static final int ROOF_SPO2H = 99;
    private static final int ROOF_PULSE = 250;
    private static final int FLOOR_SPO2H = 25;
    private static final int FLOOR_PULSE = 0;

    BTServiceConnection mConnection = new BTServiceConnection();
    BaseBluetoothService mBTService;

    private int mAnalyseRate = 1;
    private int mAnalyseGroupSize = SAMPLE_RATE_MAX / mAnalyseRate;
    private SensorData[] raw_data = new SensorData[mAnalyseGroupSize];
    private int[] spo2h_volume = new int[mAnalyseGroupSize];
    private int[] pulse_volume = new int[mAnalyseGroupSize];
    private int data_packet_counter = 0;
    private int analyse_group_counter = 0;
    private boolean is_listening = false;
    private long current_event_id = -1;
    private SQLiteManager mDataManager;

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
        mDataManager = new SQLiteManager(this);
        HubHostReceiver receiver = new HubHostReceiver();
        registerLocalReceiver(receiver, new IntentFilter(ACTION_START_EVENT));
        registerLocalReceiver(receiver, new IntentFilter(ACTION_STOP_EVENT));
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
        int valid_spo2h_count = 0;
        int pulse_sum = 0;
        int valid_pulse_count = 0;
        for (int i = 0; i < mAnalyseGroupSize; ++i) {
            if (raw_data[i].search_timeout) {
                probe_timeout++;
            } else {
                if (ValueUtils.inLimit(raw_data[i].spo2h, ROOF_SPO2H, FLOOR_SPO2H)) {
                    spo2h_sum += raw_data[i].spo2h;
                    valid_spo2h_count++;
                }
                if (ValueUtils.inLimit(raw_data[i].pulse_rate, ROOF_PULSE, FLOOR_PULSE)) {
                    pulse_sum += raw_data[i].pulse_rate;
                    valid_pulse_count++;
                }
                spo2h_volume[i] = ValueUtils.limit(spo2h_volume[i], ROOF_SPO2H, FLOOR_SPO2H);
            }
        }

        if (probe_timeout < mAnalyseGroupSize / 5) {
            int cooked_spo2h = spo2h_sum / valid_spo2h_count;
            int cooked_pulse = pulse_sum / valid_pulse_count;

            broadcast(ACTION_SPO2H_VOLUME, cooked_spo2h);
            broadcast(ACTION_PULSE_VOLUME, pulse_volume);
            broadcast(ACTION_SPO2H, cooked_spo2h);
            broadcast(ACTION_PULSE, cooked_pulse);

            EventData data = new EventData();
            data.event_id = current_event_id;
            data.spo2h = cooked_spo2h;
            data.offset_time = analyse_group_counter;

            mDataManager.addDataToEvent(data);

            analyse_group_counter++;
        } else {
            sendNotification(ID_PROBE_TIMEOUT, R.drawable.ic_launcher, "探头超时", "确保探头已夹在手指上");
            broadcast(ACTION_PROBE_TIMEOUT);
            stopListening();
        }
    }

    @Override
    public void onPacketReceived(byte[] packet) {
        if (!is_listening) {
            return;
        }
        if (data_packet_counter >= mAnalyseGroupSize) {
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

    private void startListening() {
        is_listening = true;
        data_packet_counter = 0;
        analyse_group_counter = 0;
    }

    private void stopListening() {
        is_listening = false;
        data_packet_counter = 0;
        analyse_group_counter = 0;
    }

    private class HubHostReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LCAT.d(this, "broadcast received: " + intent.getAction());
            switch (intent.getAction()) {
                case ACTION_STOP_EVENT:
                    stopListening();
                    break;
                case ACTION_START_EVENT:
                    Event event = new Event(intent.getStringExtra(KEY_EVENT_TYPE));
                    mAnalyseGroupSize = SAMPLE_RATE_MAX / mAnalyseRate;
                    current_event_id = mDataManager.addEvent(event);
                    startListening();
                    break;
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
