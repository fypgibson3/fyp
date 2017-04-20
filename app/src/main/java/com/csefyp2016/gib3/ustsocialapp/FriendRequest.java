package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //ArrayAdapter<String> adaptor = new ArrayAdapter<>(this, R.layout.friend_request_layout, fdDisplayNameList);

        //listView = (ListView) findViewById(android.R.id.list);
        //listView.setAdapter(adaptor);


    }

}
