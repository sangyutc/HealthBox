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
    //蓝牙设备名称，直接链接
    private static final String TARGET_DEVICE_NAME = "ECG";
    private static final String DEFAULT_TARGET_ADDRESS = "98:D3:31:00:02:25";
    private static final String KEY_TARGET_ADDRESS = "TARGET_ADDRESS";
    //最小包长度
    private static final int MIN_PACKET_SIZE = 5;
    private static final int MAX_PACKET_SIZE = 250;

    private static final int INDEX_PACKET_HEAD = 0;
    private static final int INDEX_PACKET_TYPE = 1;
    private static final int INDEX_PACKET_LOADSIZE_HIGH = 2;
    private static final int INDEX_PACKET_LOADSIZE_LOW = 3;
    private static final int INDEX_OTHERS = -1;
    //指令回应包包头
    private static final int HEAD_RESPONSE = 0xC0;
    //数据包包头
    private static final int HEAD_DATA = 0xA0;
    //文字通知
    private static String[] TEXT_WARNING_TITLE;
    private static String[] TEXT_WARNING_CONTENT;
    //数据包位数指针
    private int mBTByteCursor = 0;
    //数据包规格初始化
    private int mBTPacketSize = MIN_PACKET_SIZE;
    //数据包类型初始化（在数据包前均为回应包）
    private int mPacketType = HEAD_RESPONSE;
    //定义缓存包
    private byte[] mBTBuffer = new byte[MAX_PACKET_SIZE];

    public BTService() {
    }

    /**
     * 读取通知类字符串
     */
    private void loadStringRes() {
        Resources res = getResources();
        TEXT_WARNING_TITLE = res.getStringArray(R.array.title_warning_bt);
        TEXT_WARNING_CONTENT = res.getStringArray(R.array.text_warning_bt);
    }

    /**
     * 把通知信息显示在app上
     * @param code
     */
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

    /**
     * 蓝牙模块初始化
     */
    public void setup() {
        //维护底层通讯细节
        BluetoothSerialPortManager mBTManager = getBTManager();
        //判断蓝牙是否开启。
        if (!mBTManager.isBTEnabled()) {
            sendWarnNotification(WARN_CODE_DISABLED_BT);
            return;
        }
        //是否有配对历史纪录
        if (!mBTManager.devicePaired(TARGET_DEVICE_NAME)) {
            sendWarnNotification(WARN_CODE_NOT_PAIRED);
        }
        //设置设备类型
        mBTManager.setTargetDeviceType(BluetoothSerialPortManager.DEV_TYPE_BYTE);
        //设置蓝牙端通讯接收函数
        mBTManager.setByteListener(this);
        //监听蓝牙状态
        mBTManager.setBluetoothStateListener(this);
        //设置蓝牙设备名称
        setTargetName(TARGET_DEVICE_NAME);
    }

    @Override
    /**
     * 初始化
     */
    public void onCreate() {
        super.onCreate();
        LCAT.d(this, "created");
        //读取通知字符串初始化
        loadStringRes();
        //蓝牙状态初始化
        setup();
        //开始链接
        startConnection();
    }
    /**
     *  解除hubservice里的蓝牙链接
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    /**
     *  响应hubservice里的蓝牙链接
     */
    public IBinder onBind(Intent intent) {
        IBinder binder = super.onBind(intent);
        return binder;
    }

    /**
     * 销毁函数
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * 链接失败
     */
    @Override
    public void connectFailed() {
        sendWarnNotification(WARN_CODE_CONNECT_FAILED);
    }
    /**
     * 链接成功
     */
    @Override
    public void connected() {
        //链接成功后执行这个函数，清理之前链接失败的消息
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_DISCONNECTED);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_CONNECT_FAILED);
    }
    /**
     * 断开链接，发送通知信息
     */
    @Override
    public void disconnected() {
        sendWarnNotification(WARN_CODE_DISCONNECTED);
    }
    /**
     * 蓝牙开启，撤销已经发送的通知信息
     */
    @Override
    public void enabled() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(WARN_CODE_DISABLED_BT);
    }
    /**
     * 蓝牙关闭，发送通知信息
     */
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
    public void receivedByte(byte data) {
        if (mBTByteCursor == INDEX_PACKET_HEAD) {
            //缓存数据
            mBTBuffer[INDEX_PACKET_HEAD] = data;
            //取包头高四位来判断是数据包还是应答包
            mPacketType = data & 0xF0;
            switch (mPacketType) {
                //判断是应答包改长度为7byte
                case HEAD_RESPONSE:
                    mBTPacketSize = 7;
                    break;
                //判断是数据包改长度为6byte
                case HEAD_DATA:
                    mBTPacketSize = 6;
                    break;
            }
        } else {
            //判断是否为数据包最后一位
            if (mBTByteCursor == mBTPacketSize - 1) {
                //把接受到的data存到校验位
                byte xor = data;
                //进行校验，如果成功
                if (xor == ByteUtils.getXOR(mBTBuffer, mBTPacketSize - 1)) {
                    //把缓存空间的包遍历存到新包发送
                    byte[] packet = new byte[mBTPacketSize - 1];
                    for (int i = 0; i < mBTPacketSize - 1; ++i) {
                        packet[i] = mBTBuffer[i];
                    }
                    //把byte数组传到onpacketreceived函数中
                    getPacketReceivedListener().onPacketReceived(packet);
                }
                //重置计数器
                mBTByteCursor = INDEX_PACKET_HEAD - 1;
                mBTPacketSize = MIN_PACKET_SIZE;
            } else {
                //判断发现不是数据包最后一位，存储数据到缓存空间
                mBTBuffer[mBTByteCursor] = data;
            }
        }
        mBTByteCursor++;
    }
}
