package com.csefyp2016.gib3.ustsocialapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class CreateIndividualChat extends AppCompatPreferenceActivity {
    private String id;
    private String[] fdIdList;
    private String[] fdDisplayNameList;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreference;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_individual_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_createIndividualChat);
        setSupportActionBar(toolbar);

        sharedPreference = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreference.getString("ID", null);

        sharedPreference = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        if (sharedPreference.getString("FDLIST_ID", null) != null) {
            fdIdList = sharedPreference.getString("FDLIST_ID", null).split(",");
            fdDisplayNameList = sharedPreference.getString("FDLIST_DISPLAYNAME", null).split(",");

            ArrayAdapter<String> adaptor = new ArrayAdapter<>(this, R.layout.friend_list_layout, fdDisplayNameList);

            listView = (ListView) findViewById(android.R.id.list);
            listView.setAdapter(adaptor);

            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
        }
        else {
            TextView warning = (TextView) findViewById(R.id.view_createIndividualChat_warning);
            warning.setVisibility(View.VISIBLE);
        }

    }

}
