package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Toying on 22/4/17.
 */

public class ShowBlogContent extends AppCompatActivity{

    private String title;
    private String date;
    private String content;

    private TextView blogTitle;
    private TextView blogDate;
    private TextView blogContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        title = extras.getString("title");
        date = extras.getString("date");
        content = extras.getString("content");

        blogTitle = (TextView) findViewById(R.id.blog_content_title);
        blogDate = (TextView) findViewById(R.id.blog_content_date);
        blogContent = (TextView) findViewById(R.id.blog_content_content);

        blogTitle.setText(title);
        blogDate.setText(date);
        blogContent.setText(content);
    }
}
