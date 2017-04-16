package com.presisco.shared.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import com.presisco.shared.utils.LCAT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by presisco on 2017/4/8.
 */

public class BluetoothSerialPortManager {
    public static final int DEV_TYPE_BYTE = 0;
    public static final int DEV_TYPE_BLOCK = 1;
    public static final UUID BT_UUID_SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final Integer RESULT_CODE_EXIT = 0;
    private static final Integer RESULT_CODE_INTERRUPTED = -1;
    private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 512;

    Context mContext;
    BluetoothAdapter mBTAdapter;
    BluetoothDevice mBTDevice;
    BluetoothSocket mBTSocket;
    StateListener mBTStateListener;
    ByteListener mByteListener;
    CharListener mCharListener;
    BlockListener mBlockListener;

    int mReceiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
    OutputStream mOutputStream;

    UUID mUUID = BT_UUID_SERIAL;
    int mDevType = DEV_TYPE_BYTE;
    ConnectTask mConnectTask;
    ReceiverTask mReceiverTask;

    public BluetoothSerialPortManager(Context context) {
        mContext = context;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothSerialPortManager newInstance(Context context) {
        BluetoothSerialPortManager newManager = new BluetoothSerialPortManager(context);
        if (newManager.mBTAdapter == null) {
            newManager = null;
        }
        return newManager;
    }

    public boolean isBTEnabled() {
        return mBTAdapter.isEnabled();
    }

    public boolean devicePaired(String name) {
        for (BluetoothDevice device : mBTAdapter.getBondedDevices()) {
            String devName = device.getName();
            if (devName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getAddress(String name) {
        for (BluetoothDevice device : mBTAdapter.getBondedDevices()) {
            String devName = device.getName();
            String devAddr = device.getAddress();
            if (devName.equals(name)) {
                return devAddr;
            }
        }
        return null;
    }

    public void setTargetDeviceType(int type) {
        mDevType = type;
    }

    public void setListeners(Object listeners) {
        if (listeners instanceof StateListener) {
            setBluetoothStateListener((StateListener) listeners);
        }
        if (listeners instanceof ByteListener) {
            mByteListener = (ByteListener) listeners;
        }
        if (listeners instanceof CharListener) {
            mCharListener = (CharListener) listeners;
        }
    }

    public void setBluetoothStateListener(StateListener listener) {
        mBTStateListener = listener;
        BroadcastReceiver br = new BTBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(br, filter);
    }

    public void setByteListener(ByteListener listener) {
        mByteListener = listener;
    }

    public void setCharListener(CharListener listener) {
        mCharListener = listener;
    }

    public void setBlockListener(BlockListener listener) {
        mBlockListener = listener;
    }

    public void setReceiveBufferSize(int size) {
        mReceiveBufferSize = size;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return mBTAdapter.getBondedDevices();
    }

    public void setUUID(UUID uuid) {
        mUUID = uuid;
    }

    public void connectAddress(String address) {
        mBTDevice = mBTAdapter.getRemoteDevice(address);
        mConnectTask = new ConnectTask();
        mConnectTask.execute(mBTDevice);
    }

    public void connectName(String name) {
        for (BluetoothDevice device : mBTAdapter.getBondedDevices()) {
            String devName = device.getName();
            if (devName.equals(name)) {
                mBTDevice = device;
                mConnectTask = new ConnectTask();
                mConnectTask.execute(mBTDevice);
                return;
            }
        }
        LCAT.d(this, "target: " + name + " not found!");
    }

    public void send(byte[] data) {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(data);
                mOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            mBTStateListener.disconnected();
        }
    }

    public void send(byte data) {
        send(new byte[]{data});
    }

    public void send(String data) {
        send(data.getBytes());
    }

    public void disconnect() {
        try {
            mOutputStream.close();
            mReceiverTask.cancel(true);
            mBTSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface StateListener {
        void connectFailed();

        void connected();

        void disconnected();

        void enabled();

        void disabled();
    }

    public interface ByteListener {
        void received(byte data);
    }

    public interface BlockListener {
        void received(byte[] data);
    }

    public interface CharListener {
        void received(String data);
    }

    private class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        @Override
        protected void onPreExecute() {
            LCAT.d(this, "prepare connecting");
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (bluetoothSocket == null) {
                LCAT.d(this, "connect failed");
                mBTStateListener.connectFailed();
                return;
            }
            LCAT.d(this, "finished connecting");
            try {
                mReceiverTask = new ReceiverTask();
                mReceiverTask.execute(bluetoothSocket.getInputStream());
                mOutputStream = bluetoothSocket.getOutputStream();
                mBTStateListener.connected();
            } catch (IOException e) {
                LCAT.d(this, "link failed");
                e.printStackTrace();
                mBTStateListener.connectFailed();
            }
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... devices) {
            LCAT.d(this, "start connecting");
            BluetoothSocket socket;
            try {
                socket = devices[0].createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                e.printStackTrace();
                mBTStateListener.connectFailed();
                return null;
            }
            mBTSocket = socket;
            mBTAdapter.cancelDiscovery();
            try {
                mBTSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mBTSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                mBTStateListener.connectFailed();
                return null;
            }
            return mBTSocket;
        }
    }

    private class ReceiverTask extends AsyncTask<InputStream, Void, Integer> {
        @Override
        protected void onPreExecute() {
            LCAT.d(this, "prepare receiving");
        }

        @Override
        protected void onPostExecute(Integer i) {
            LCAT.d(this, "closed receiving");
        }

        @Override
        protected Integer doInBackground(InputStream... params) {
            LCAT.d(this, "started receiving");
            InputStream inStream = params[0];
            Integer resultCode = RESULT_CODE_EXIT;
            try {
                switch (mDevType) {
                    case DEV_TYPE_BYTE:
                        byte byteBuffer = 0x00;
                        while (true) {
                            byteBuffer = (byte) inStream.read();
                            mByteListener.received(byteBuffer);
                        }
                    case DEV_TYPE_BLOCK:
                        byte[] blockBuffer = new byte[mReceiveBufferSize];
                        while (true) {
                            int length = inStream.read(blockBuffer);
                            byte[] packet = new byte[length];
                            for (int i = 0; i < length; ++i) {
                                packet[i] = blockBuffer[i];
                            }
                            mBlockListener.received(packet);
                        }
                }
            } catch (IOException e) {
                mBTStateListener.disconnected();
                resultCode = RESULT_CODE_INTERRUPTED;
                e.printStackTrace();
            }
            return resultCode;
        }
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                case BluetoothAdapter.STATE_OFF:
                    mBTStateListener.disabled();
                    break;
                case BluetoothAdapter.STATE_ON:
                    mBTStateListener.enabled();
                    break;
            }
        }
    }

}
