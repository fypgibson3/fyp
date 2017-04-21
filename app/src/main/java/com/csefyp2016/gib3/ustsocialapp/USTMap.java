package com.csefyp2016.gib3.ustsocialapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;


public class USTMap extends Fragment {

    private String ssid;
    private String pastssid = "";
    private String mapName;
    private Integer smallMapX;
    private Integer smallMapY;
    private String mapLocation;
    private Integer userID;
    private Integer[] peopleID;
    private Integer[] pastpeopleID;
    private int[] peopleX;
    private int[] peopleY;
    private Timer ssidTimer;
    private Boolean enableTimer = false;
    private Integer screenWidth;
    private Integer screenHeight;
    private Boolean correctSSID = false;
    private Boolean haveLocation = false;
    private Boolean currentView = false;
    private Boolean goSetting = false;

    private static final String getMapLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getMapLocation.php";
    private static final String getLocationPeopleURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getLocationPeople.php";
    private static final String deleteUserLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/deleteUserLocation.php";
    private static final String updateUserLocationURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/updateUserLocation.php";

    private RequestQueue requestQueue;
    private StringRequest request;
    private WifiInfo wifiInfo;
    private WifiManager wifiMgr;

    private static final String loginPreference = "LoginPreference";
    private SharedPreferences sharedPreferences;

    private FrameLayout bigMapLayout;
    private TextView location;
    private Button instantChatroom;
    private ImageView bigMap;
    private TextView bigMapTitle;
    private ImageView smallMap;
    private ImageView smallUser;
    private ImageView bigUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

        view.findViewById(R.id.linearLayout_map).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(currentView) {
                    screenWidth = getView().findViewById(R.id.linearLayout_map).getWidth();
                    screenHeight = getView().findViewById(R.id.linearLayout_map).getHeight();
                    FrameLayout.LayoutParams bigMapSize = new FrameLayout.LayoutParams(screenWidth, screenHeight - instantChatroom.getHeight() - smallMap.getHeight() - 100);
                    bigMap.setLayoutParams(bigMapSize);
                    //System.out.println("width"+screenHeight);
                }
            }
        });

        location = (TextView) view.findViewById(R.id.textView_map_location);
        instantChatroom = (Button) view.findViewById(R.id.b_map_instant_chatroom);
        instantChatroom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstantChatRoom.class);
                intent.putExtra("region", mapLocation);
                startActivity(intent);
            }
        });

        bigMapLayout = (FrameLayout) view.findViewById(R.id.frame_map);
        bigMap = (ImageView) view.findViewById(R.id.image_big_map);
        bigMapTitle = (TextView) view.findViewById(R.id.textView_big_map_title);
        smallMap = (ImageView) view.findViewById(R.id.image_map_location);
        smallUser = (ImageView) view.findViewById(R.id.image_small_user);
        bigUser = (ImageView) view.findViewById(R.id.image_big_user);

        wifiMgr = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiMgr.getConnectionInfo();

        getSelfID();
        //ssid = "84:B8:02:F7:10:D";
        //getMapLocation();
        return view;
    }

    @Override
    public void onPause() {System.out.println("pause");
        super.onPause();
        currentView = false;
        pastssid = "";
        if(enableTimer == true) {
            ssidTimer.cancel();
            if(haveLocation) {
                deleteUserLocation();
            }
            enableTimer = false;
        }
    }

    @Override
    public void onResume() {System.out.println("resume");
        super.onResume();
        if(goSetting) {
            currentView = true;
            enableTimer = true;
            ssidTimer = new Timer();
            ssidTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("timer");
                    wifiMgr = (WifiManager) getView().getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    ssid = wifiInfo.getBSSID();
                    if (wifiMgr.isWifiEnabled()) {
                        ssid = ssid.substring(0, 16);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("Bssid detected: " + ssid);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        if (!ssid.equals(pastssid)) {
                            pastssid = ssid;
                            getMapLocation();
                        } else {
                            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                            System.out.println("SSID changed");
                            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                            if (correctSSID) {
                                updateUserLocation();
                                getLocationPeople();
                            } else {
                                getMapLocation();
                            }
                        }
                    } else {
                        System.out.println("no");
                        if (enableTimer == true) {
                            ssidTimer.cancel();
                            if (haveLocation) {
                                deleteUserLocation();
                            }
                            enableTimer = false;
                        }
                        ssid = "no";
                        getMapLocation();
                    }
                }
            }, 0, 1000);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("start");
            currentView = true;
            enableTimer = true;
            ssidTimer = new Timer();
            ssidTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {System.out.println("timer");
                    ssid = wifiInfo.getBSSID();
                    if(wifiMgr.isWifiEnabled()) {
                        ssid = ssid.substring(0, 16);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("Bssid detected: " + ssid);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        if (!ssid.equals(pastssid)) {
                            pastssid = ssid;
                            getMapLocation();
                        }
                        else {
                            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                            System.out.println("SSID changed");
                            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                            if (correctSSID) {
                                updateUserLocation();
                                getLocationPeople();
                            }
                            else {
                                getMapLocation();
                            }
                        }
                    }
                    else{System.out.println("no");
                        if(enableTimer == true) {
                            ssidTimer.cancel();
                            if(haveLocation){
                                deleteUserLocation();
                            }
                            enableTimer = false;
                        }
                        ssid = "no";
                        getMapLocation();
                    }
                }
            }, 0, 1000);
        } else {
            System.out.println("end");
            currentView = false;
            pastssid = "";
            if(enableTimer == true) {
                ssidTimer.cancel();
                if(haveLocation){
                    deleteUserLocation();
                }
                enableTimer = false;
            }
        }
    }

    private void getMapLocation() {
        request = new StringRequest(Request.Method.POST, getMapLocationURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.length() != 0) {
                    correctSSID = true;
                    String[] mapphp = response.split("~");
                    mapName = mapphp[0];
                    smallMapX = Integer.parseInt(mapphp[1]);
                    smallMapY = Integer.parseInt(mapphp[2]);
                    mapLocation = mapphp[3];
                    location.setText(mapLocation);
                    updateUserLocation();
                    getLocationPeople();
                    setMap();
                }
                else{System.out.println("fail");
                    correctSSID = false;
                    showWarning();
                }
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
                for(Integer i = 0; i < people.length; i++){
                    if(people[i] != "") {
                        peopleID[i] = Integer.parseInt(people[i]);
                    }
                }
                if(!checkSamePeople()) {
                    setPeople();
                }
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
                haveLocation = true;
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
                haveLocation = false;
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
                smallMap.setImageResource(R.drawable.a2);
                smallMap.setScaleX(15);
                smallMap.setScaleY(15);
                break;
            case "A4":
                smallMap.setImageResource(R.drawable.a4);
                smallMap.setScaleX(15);
                smallMap.setScaleY(15);
                break;
        }
        smallMap.setX(smallMapX);
        smallMap.setY(smallMapY);
        bigMap.setBackgroundColor(getResources().getColor(R.color.bigMap_background));
        bigMapTitle.setText(mapLocation);
        FrameLayout.LayoutParams bigMapTitlePlace = new FrameLayout.LayoutParams(screenWidth, 100, CENTER_HORIZONTAL);
        bigMapTitle.setLayoutParams(bigMapTitlePlace);
        setUser();
    }

    private void setPeople() {System.out.println("set");
        if(pastpeopleID != null) {
            for (int i = 0; i < pastpeopleID.length; i++) {
                if (pastpeopleID[i] != userID && pastpeopleID[i] != null) {
                    ImageView pastPeopleButton = (ImageView) getView().findViewById(100000 + pastpeopleID[i].intValue());
                    bigMapLayout.removeView(pastPeopleButton);
                }
            }
        }
        pastpeopleID = new Integer[peopleID.length];
        peopleX = new int[peopleID.length];
        peopleY = new int[peopleID.length];
        for(int i = 0; i < peopleID.length; i++){System.out.print(peopleID[i]);
            if(peopleID[i] != userID) {
                ImageView peopleButton = new ImageView(getView().getContext());
                peopleButton.setImageResource(R.drawable.people);
                FrameLayout.LayoutParams peopleButtonSize = new FrameLayout.LayoutParams(150, 150);
                Double peopleWidth = ((bigMap.getWidth() - 200) * Math.random()) + 20;
                Double peopleHeight = ((bigMap.getHeight() - 200) * Math.random()) + 20;
                while (!checkOverlap(peopleWidth.intValue(), peopleHeight.intValue())) {
                    peopleWidth = ((bigMap.getWidth() - 200) * Math.random()) + 20;
                    peopleHeight = ((bigMap.getHeight() - 200) * Math.random()) + 20;
                    peopleX[i] = peopleWidth.intValue();
                    peopleY[i] = peopleHeight.intValue();
                }
                peopleButtonSize.setMargins(peopleWidth.intValue(), peopleHeight.intValue(), 0, 0);
                peopleButton.setLayoutParams(peopleButtonSize);
                peopleButton.requestLayout();
                peopleButton.setId(100000 + peopleID[i].intValue());
                final String friendId = peopleID[i].toString();
                peopleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ViewProfile.class);
                        intent.putExtra("friendId", friendId);
                        startActivity(intent);
                    }
                });
                bigMapLayout.addView(peopleButton);
                bigMapLayout.requestLayout();
            }
            pastpeopleID[i] = peopleID[i];
        }
    }

    private boolean checkOverlap(int x, int y){
        for(int i = 0; i < peopleX.length; i++){
            if((x >= bigUser.getX() && x <= (bigUser.getX()+bigUser.getWidth())) || (y >= bigUser.getY() && y <= (bigUser.getY()+bigUser.getHeight())) || (x >= peopleX[i] && x <= (peopleX[i] + 150)) || (y >= peopleY[i] && y <= (peopleY[i] + 150))){
                return true;
            }
        }
        return false;

    }
    private void setUser() {
        smallUser.setImageResource(R.drawable.location);
        ViewGroup.MarginLayoutParams smallUserPlace = new ViewGroup.MarginLayoutParams(60, 60);
        smallUserPlace.setMargins(smallMap.getWidth()/2 + 60, smallMap.getHeight()/2 - 60, 0, 0);
        FrameLayout.LayoutParams smallUserSize = new FrameLayout.LayoutParams(smallUserPlace);
        smallUser.setLayoutParams(smallUserSize);
        smallUser.requestLayout();
        bigUser.setImageResource(R.drawable.user);
        FrameLayout.LayoutParams bigUserSize = new FrameLayout.LayoutParams(150, 150, CENTER);
        bigUser.setLayoutParams(bigUserSize);
        bigUser.requestLayout();
    }

    private boolean checkSamePeople() {
        if (peopleID != null && pastpeopleID != null) {
            if(peopleID.length == pastpeopleID.length) {
                for (int i = 0; i < Math.max(peopleID.length, pastpeopleID.length); i++) {
                    if (peopleID[i] != pastpeopleID[i]) {
                        return false;
                    }
                }
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }

    private void showWarning() {System.out.println("warning");
        if(enableTimer == true) {
            ssidTimer.cancel();
            if(haveLocation){
                deleteUserLocation();
            }
            enableTimer = false;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext());
        alertDialogBuilder.setTitle("Service not available");
        alertDialogBuilder.setMessage("Please connect to HKUST Wifi in order to use this service.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Setting",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                goSetting = true;
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });
        alertDialogBuilder.setNegativeButton("Back",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                ViewPager viewPager = USTSocialAppMain.mViewPager;
                int returnFragment = viewPager.getCurrentItem() - 1;
                viewPager.setCurrentItem(returnFragment);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
