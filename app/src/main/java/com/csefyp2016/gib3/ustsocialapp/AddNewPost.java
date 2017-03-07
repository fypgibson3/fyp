package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class AddNewPost extends AppCompatActivity {

    private String postTitle;
    private String postHashtags;
    private String postContent;
    private String postWarning;

    private ScrollView scrollView;
    private EditText title;
    private EditText hashtags;
    private EditText content;
    private TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollView = (ScrollView) findViewById(R.id.scroll_addNewPost);
        title = (EditText) findViewById(R.id.i_addNewPost_title);
        hashtags = (EditText) findViewById(R.id.i_addNewPost_hashtags);
        content = (EditText) findViewById(R.id.i_addNewPost_content);
        warning = (TextView) findViewById(R.id.view_addNewPost_warning);

        Button cancelButton = (Button) findViewById(R.id.b_addNewPost_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonAction();
            }
        });

        Button submitButton = (Button) findViewById(R.id.b_addNewPost_submit);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonAction();
            }
        });
    }

    private void cancelButtonAction(){
        this.finish();
    }

    private void submitButtonAction(){
        if(checkFillIn()) {
            postTitle = title.getText().toString();
            postHashtags = hashtags.getText().toString();
            postContent = content.getText().toString();
            //save to server
            this.finish();
        }
        else{
            warning.setText(postWarning);
            warning.setVisibility(View.VISIBLE);
            scrollView.pageScroll(View.FOCUS_UP);
        }
    }

    private boolean checkFillIn(){
        boolean moreThanOne = false;
        postWarning = "Please fill in";
        if(title.getText().toString().isEmpty()){
            moreThanOne = true;
            postWarning = postWarning + " title";
        }
        if(content.getText().toString().isEmpty()) {
            if (moreThanOne == true) {
                postWarning = postWarning + ", content";
            } else {
                moreThanOne = true;
                postWarning = postWarning + " content";
            }
        }
        if(moreThanOne == true){
            postWarning = postWarning + " field.";
            return false;
        }
        else{
            return true;
        }
    }

}
