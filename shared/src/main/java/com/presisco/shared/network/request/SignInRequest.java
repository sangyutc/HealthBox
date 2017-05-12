package com.presisco.shared.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.presisco.shared.network.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by presisco on 2017/5/12.
 */

public class SignInRequest extends StringRequest {
    private String username;
    private String password;

    /**
     * Creates a new request.
     *
     * @param username      username
     * @param password      password
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public SignInRequest(String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(null, listener, errorListener);
        this.username = username;
        this.password = password;
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
        return Constant.HOST_ADDRESS + Constant.PATH_SIGN_IN;
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     * <p>
     * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        return params;
    }
}
