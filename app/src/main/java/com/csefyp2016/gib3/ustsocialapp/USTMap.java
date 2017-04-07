package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.content.Context;


public class USTMap extends Fragment {

    private String SSID;

    private TextView location;
    private Button instantChatroom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        instantChatroom = (Button) view.findViewById(R.id.b_map_instant_chatroom);
        instantChatroom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstantChatRoom.class);
                startActivity(intent);
            }
        });

        location = (TextView) view.findViewById(R.id.view_map_locaion_des);
        WifiManager wifiMgr = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        SSID = wifiInfo.getBSSID();
        return view;
    }
}
