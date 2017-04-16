package com.example.heartmeter.Data;

import android.util.Log;

import com.presisco.shared.utils.ByteUtils;

/**
 * Created by presisco on 2017/4/12.
 */

public class SensorData {
    public static final int STATUS_NORMAL = 0;

    public int seq;
    public int status;
    public boolean link_mode;
    public boolean ra_detach;
    public boolean la_detach;
    public int heart_rate;
    public int ecg;

    public SensorData() {
    }

    public static SensorData parseDataPacket(byte[] packet) {
        SensorData sensorData = new SensorData();

        //第一个字节
        sensorData.seq = packet[0] & 0x0F;

        //第二个字节
        sensorData.status = (packet[1] & 0xF0) >>> 4;
        sensorData.link_mode = ByteUtils.getBit(packet[1], 3);
        sensorData.ra_detach = ByteUtils.getBit(packet[1], 2);
        sensorData.la_detach = ByteUtils.getBit(packet[1], 1);

        //第三，第四个字节
        sensorData.heart_rate = (packet[1] & 0x01) << 8;
        sensorData.heart_rate += (int) packet[2] & 0xFF;

        //第五个字节
        sensorData.ecg = ((packet[3] & 0xFF) << 8) + (packet[4] & 0xFF);
        if (sensorData.ecg > 0xFFFF) {
            Log.d("parseDataPacket()", "ECG Overflow");
        }
        return sensorData;
    }
}
