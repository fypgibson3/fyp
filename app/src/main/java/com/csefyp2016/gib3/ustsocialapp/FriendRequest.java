package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendRequest extends AppCompatActivity {

    private String id;
    private Set<String> friendRequestList;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreferences;

    private static final String acceptURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/";
    private static final String deleteURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/";
    private RequestQueue requestQueue;
    private StringRequest request;

    private List<Request> mRequests = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRequestsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);

        sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        friendRequestList = sharedPreferences.getStringSet("friendRequestList", null);

        if (friendRequestList != null) {

        }


        //ArrayAdapter<String> adaptor = new ArrayAdapter<>(this, R.layout.friend_request_layout, fdDisplayNameList);

        //listView = (ListView) findViewById(android.R.id.list);
        //listView.setAdapter(adaptor);
    }

}
