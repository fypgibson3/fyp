package com.csefyp2016.gib3.ustsocialapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/refreshToken.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private SharedPreferences sharedPreferences;

    private String id;

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        requestQueue = Volley.newRequestQueue(this);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        Boolean login = sharedPreferences.getBoolean("LOGIN", false);
        System.out.println("You are now login?? " + login);
        if (login) {
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            // Post request parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
                id = sharedPreferences.getString("ID", null);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                hashMap.put("token", token);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);

    }
}
