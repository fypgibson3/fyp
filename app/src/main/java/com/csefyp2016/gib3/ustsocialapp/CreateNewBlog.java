package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateNewBlog extends AppCompatActivity {

    private String blogTitle;
    private String blogContent;
    private String blogWarning;
    private Integer userID;

    private static final String uploadBlogURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadBlog.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private SharedPreferences sharedPreferences;

    private ScrollView scrollView;
    private EditText title;
    private EditText content;
    private TextView warning;
    private Button submit;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        scrollView = (ScrollView) findViewById(R.id.scroll_addNewBlog);
        title = (EditText) findViewById(R.id.i_addNewBlog_title);
        content = (EditText) findViewById(R.id.i_addNewBlog_content);
        warning = (TextView) findViewById(R.id.view_addNewBlog_warning);

        submit = (Button) findViewById(R.id.b_addNewBlog_submit);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonAction();
            }
        });

        cancel = (Button) findViewById(R.id.b_addNewBlog_cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonAction();
            }
        });

    }

    private void cancelButtonAction() {
        this.finish();
    }

    private void submitButtonAction() {
        if (checkFillIn()) {
            blogTitle = title.getText().toString();
            blogContent = content.getText().toString();
            uploadBlog();
            this.finish();
        } else {
            warning.setText(blogWarning);
            warning.setVisibility(View.VISIBLE);
            scrollView.pageScroll(View.FOCUS_UP);
        }

    }

    private boolean checkFillIn() {
        boolean moreThanOne = false;
        blogWarning = "Please fill in";
        if (title.getText().toString().isEmpty()) {
            moreThanOne = true;
            blogWarning = blogWarning + " title";
        }
        if (content.getText().toString().isEmpty()) {
            if (moreThanOne == true) {
                blogWarning = blogWarning + ", content";
            } else {
                moreThanOne = true;
                blogWarning = blogWarning + " content";
            }
        }
        if (moreThanOne == true) {
            blogWarning = blogWarning + " field.";
            return false;
        } else {
            return true;
        }
    }

    private void uploadBlog() {
        request = new StringRequest(Request.Method.POST, uploadBlogURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    setToast("success");
                } else if (response.contains("Fail")) {
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
                hashMap.put("title", blogTitle);
                hashMap.put("content", blogContent);
                getSelfID();
                hashMap.put("userid", userID.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void setToast(String response) {
        if (response == "success") {
            Toast.makeText(this, "Create blog successful.", Toast.LENGTH_LONG).show();
        } else if (response == "fail") {
            Toast.makeText(this, "Create blog fail. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void getSelfID() {
        sharedPreferences = this.getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        userID = Integer.parseInt(sharedPreferences.getString("ID", ""));
    }
}
