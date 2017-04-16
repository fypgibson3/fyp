package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewPost extends AppCompatActivity {

    private String postTitle;
    private String postHashtags;
    private String postContent;
    private String postWarning;

    private static final String uploadStoryPostURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadStoryPost.php";

    private RequestQueue requestQueue;
    private StringRequest request;

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

        requestQueue = Volley.newRequestQueue(this);

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
            uploadPost();
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

    private void uploadPost() {
        request = new StringRequest(Request.Method.POST, uploadStoryPostURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {System.out.println(response);
                if(response.contains("Success")){
                    setToast("success");
                }
                else if(response.contains("Fail")){
                    setToast("fail");
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                hashMap.put("date", dateFormat.format(date));
                hashMap.put("title", postTitle);
                hashMap.put("hashtag", postHashtags);
                hashMap.put("content", postContent);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void setToast(String response){
        if(response == "success"){
            Toast.makeText(this, "Create story post successful.", Toast.LENGTH_LONG).show();
        }
        else if(response == "fail"){
            Toast.makeText(this, "Create story post fail. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

}
