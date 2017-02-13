package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class CreateGroupChat extends AppCompatActivity {
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createButton = (Button) findViewById(R.id.b_createGroupChat_create);
        createButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

}
