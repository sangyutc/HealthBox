package com.presisco.shared.utils;

import java.util.List;

/**
 * Created by presisco on 2017/4/8.
 */

public class ByteUtils {
    public static int[] IntegerList2intArray(List<Integer> src) {
        int[] converted = new int[src.size()];
        for (int i = 0; i < src.size(); ++i) {
            converted[i] = src.get(i);
        }
        return converted;
    }

    public static float[] IntegerList2floatArray(List<Integer> src) {
        float[] converted = new float[src.size()];
        for (int i = 0; i < src.size(); ++i) {
            converted[i] = src.get(i);
        }
        return converted;
    }

    public static float[] intArray2floatArray(int[] src) {
        float[] converted = new float[src.length];
        for (int i = 0; i < src.length; ++i) {
            converted[i] = src[i];
        }
        return converted;
    }

    public static int byte2int_unsigned(final byte src) {
        int result = src & 0x000000FF;
        return result;
    }

    public static String byte2hex(final byte data) {
        int lbyte = data & 0x0F;
        int hbyte = (data & 0xF0) >>> 4;
        int index = (hbyte << 4) + lbyte;
        String mid = Integer.toHexString(index);
        if (mid.length() == 1)
            return "0" + mid;
        else
            return mid;
    }

    public static String bytes2hex(final byte[] data) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(byte2hex(data[i]) + " ");
        }
        return result.toString();
    }

    public static String byte2bin(final byte data) {
        StringBuilder result = new StringBuilder("");
        int cursor = 0b10000000;
        for (int i = 0; i < 8; ++i) {
            int bit = data & cursor;
            if (bit > 0) {
                result.append("1");
            } else {
                result.append("0");
            }
            cursor = cursor >>> 1;
        }
        return result.toString();
    }

    public static byte getXOR(byte[] data) {
        return getXOR(data, data.length);
    }

    public static byte getXOR(byte[] data, int length) {
        int xor = data[0] ^ data[1];
        for (int i = 2; i < length; ++i) {
            xor = data[i] ^ xor;
        }
        return (byte) xor;
    }

    public static String bytes2String(final byte[] data) {
        return new String(data);
    }

    public static boolean byteHighMatch(byte data, int mask) {
        int extended = data & 0xF0;
        return (extended ^ mask) == 0;
    }

    public static boolean byteLowMatch(byte data, int mask) {
        int extended = data & 0x0F;
        return (extended ^ mask) == 0;
    }

    public static boolean getBit(byte data, int pos) {
        int cursor = 0x01 << pos;
        return (data & cursor) > 0;
    }

    public static int getHigherByte(byte data) {
        return data & 0xF0;
    }

    public static int getLowerByte(byte data) {
        return data & 0x0F;
    }
}
