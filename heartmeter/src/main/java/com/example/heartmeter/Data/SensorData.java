package com.example.heartmeter.Data;

/**
 * Created by presisco on 2017/4/12.
 */

public class SensorData {
    public static final int STATUS_NORMAL = 0;

    public int seq;
    public int status;
    public int link_mode;
    public int ra_detach;
    public int la_detach;
    public int heart_rate;

    public SensorData() {
    }

    public static SensorData parseDataPacket(byte[] packet) {
        SensorData sensorData = new SensorData();
        sensorData.seq = packet[0] & 0x0F;
        sensorData.status = (packet[1] & 0xF0) >>> 4;
        sensorData.link_mode = (packet[1] & 0x08) >>> 3;
        sensorData.ra_detach = (packet[1] & 0x04) >>> 2;
        sensorData.la_detach = (packet[1] & 0x02) >>> 1;
        sensorData.heart_rate = (packet[1] & 0x01) << 8;
        sensorData.heart_rate += (int) packet[2] & 0xFF;
        return sensorData;
    }
}
