package com.csefyp2016.gib3.ustsocialapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class USTMap extends Fragment {
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getMapLocation.php";
    private RequestQueue requestQueue;

    private String SSID;
    private int IP;

    private TextView location;
    private Button instantChatroom;
    private Button instantChatroom2;

    //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                SSID = wifiInfo.getBSSID();
                location.setText(SSID);
            }
        }
    };
    //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

        location = (TextView) view.findViewById(R.id.view_map_locaion_des);
        WifiManager wifiMgr = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        SSID = wifiInfo.getBSSID();
        location.setText(SSID);

        //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        view.getContext().registerReceiver(broadcastReceiver, intentFilter);
        //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //

        instantChatroom = (Button) view.findViewById(R.id.b_map_instant_chatroom);
        instantChatroom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstantChatRoom.class);
                intent.putExtra("region", "region_a");
                startActivity(intent);
            }
        });

        //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //
        instantChatroom2 = (Button) view.findViewById(R.id.b_map_instant_chatroom2);
        instantChatroom2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstantChatRoom.class);
                intent.putExtra("region", "region_b");
                startActivity(intent);
            }
        });
        //  --------------------------------------------------------------- Testing Line  --------------------------------------------------------------- //

        return view;
    }


}
