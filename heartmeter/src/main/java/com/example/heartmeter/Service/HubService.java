package com.example.heartmeter.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.heartmeter.Data.SensorData;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.service.BaseHubService;
import com.presisco.shared.utils.ByteUtils;

import java.util.ArrayList;

public class HubService extends BaseHubService {

    public static final String KEY_INSTRUCTION = "INSTRUCTION";
    public static final String KEY_SAMPLE_RATE = "SAMPLE_RATE";
    public static final String KEY_FILTER = "FILTER";

    public static final byte SAMPLERATE_100HZ = 0x01;
    public static final byte SAMPLERATE_250HZ = 0x02;
    public static final byte SAMPLERATE_500HZ = 0x03;

    public static final byte FILTER_OFF = 0x01;
    public static final byte FILTER_ON = 0x02;

    public static final byte SEND_START = (byte) 0x91;
    public static final byte CHANGE_SAMPLE_RATE = (byte) 0x87;
    public static final byte CHANGE_FILTER = (byte) 0x85;
    public static final byte SEND_STOP = (byte) 0x93;
    public static final byte UNKNOWN = 0x00;

    private static final int HEAD_DATA = 0xA0;
    private static final int HEAD_RESPONSE = 0xC0;

    int mInstructionIdCounter = 1;

    public HubService() {

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
        super.onCreate();
        registerReceiver(new BTServiceReceiver(), new IntentFilter(BaseBluetoothService.ACTION_TARGET_DATA_RECEIVED));
        registerReceiver(new HubReceiver(), new IntentFilter(ACTION_SEND_INSTRUCTION));
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
     * 生成设备控制指令
     *
     * @param intent 指令数据
     */
    private void sendInstruction(Intent intent) {
        byte[] instruction = new byte[7];
        instruction[0] = (byte) (0x80 | mInstructionIdCounter);
        instruction[1] = 0x01;
        instruction[2] = 0x00;
        instruction[3] = 0x02;
        instruction[4] = intent.getByteExtra(KEY_INSTRUCTION, UNKNOWN);
        switch (instruction[4]) {
            case SEND_START:
            case SEND_STOP:
                instruction[5] = 0x00;
                break;
            case CHANGE_SAMPLE_RATE:
                instruction[5] = intent.getByteExtra(KEY_SAMPLE_RATE, SAMPLERATE_100HZ);
                break;
            case CHANGE_FILTER:
                instruction[5] = intent.getByteExtra(KEY_FILTER, FILTER_ON);
                break;
            default:
                return;
        }
        instruction[6] = ByteUtils.getXOR(instruction, 6);

        sendInstruction(instruction);

        if (mInstructionIdCounter == 0x0F) {
            mInstructionIdCounter = 0;
        } else {
            mInstructionIdCounter++;
        }
    }

    private boolean isValidData(SensorData data) {
        return data.status == SensorData.STATUS_NORMAL;
    }

    private boolean need2Dump(SensorData data) {
        return !(data.heart_rate > 20 && data.heart_rate < 180);
    }

    private int reduce(ArrayList<Integer> data) {

        return 0;
    }

    private class BTServiceReceiver extends BroadcastReceiver {
        private ArrayList<Integer> raw_data;
        private ArrayList<Integer> filtered_data;
        private boolean receiving_data = false;
        private int data_packet_counter = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] packet = intent.getByteArrayExtra(BaseBluetoothService.KEY_DATA);
            if (ByteUtils.byteHighMatch(packet[0], HEAD_RESPONSE)) {

            } else {
                data_packet_counter++;
                SensorData data = SensorData.parseDataPacket(packet);
                receiving_data = true;
                if (!isValidData(data)) {
                    return;
                }
                raw_data.add(data.heart_rate);
                if (need2Dump(data)) {
                    return;
                }
                filtered_data.add(data.heart_rate);
                if (data_packet_counter == 100) {
                    broadcastReduced(reduce(filtered_data));
                    broadcastFiltered((Integer[]) filtered_data.toArray());
                    broadcastRaw((Integer[]) raw_data.toArray());
                }
            }
        }

    }

    private class HubReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_SEND_INSTRUCTION:
                    sendInstruction(intent);
                    break;
            }
        }
    }

}
