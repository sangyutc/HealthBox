package com.presisco.boxmeter.UI.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.presisco.boxmeter.R;
import com.presisco.boxmeter.Service.HubService;

import java.util.concurrent.Executors;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new IgniteTask().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private class IgniteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            startService(new Intent(WelcomeActivity.this, HubService.class));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
