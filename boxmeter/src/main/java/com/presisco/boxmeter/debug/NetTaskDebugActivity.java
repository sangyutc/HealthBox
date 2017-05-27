package com.presisco.boxmeter.debug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.presisco.boxmeter.R;
import com.presisco.boxmeter.Service.UploadService;
import com.presisco.shared.network.Constant;
import com.presisco.shared.network.request.PostFormRequest;
import com.presisco.shared.utils.LCAT;

import java.util.HashMap;
import java.util.Map;

public class NetTaskDebugActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_task_debug);
        queue = Volley.newRequestQueue(this);
    }

    public void onUpload(View v) {
        startService(new Intent(this, UploadService.class));
    }

    public void onTest(View v) {
        Map<String, String> form = new HashMap<>();
        form.put("start_time", "2016/1/1");
        form.put("end_time", "2017/12/31");
        queue.add(new PostFormRequest(Constant.HOST_ADDRESS + "event_distribution.php", form, this, this));
    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        LCAT.d(this, error.toString());
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        LCAT.d(this, response);
    }
}
