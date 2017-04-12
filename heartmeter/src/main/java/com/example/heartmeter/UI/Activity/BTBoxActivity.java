package com.example.heartmeter.UI.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.heartmeter.R;
import com.example.heartmeter.Service.BTService;
import com.example.heartmeter.Service.HubService;
import com.presisco.shared.utils.ByteUtils;

public class BTBoxActivity extends AppCompatActivity {
    TextView mReceivedText;
    Spinner mSampleRateSpinner;
    ToggleButton mFilterToggle;
    LocalBroadcastManager mBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btbox);

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter btFilter = new IntentFilter(BTService.ACTION_TARGET_DATA_RECEIVED);
        mBroadcastManager.registerReceiver(new BTReceiver(), btFilter);

        mReceivedText = (TextView) findViewById(R.id.textReceived);
        mSampleRateSpinner = (Spinner) findViewById(R.id.spinnerSampleRate);
        mFilterToggle = (ToggleButton) findViewById(R.id.toggleFilter);

        mSampleRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * <p>Callback method to be invoked when an item in this view has been
             * selected. This callback is invoked only when the newly selected
             * position is different from the previously selected position or if
             * there was no selected item.</p>
             * <p>
             * Impelmenters can call getItemAtPosition(position) if they need to access the
             * data associated with the selected item.
             *
             * @param parent   The AdapterView where the selection happened
             * @param view     The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id       The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setAction(HubService.ACTION_SEND_INSTRUCTION);
                intent.putExtra(HubService.KEY_INSTRUCTION, HubService.CHANGE_SAMPLE_RATE);
                byte sampleRate = 0;
                switch (position) {
                    case 0:
                        sampleRate = HubService.SAMPLERATE_100HZ;
                        break;
                    case 1:
                        sampleRate = HubService.SAMPLERATE_250HZ;
                        break;
                    case 2:

                        sampleRate = HubService.SAMPLERATE_500HZ;
                        break;
                }
                intent.putExtra(HubService.KEY_SAMPLE_RATE, sampleRate);
                mBroadcastManager.sendBroadcast(intent);
            }

            /**
             * Callback method to be invoked when the selection disappears from this
             * view. The selection can disappear for instance when touch is activated
             * or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFilterToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent();
                intent.setAction(HubService.ACTION_SEND_INSTRUCTION);
                intent.putExtra(HubService.KEY_INSTRUCTION, HubService.CHANGE_FILTER);
                if (isChecked) {
                    intent.putExtra(HubService.KEY_FILTER, HubService.FILTER_ON);
                } else {
                    intent.putExtra(HubService.KEY_FILTER, HubService.FILTER_OFF);
                }
                mBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    public void onBegin(View v) {
        Intent intent = new Intent();
        intent.setAction(HubService.ACTION_SEND_INSTRUCTION);
        intent.putExtra(HubService.KEY_INSTRUCTION, HubService.SEND_START);
        mBroadcastManager.sendBroadcast(intent);
    }

    public void onStop(View v) {
        Intent intent = new Intent();
        intent.setAction(HubService.ACTION_SEND_INSTRUCTION);
        intent.putExtra(HubService.KEY_INSTRUCTION, HubService.SEND_STOP);
        mBroadcastManager.sendBroadcast(intent);
    }

    public void onClear(View v) {
        mReceivedText.setText("");
    }

    private class BTReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder("");
            byte[] data = intent.getByteArrayExtra(BTService.KEY_DATA);
            if (ByteUtils.byteHighMatch(data[0], 0xA0)) {
                sb.append("data: ");
            } else {
                sb.append("response: ");
            }
            sb.append(ByteUtils.bytes2hex(data) + "\n");
            mReceivedText.append(sb.toString());
        }
    }
}
