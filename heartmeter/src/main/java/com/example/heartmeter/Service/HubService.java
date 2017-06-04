package com.example.heartmeter.Service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.heartmeter.Data.Event;
import com.example.heartmeter.Data.EventData;
import com.example.heartmeter.Data.SensorData;
import com.example.heartmeter.R;
import com.example.heartmeter.storage.SQLiteManager;
import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.service.BaseHubService;
import com.presisco.shared.utils.ByteUtils;
import com.presisco.shared.utils.LCAT;
import com.presisco.shared.utils.ValueUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;

public class HubService extends BaseHubService implements BaseBluetoothService.PacketReceivedListener {

    public static final String KEY_INSTRUCTION = "INSTRUCTION";
    public static final String KEY_SAMPLE_RATE = "SAMPLE_RATE";
    public static final String KEY_FILTER = "FILTER";

    public static final String ACTION_HEART_RATE_REDUCED = "HEART_RATE_REDUCED";
    public static final String ACTION_HEART_RATE_VOLUME = "HEART_RATE_VOLUME";
    public static final String ACTION_ECG = "ECG";
    public static final String ACTION_PROBE_DETACH = "PROBE_DETACH";

    public static final String ACTION_START_EVENT = "START_EVENT";
    public static final String KEY_EVENT_TYPE = "EVENT_TYPE";
    public static final String KEY_ANALYSE_RATE = "ANALYSE_RATE";
    public static final String KEY_START_TIME = "START_TIME";

    public static final int TYPE_HEARTRATE = 0;
    public static final int TYPE_ECG = 1;
    public static final byte SAMPLE_RATE_100HZ = 0x01;
    public static final byte SAMPLE_RATE_250HZ = 0x02;
    public static final byte SAMPLE_RATE_500HZ = 0x03;
    public static final byte FILTER_OFF = 0x01;
    public static final byte FILTER_ON = 0x02;
    public static final byte SEND_START = (byte) 0x91;
    public static final byte CHANGE_SAMPLE_RATE = (byte) 0x87;
    public static final byte CHANGE_FILTER = (byte) 0x85;
    public static final byte SEND_STOP = (byte) 0x93;
    public static final byte UNKNOWN = 0x00;
    private static final int MAX_SAMPLE_RATE = 500;
    private static final int HEAD_DATA = 0xA0;
    private static final int HEAD_RESPONSE = 0xC0;

    private static final int ROOF_HEARTRATE = 250;
    private static final int ROOF_ECG = 1500;
    private static final int FLOOR_HEARTRATE = 0;
    private static final int FLOOR_ECG = 0;

    private static final int ID_PROBE_DETACH = 0;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
    int mInstructionIdCounter = 1;
    int mSampleRate = 100;
    BTServiceConnection mConnection = new BTServiceConnection();
    BaseBluetoothService mBTService;
    private SQLiteManager mDataManager;
    private int mAnalyseRate = 1;
    private int mAnalyseGroupSize = 100 / mAnalyseRate;
    private SensorData[] raw_data;
    private int[] raw_heart_rate;
    private int[] raw_ecg;
    private int data_packet_counter = 0;
    private long current_event_id = -1;
    private int analyse_group_counter = 0;

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
     * 初始化hubservice
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //registerLocalReceiver(new BTServiceReceiver(), new IntentFilter(BaseBluetoothService.ACTION_TARGET_DATA_RECEIVED));
        //初始化广播响应的三个标签
        HubHostReceiver receiver = new HubHostReceiver();
        registerLocalReceiver(receiver, new IntentFilter(ACTION_SEND_INSTRUCTION));
        registerLocalReceiver(receiver, new IntentFilter(ACTION_START_EVENT));
        registerLocalReceiver(receiver, new IntentFilter(ACTION_STOP_EVENT));
        //链接蓝牙服务
        bindService(new Intent(this, BTService.class), mConnection, Context.BIND_AUTO_CREATE);
        //链接数据库
        mDataManager = new SQLiteManager(this);
    }

    @Override
    public void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    /**
     * 生成设备控制指令
     *
     * @param intent 指令数据
     */
    private void sendInstruction(Intent intent) {
        //组建命令请求帧格式包
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
                instruction[5] = intent.getByteExtra(KEY_SAMPLE_RATE, SAMPLE_RATE_100HZ);
                switch (intent.getByteExtra(KEY_SAMPLE_RATE, SAMPLE_RATE_100HZ)) {
                    //设定采样率
                    case SAMPLE_RATE_100HZ:
                        LCAT.d(this, "sample rate change to 100");
                        mSampleRate = 100;
                        break;
                    case SAMPLE_RATE_250HZ:
                        LCAT.d(this, "sample rate change to 250");
                        mSampleRate = 250;
                        break;
                    case SAMPLE_RATE_500HZ:
                        LCAT.d(this, "sample rate change to 500");
                        mSampleRate = 500;
                        break;
                }
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

    @Override
    protected void analyseGroup() {
        int error = 0;
        int heart_rate_sum = 0;
        int valid_heart_rate_count = 0;
        //对接收到的数据进行筛选
        for (int i = 0; i < mAnalyseGroupSize; ++i) {
            //是否脱落
            if (raw_data[i].la_detach
                    || raw_data[i].ra_detach
                    || raw_data[i].status != SensorData.STATUS_NORMAL) {
                error++;
            } else {
                if (ValueUtils.inLimit(raw_data[i].heart_rate, ROOF_HEARTRATE, FLOOR_HEARTRATE)) {
                    heart_rate_sum += raw_data[i].heart_rate;
                    valid_heart_rate_count++;
                }
                //对心电图数据进行筛选，选取在roof和floor之间的值
                raw_ecg[i] = ValueUtils.limit(raw_ecg[i], ROOF_ECG, FLOOR_ECG);
            }
        }
        //对错误率进行筛选 标准为20%
        if (error < mAnalyseGroupSize / 5) {
            if (valid_heart_rate_count == 0) {
                valid_heart_rate_count = 1;
            }
            //广播发送心电数据
            broadcast(ACTION_ECG, raw_ecg);
            //广播发送心率数据，处理心率数据的方法是求平均
            broadcast(ACTION_HEART_RATE_REDUCED, heart_rate_sum / valid_heart_rate_count);
            EventData data = new EventData();
            //录入事件到data对象
            data.event_id = current_event_id;
            //录入心率到data对象
            data.heart_rate = heart_rate_sum / valid_heart_rate_count;
            //录入事件发生时间点到data对象
            data.offset_time = analyse_group_counter;
            //写入数据库
            mDataManager.addDataToEvent(data);
            analyse_group_counter++;
        } else {
            //向屏幕发送信息
            sendNotification(ID_PROBE_DETACH, R.drawable.ic_launcher, "探头脱落", "确保探头连接到手臂上");
            broadcast(ACTION_PROBE_DETACH);
            stopListening();
        }
    }

    @Override
    public void onPacketReceived(byte[] packet) {
        if (ByteUtils.byteHighMatch(packet[0], HEAD_RESPONSE)) {
            LCAT.d(this, "receivedByte response packet: " + ByteUtils.bytes2hex(packet));
        } else {
            //判断数据包数量是否满足采样数量
            if (data_packet_counter >= mSampleRate) {
                analyseGroup();
                data_packet_counter = 0;
            }
            //调用sensordata函数，把数组类化
            SensorData data = SensorData.parseDataPacket(packet);
            //把未分析过的数据缓存，以便分析
            raw_data[data_packet_counter] = data;
            raw_heart_rate[data_packet_counter] = data.heart_rate;
            raw_ecg[data_packet_counter] = data.ecg;
            data_packet_counter++;
        }
    }

    private void startListening() {
        data_packet_counter = 0;
        analyse_group_counter = 0;
    }

    private void stopListening() {
        data_packet_counter = 0;
        analyse_group_counter = 0;
    }

    //广播接收站函数
    private class HubHostReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LCAT.d(this, "broadcast received: " + intent.getAction());
            switch (intent.getAction()) {
                //向蓝牙发送指令标签
                case ACTION_SEND_INSTRUCTION:
                    sendInstruction(intent);
                    break;
                //停止事件标签
                case ACTION_STOP_EVENT:
                    sendInstruction(new Intent().putExtra(KEY_INSTRUCTION, SEND_STOP));
                    stopListening();
                    break;
                //开始事件标签
                case ACTION_START_EVENT:
                    //确定分组大小，采样率/分析率
                    mAnalyseGroupSize = mSampleRate / mAnalyseRate;
                    //在数据库里创建新的事件
                    Event event = new Event(intent.getStringExtra(KEY_EVENT_TYPE));
                    //获取新事件的id
                    current_event_id = mDataManager.addEvent(event);
                    //开始测量
                    new StartMeasureTask().executeOnExecutor(Executors.newSingleThreadExecutor());
                    startListening();
                    break;
            }
        }
    }

    private class StartMeasureTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                //发送设置采样率指令
                sendInstruction(new Intent()
                        .putExtra(KEY_INSTRUCTION, CHANGE_SAMPLE_RATE)
                        .putExtra(KEY_SAMPLE_RATE, SAMPLE_RATE_100HZ));
                Thread.sleep(500);
                //发送设置滤波指令
                sendInstruction(new Intent()
                        .putExtra(KEY_INSTRUCTION, CHANGE_FILTER)
                        .putExtra(KEY_FILTER, FILTER_ON));
                Thread.sleep(500);
                //发送开始指令
                sendInstruction(new Intent()
                        .putExtra(KEY_INSTRUCTION, SEND_START));
                //初始化缓存数据
                data_packet_counter = 0;
                raw_data = new SensorData[mSampleRate];
                raw_ecg = new int[mSampleRate];
                raw_heart_rate = new int[mSampleRate];
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //建立蓝牙通讯连接
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
