package com.presisco.boxmeter.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.presisco.shared.service.BaseBluetoothService;
import com.presisco.shared.service.BaseHubService;

public class HubService extends BaseHubService {

    public HubService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new BTServiceReceiver(), new IntentFilter(BaseBluetoothService.ACTION_TARGET_DATA_RECEIVED));
    }

    private class BTServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == BaseBluetoothService.ACTION_TARGET_DATA_RECEIVED) {
                byte[] packet = intent.getByteArrayExtra(BaseBluetoothService.KEY_DATA);
            }
        }
    }

    private class HubReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_SEND_INSTRUCTION:
                    break;
            }
        }
    }

}
