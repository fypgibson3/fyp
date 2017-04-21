package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class FriendRequest extends AppCompatActivity {

    private String id;
    private String friendRequestIdList;
    private String friendRequestDisplayNameList;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreferences;

    private List<Request> mRequests = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRequestsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  --------------------------------------------------------------- Testing features  --------------------------------------------------------------- //
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusBarColor));
        //  ---------------------------------------------------------------Testing features  --------------------------------------------------------------- //

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);

        sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        friendRequestIdList = sharedPreferences.getString("friendRequestList_ID", null);
        friendRequestDisplayNameList = sharedPreferences.getString("friendRequestList_DisplayName", null);

        if (friendRequestIdList != null) {
            System.out.println("Creating Request Layouts.......................");

            String[] idList = friendRequestIdList.split(",");
            String[] displayNameList = friendRequestDisplayNameList.split(",");
            for (int i = 0; i < idList.length; i++) {
                addRequest(idList[i], displayNameList[i]);
            }
        }

        mAdapter = new RequestAdapter(this, mRequests);
        mRequestsView = (RecyclerView) findViewById(R.id.request_list);
        mRequestsView.setLayoutManager(new LinearLayoutManager(this));
        mRequestsView.setAdapter(mAdapter);
    }

    private void addRequest(String id, String name) {
        mRequests.add(new Request.Builder()
                .requestId(id).requestName(name).build());
//        mAdapter.notifyItemInserted(mRequests.size() - 1);

        System.out.println("Request from " + name + " is set!");
    }

}
