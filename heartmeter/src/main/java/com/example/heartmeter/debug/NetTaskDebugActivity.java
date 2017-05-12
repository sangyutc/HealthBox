package com.example.heartmeter.debug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.heartmeter.R;
import com.example.heartmeter.Service.UploadService;

public class NetTaskDebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_task_debug);
    }

    public void onUpload(View v) {
        startService(new Intent(this, UploadService.class));
    }
}
