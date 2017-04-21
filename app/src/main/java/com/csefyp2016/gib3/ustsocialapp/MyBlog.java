package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MyBlog extends AppCompatActivity {

    private Integer userID;
    private Integer[] blogID;
    private String[] blog;

    private static final String getBlogIdURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getBlogId.php";
    private static final String getBlogURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getBlog.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private SharedPreferences sharedPreferences;

    private FloatingActionButton addBlog;
    private TableLayout table;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        addBlog = (FloatingActionButton) findViewById(R.id.fab_myBlog_add_blog);
        addBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBlog.this, CreateNewBlog.class));
            }
        });
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.blog_swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        table = (TableLayout) findViewById(R.id.f_myBlog_table);
        requestBlogId();
    }

    private void refresh() {
        table.removeAllViewsInLayout();
        requestBlogId();
        swipeLayout.setRefreshing(false);
    }

    private void requestBlogId(){
        request = new StringRequest(Request.Method.POST, getBlogIdURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String[] blog = response.split("~");
                blogID = new Integer[blog.length];
                for(int i = 0; i < blog.length; i++){
                    if(blog[i] != ""){
                        blogID[i] = Integer.parseInt(blog[i]);
                    }
                }
                if(blog[0] != ""){
                    requestBlogTitle(blogID[0], 0);
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
                getSelfID();
                hashMap.put("userid", userID.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void getSelfID() {
        sharedPreferences = this.getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        userID = Integer.parseInt(sharedPreferences.getString("ID", ""));
    }

    private void requestBlogTitle(final Integer blogid,final Integer i){

        request = new StringRequest(Request.Method.POST, getBlogURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                blog = response.split("~`>]-");
                addTitle(blog, blogid);
                if(i != blogID.length - 1){
                    requestBlogTitle(blogID[i+1], i+1);
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
                hashMap.put("blogid", blogid.toString());
                hashMap.put("userid", userID.toString());
                hashMap.put("get", "title");
                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    private void addTitle(String[] blog, Integer blogid){
        TableRow newRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.show_blog_title, null);
        TextView title = (TextView) newRow.findViewById(R.id.blog_title);
        TextView date = (TextView) newRow.findViewById(R.id.blog_date);
        TextView currentId = (TextView) newRow.findViewById(R.id.blog_currentId);
        title.setText(blog[0]);
        date.setText(blog[1]);
        currentId.setText(blogid.toString());
        newRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) view;
                LinearLayout layout = (LinearLayout) row.getChildAt(0);
                TextView textView = (TextView) layout.getChildAt(2);
                Integer contentId = Integer.parseInt(textView.getText().toString());
                requestBlogContent(contentId);
            }
        });
        table.addView(newRow);
        table.requestLayout();
    }

    private void requestBlogContent(final Integer blogid){

        request = new StringRequest(Request.Method.POST, getBlogURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                blog = response.split("~`>]-");
                showContent(blog);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("blogid", blogid.toString());
                hashMap.put("userid", userID.toString());
                hashMap.put("get", "content");
                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    private void showContent(String[] blog){
        Intent intent = new Intent(this, ShowBlogContent.class);
        intent.putExtra("title", blog[0]);
        intent.putExtra("date", blog[1]);
        intent.putExtra("content", blog[2]);
        startActivity(intent);
    }
}
