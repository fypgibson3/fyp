package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class MyFriendList extends AppCompatActivity {
    private static final String getFdIdListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendIdList.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private String id;
    private String[] fdIdList;
    private String[] fdDisplayNameList;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreference;

    private Button instantAddFriendButton;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreference = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreference.getString("ID", null);

        sharedPreference = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        String idList = sharedPreference.getString("FDLIST_ID", null);
        String nameList = sharedPreference.getString("FDLIST_DISPLAYNAME", null);

        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
        System.out.println("Id List: " + idList);
        System.out.println("Name List: " + nameList);
        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

        if (sharedPreference.getString("FDLIST_ID", null) != null) {
            fdIdList = sharedPreference.getString("FDLIST_ID", null).split(",");
            fdDisplayNameList = sharedPreference.getString("FDLIST_DISPLAYNAME", null).split(",");

            ArrayAdapter<String> adaptor = new ArrayAdapter<>(this, R.layout.friend_list_layout, fdDisplayNameList);

            listView = (ListView) findViewById(android.R.id.list);
            listView.setAdapter(adaptor);

            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedId = fdIdList[i];
                    startNewIntent(selectedId);
                }
            });
        }
        else {
            TextView warning = (TextView) findViewById(R.id.view_myFriendList_warning);
            warning.setVisibility(View.VISIBLE);
        }

        instantAddFriendButton = (Button) findViewById(R.id.b_myFriendList_instant_add_fd);
        instantAddFriendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyFriendList.this, InstantAddFriend.class));
            }
        });

    }

    private void startNewIntent(String id) {
        Intent viewProfile = new Intent(this, ViewProfile.class);
        viewProfile.putExtra("friendId", id);
        startActivity(viewProfile);
    }

}
