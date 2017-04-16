package com.presisco.boxmeter.Data;

import com.presisco.shared.utils.ByteUtils;

/**
 * Created by presisco on 2017/4/16.
 */

public class SensorData {

    public int pulse_strength;
    public boolean search_timeout;
    public boolean spo2h_drop;
    public boolean pulse_sound;
    public int spo2h_volume;
    public int pulse_volume;
    public boolean probe_error;
    public boolean search_pulse;
    public int pulse_rate;
    public int spo2h;

    public SensorData() {
    }

    public static SensorData parseDataPacket(byte[] packet) {
        SensorData sensorData = new SensorData();

        //第一个字节
        sensorData.pulse_strength = ByteUtils.getLowerByte(packet[0]);
        sensorData.search_timeout = ByteUtils.getBit(packet[0], 4);
        sensorData.spo2h_drop = ByteUtils.getBit(packet[0], 5);
        sensorData.pulse_sound = ByteUtils.getBit(packet[0], 6);

        //第二个字节
        sensorData.spo2h_volume = packet[1] & 0x7F;

        //第三个字节
        sensorData.pulse_volume = ByteUtils.getLowerByte(packet[2]);
        sensorData.probe_error = ByteUtils.getBit(packet[2], 4);
        sensorData.search_pulse = ByteUtils.getBit(packet[2], 5);
        boolean pulse_prefix = ByteUtils.getBit(packet[2], 6);

        //第四个字节
        sensorData.pulse_rate = packet[3] & 0x7F;
        if (pulse_prefix) {
            sensorData.pulse_rate += 128;
        }

        //第五个字节
        sensorData.spo2h = packet[4] & 0x7F;

        return sensorData;
    }
}
