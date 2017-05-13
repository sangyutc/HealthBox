package com.example.heartmeter.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.heartmeter.R;
import com.presisco.shared.network.request.SignInRequest;

public class SignInActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {
    //所有的执行结果定义
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_PASSED = 1;

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private String username;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mUsernameEdit = (EditText) findViewById(R.id.editUsername);
        mPasswordEdit = (EditText) findViewById(R.id.editPassword);
        mQueue = Volley.newRequestQueue(this);
    }

    public void onLogin(View v) {
        username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString();
        mQueue.add(new SignInRequest(username, password, this, this));
    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        if (response.contains("failed")) {
            Toast.makeText(this, "wrong password", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("username", username);
            setResult(RESULT_PASSED, intent);
            finish();
        }
    }
}
