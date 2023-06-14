/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Bhushan Udawant on 22/3/22.
 */
public class UTF8JsonObjectRequest extends JsonRequest<JSONObject> {

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param requestBody   A {@link String} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UTF8JsonObjectRequest(int method, String url, String requestBody,
                                 Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener,
                errorListener);
    }

    /**
     * Creates a new request.
     *
     * @param url           URL to fetch the JSON from
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UTF8JsonObjectRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UTF8JsonObjectRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
    }

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UTF8JsonObjectRequest(int method, String url, JSONObject jsonRequest,
                                 Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #UTF8JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     */
    public UTF8JsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String utf8String = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
