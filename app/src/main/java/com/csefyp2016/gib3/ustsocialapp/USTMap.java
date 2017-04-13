package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER;


public class USTMap extends Fragment {

    private String ssid;
    private String pastssid;
    private String mapName;
    private Integer smallMapX;
    private Integer smallMapY;
    private String mapLocation;
    private Integer userID;
    private Integer[] peopleID;
    private Timer ssidTimer;
    private Boolean enableTimer = false;
    private Integer screenWidth;
    private Integer screenHeight;

    private static final String getMapLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getMapLocation.php";
    private static final String getLocationPeopleURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getLocationPeople.php";
    private static final String deleteUserLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/deleteUserLocation.php";
    private static final String updateUserLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/updateUserLocation.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private SharedPreferences sharedPreferences;

    private LinearLayout mapLayout;
    private TextView location;
    private Button instantChatroom;
    private SurfaceView surfaceView;
    private ImageView imageView;
    private ImageView smallUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        location = (TextView) view.findViewById(R.id.textView_map_location);
        instantChatroom = (Button) view.findViewById(R.id.b_map_instant_chatroom);
        instantChatroom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstantChatRoom.class);
                intent.putExtra("region", "region_a");
                startActivity(intent);
            }
        });

        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView_map);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, screenHeight - 900);
        surfaceView.setLayoutParams(params);
        imageView = (ImageView) view.findViewById(R.id.image_map_location);
        smallUser = (ImageView) view.findViewById(R.id.image_small_user);

        WifiManager wifiMgr = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        getSelfID();
        ssid = "84:B8:02:F7:10:D";
        getMapLocation();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("start");
            enableTimer = true;
            ssidTimer = new Timer();
            ssidTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {System.out.println("timer");
                    //ssid = wifiInfo.getBSSID();
                    if(!ssid.equals(pastssid)){
                        pastssid = ssid;
                        //getMapLocation();
                    }
                }
            }, 0, 1000);
        } else {
            System.out.println("end");
            if(enableTimer == true) {
                ssidTimer.cancel();
                deleteUserLocation();
                enableTimer = false;
            }
        }
    }

    private void getMapLocation() {
        request = new StringRequest(Request.Method.POST, getMapLocationURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String[] mapphp = response.split("~");
                mapName = mapphp[0];
                smallMapX = Integer.parseInt(mapphp[1]);
                smallMapY = Integer.parseInt(mapphp[2]);
                mapLocation = mapphp[3];
                location.setText(mapLocation);
                getLocationPeople();
                setMap();
                updateUserLocation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("ssid", ssid);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void getLocationPeople(){
        request = new StringRequest(Request.Method.POST, getLocationPeopleURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //location.setText(response);
                String[] people = response.split(",");
                peopleID = new Integer[people.length];
                for(Integer i = 0; i< people.length; i++){
                    if(people[i] != "") {
                        peopleID[i] = Integer.parseInt(people[i]);
                    }
                }
                setPeople();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("location", mapLocation);
                hashMap.put("map", mapName);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void getSelfID() {
        sharedPreferences = getContext().getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        userID = Integer.parseInt(sharedPreferences.getString("ID", ""));
    }

    private void updateUserLocation() {
        request = new StringRequest(Request.Method.POST, updateUserLocationURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //location.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", userID.toString());
                hashMap.put("location", mapLocation);
                hashMap.put("map", mapName);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void deleteUserLocation() {
        request = new StringRequest(Request.Method.POST, deleteUserLocationURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //location.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", userID.toString());
                hashMap.put("map", mapName);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void setMap() {
        switch (mapName) {
            case "A2":
                imageView.setImageResource(R.drawable.a2);
                break;
        }
        imageView.setScaleX(15);
        imageView.setScaleY(15);
        imageView.setX(smallMapX);
        imageView.setY(smallMapY);
        setSmallUser();
    }

    private void setPeople() {

    }

    private void setSmallUser() {
        smallUser.setImageResource(R.drawable.location);
        ViewGroup.MarginLayoutParams smallUserPlace = new ViewGroup.MarginLayoutParams(60, 60);
        smallUserPlace.setMargins(imageView.getWidth()/2+60, imageView.getHeight()/2-60, 0, 0);
        FrameLayout.LayoutParams smallUserSize = new FrameLayout.LayoutParams(smallUserPlace);
        smallUser.setLayoutParams(smallUserSize);
        smallUser.requestLayout();
    }
}
