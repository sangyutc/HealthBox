package com.presisco.shared.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.presisco.shared.data.BaseEvent;
import com.presisco.shared.network.Constant;

/**
 * Created by presisco on 2017/5/11.
 */

public class UploadEventsRequest extends StringRequest {
    private BaseEvent[] events;

    /**
     * Creates a new GET request.
     *
     * @param events        data to upload
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public UploadEventsRequest(BaseEvent[] events, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(null, listener, errorListener);
    }

    /**
     * Return the method for this request.  Can be one of the values in {@link Method}.
     */
    @Override
    public int getMethod() {
        return Method.POST;
    }

    /**
     * Returns the URL of this request.
     */
    @Override
    public String getUrl() {
        return Constant.HOST_ADDRESS + Constant.PATH_UPLOAD_EVENTS;
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     * <p>
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    public byte[] getBody() throws AuthFailureError {

        return super.getBody();
    }
}
