/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 03/05/23, 2:40 PM
 *
 */

package com.qamp.app;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qamp.app.uihelper.Utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSendClass {

//    private static String BASE_URL = "https://dcore.qampservices.in/v1/notification-service/notification/send";
//    private static String server_key = "";
    private static String BASE_URL = "https://dcore.qampservices.in/v1/notification-service/notification/mobilenumber/send";
    private static String server_key = "";

    public static void pushNotifications(Context context, String destination_id,
                                         String notification_title, String notification_body) {

        if (destination_id.length()==12){
            destination_id = destination_id.substring(2,12);
        }
//        Toast.makeText(context, ""+destination_id, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, ""+notification_title, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, ""+notification_body, Toast.LENGTH_SHORT).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            //shivam user id - da544db5-2ddb-4cbb-8c38-dae3dc79e0c4
            //shivam2 user id - 2b09782a-1941-4bf9-a6e4-408516c304f5
            //json.put("userId", "2b09782a-1941-4bf9-a6e4-408516c304f5");
            json.put("mobileNumber",destination_id);
            json.put("countryCode","91");
            json.put("title", notification_title);
            json.put("body", notification_body);
            json.put("region", "TEST");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL
                    , json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, "Here Response", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(context, ""+error.toString(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, "Here Volley", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
//                    return super.getHeaders();
                    Map<String, String> params = new HashMap<>();
                    params.put("user-session-token", AppConfig.getConfig().token);
                    params.put("content-type", "application/json");
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
//            Toast.makeText(context, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }

}
