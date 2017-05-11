package com.presisco.boxmeter.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.presisco.boxmeter.R;
import com.presisco.boxmeter.Service.BTService;
import com.presisco.shared.utils.ByteUtils;

public class BTBoxActivity extends AppCompatActivity {
    TextView mReceivedText;
    LocalBroadcastManager mBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btbox);

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter btFilter = new IntentFilter(BTService.ACTION_TARGET_DATA_RECEIVED);
        mBroadcastManager.registerReceiver(new BTReceiver(), btFilter);

        mReceivedText = (TextView) findViewById(R.id.textReceived);
    }

    public void onClear(View v) {
        mReceivedText.setText("");
    }

    private class BTReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder("");
            byte[] data = intent.getByteArrayExtra(BTService.KEY_DATA);
            sb.append("data: ");
            sb.append(ByteUtils.bytes2hex(data) + "\n");
            mReceivedText.append(sb.toString());
        }
    }
}
