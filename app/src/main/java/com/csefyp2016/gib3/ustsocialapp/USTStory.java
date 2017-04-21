package com.csefyp2016.gib3.ustsocialapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class USTStory extends Fragment {

    private Integer shownStory = 0;
    private Integer[] storyId;
    private String[] content;
    private String[] attitude;
    private String[] comment;

    private static final String getStoryIdURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStoryId.php";
    private static final String getStoryURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStory.php";
    private static final String getVoteURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getVote.php";
    private static final String getAttitudeURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getAttitude.php";
    private static final String getCommentURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getComment.php";


    private RequestQueue requestQueue;
    private StringRequest request;

    private FloatingActionButton addStory;
    private SwipeRefreshLayout swipeLayout;
    private NestedScrollView scrollView;
    private TableLayout table;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ust_story, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.story_swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        scrollView = (NestedScrollView) view.findViewById(R.id.story_scroll);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff == 0) {
                    System.out.println("show" + shownStory);
                    requestStoryId();
                }
            }
        });
        table = (TableLayout) view.findViewById(R.id.story_table);
        addStory = (FloatingActionButton) view.findViewById(R.id.fab_ustStory_add_story);
        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[] {"Create a Post", "Create a Vote"};

                AlertDialog.Builder addStoryOption = new AlertDialog.Builder(getActivity());
                addStoryOption.setTitle("Add New Ust Story");
                addStoryOption.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intentPost = new Intent(getActivity(), AddNewPost.class);
                            startActivity(intentPost);
                        }
                        else {
                            Intent intentVote = new Intent(getActivity(), AddNewVote.class);
                            startActivity(intentVote);
                        }
                    }
                });
                addStoryOption.show();
            }
        });
        requestStoryId();
        //requestStoryContent(9);
        //requestStoryContent(25);
        return view;
    }

    private void refresh() {
        table.removeAllViewsInLayout();
        shownStory = 0;
        requestStoryId();
        swipeLayout.setRefreshing(false);
    }

    private void requestStoryId(){
        request = new StringRequest(Request.Method.POST, getStoryIdURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {System.out.println(response);
                String[] story = response.split("~");
                storyId = new Integer[story.length];
                for(int i = 0; i < story.length; i++){
                    if(story[i] != "") {
                        storyId[i] = Integer.parseInt(story[i]);
                    }
                }
                if(story[0] != "") {
                    requestStoryContent(storyId[0], 0);
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
                hashMap.put("shown", shownStory.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void requestStoryContent(final Integer storyid,final Integer i){

        request = new StringRequest(Request.Method.POST, getStoryURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                content = response.split("~`>]-");
                addStory(content, storyid);
                if(i != storyId.length - 1){
                    requestStoryContent(storyId[i+1], i+1);
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
                hashMap.put("storyid", storyid.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    private void addStory(String[] content, Integer storyid){
        TableRow newRow = (TableRow) LayoutInflater.from(getView().getContext()).inflate(R.layout.show_story, null);
        TextView title = (TextView) newRow.findViewById(R.id.story_title);
        TextView datetime = (TextView) newRow.findViewById(R.id.story_date);
        TextView wording = (TextView) newRow.findViewById(R.id.story_content);
        RadioGroup radioGroup = (RadioGroup) newRow.findViewById(R.id.story_radiogroup);
        TextView currentId = (TextView) newRow.findViewById(R.id.story_currentId);
        Button attitude = (Button) newRow.findViewById(R.id.story_attitude);
        Button comment = (Button) newRow.findViewById(R.id.story_comment);
        title.setText(content[2]);
        datetime.setText(content[1]);
        wording.setText(content[3]);
        if(content[0].contains("post")){

        }
        else{
            String[] option = content[4].split("`~>@!");
            radioGroup.setVisibility(View.VISIBLE);
                for(int i = 0; i < option.length - 1; i++){
                RadioButton radioButton = (RadioButton) LayoutInflater.from(getView().getContext()).inflate(R.layout.story_radio, null);
                radioButton.setText(option[i]);
                radioGroup.addView(radioButton);
                radioGroup.requestLayout();
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                    RadioButton radio = (RadioButton) getView().findViewById(id);
                    for(int i = 0; i < radioGroup.getChildCount(); i++){
                        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                        radioButton.setEnabled(false);
                        radioButton.setTextColor(Color.BLACK);
                    }
                }
            });
        }
        currentId.setText(storyid.toString());
        attitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) view.getParent();
                TextView textView = (TextView) layout.getChildAt(0);
                Integer attitudeId = Integer.parseInt(textView.getText().toString());
                onAttitude(attitudeId);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) view.getParent();
                TextView textView = (TextView) layout.getChildAt(0);
                Integer commentId = Integer.parseInt(textView.getText().toString());
                onComment(commentId);
            }
        });
        shownStory++;
        table.addView(newRow);
        table.requestLayout();
    }

    private void onAttitude(final Integer story) {
        request = new StringRequest(Request.Method.POST, getAttitudeURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                attitude = response.split("~`>]-");
                showAttitude(attitude);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("storyid", story.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void showAttitude(String[] attitude){
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle(attitude[0]);
        builder.setMessage(attitude[1]);
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onComment(final Integer story){
        request = new StringRequest(Request.Method.POST, getCommentURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("storyid", story.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }
}
