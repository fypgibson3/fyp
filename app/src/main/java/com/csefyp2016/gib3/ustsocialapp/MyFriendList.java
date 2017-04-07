package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MyFriendList extends AppCompatActivity {
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFreindList.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private Button instantAddFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        instantAddFriendButton = (Button) findViewById(R.id.b_myFriendList_instant_add_fd);
        instantAddFriendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyFriendList.this, InstantAddFriend.class));
            }
        });

    }

}
