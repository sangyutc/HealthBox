package com.presisco.boxmeter.UI.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.presisco.boxmeter.R;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);

        findViewById(R.id.imageGood).setOnClickListener(this);
        findViewById(R.id.imageBad).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageGood:
                break;
            case R.id.imageBad:
                break;
        }
        finish();
    }
}
