package com.example.heartmeter.debug;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.heartmeter.R;
import com.example.heartmeter.Service.HubService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MonitorDebugActivity extends AppCompatActivity {
    private EditText mTestValueEdit;
    private TestBeacon mCurrentBeacon;
    private Executor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_debug);
        mTestValueEdit = (EditText) findViewById(R.id.editTestValue);
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void onStart(View v) {
        mCurrentBeacon = new TestBeacon();
        mCurrentBeacon.executeOnExecutor(mExecutor, Integer.parseInt(mTestValueEdit.getText().toString()));
    }

    public void onStop(View v) {
        mCurrentBeacon.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCurrentBeacon.getStatus() != AsyncTask.Status.FINISHED) {
            mCurrentBeacon.cancel(true);
        }
    }

    private class TestBeacon extends AsyncTask<Integer, Void, Void> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                int test_value = params[0];
                LocalBroadcastManager mTestBroadcast = LocalBroadcastManager.getInstance(MonitorDebugActivity.this);
                while (true) {
                    mTestBroadcast.sendBroadcast(
                            new Intent(HubService.ACTION_HEART_RATE_REDUCED)
                                    .putExtra(HubService.KEY_DATA, test_value));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
