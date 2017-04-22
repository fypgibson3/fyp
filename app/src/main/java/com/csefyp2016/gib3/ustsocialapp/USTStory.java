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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
    private Boolean currentView = false;

    private static final String getStoryIdURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStoryId.php";
    private static final String getStoryURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStory.php";
    private static final String getVoteURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getVote.php";
    private static final String getAttitudeURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getAttitude.php";
    private static final String getCommentURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getComment.php";
    private static final String uploadAttitudeURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadAttitude.php";
    private static final String uploadCommentURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadComment.php";
    private static final String uploadVoteURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadVote.php";

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
                if(currentView) {
                    View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                    if (diff == 0) {
                        System.out.println("show" + shownStory);
                        requestStoryId();
                    }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("visible");
            currentView = true;
            refresh();
        } else {
            System.out.println("invisible");
            currentView = false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        currentView = false;
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
                    String storyid = ((TextView)((LinearLayout) radioGroup.getParent()).findViewById(R.id.story_currentId)).getText().toString();
                    setVote(storyid, radio.getText().toString(), radioGroup);
                }
            });
        }
        currentId.setText(storyid.toString());
        attitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) view.getParent();
                TextView textView = (TextView) layout.getChildAt(1);
                Integer attitudeId = Integer.parseInt(textView.getText().toString());
                onAttitude(attitudeId);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) view.getParent();
                TextView textView = (TextView) layout.getChildAt(1);
                Integer commentId = Integer.parseInt(textView.getText().toString());
                onComment(commentId);
            }
        });
        shownStory++;
        table.addView(newRow);
        table.requestLayout();
    }

    private void setVote(final String storyid,final String option, final RadioGroup radioGroup){
        request = new StringRequest(Request.Method.POST, getVoteURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String[] vote = response.split("`~@!");
                String uploadVote = "";
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    RadioButton optionButton = (RadioButton) radioGroup.getChildAt(i);
                    Integer newVote = Integer.parseInt(vote[i]);
                    String currentOption = optionButton.getText().toString();
                    if(currentOption == option){
                        newVote++;
                    }
                    uploadVote = uploadVote + newVote.toString() + "`~@!";
                    optionButton.setText(currentOption + " (" + newVote + ")");
                }
                uploadVote(storyid, uploadVote);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("storyid", storyid);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void uploadVote(final String storyid, final String vote){
        request = new StringRequest(Request.Method.POST, uploadVoteURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {System.out.println(response);
                if(response.contains("Success")){
                    setToast("vote", "success");
                }
                else{
                    setToast("vote", "fail");
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
                hashMap.put("storyid", storyid);
                hashMap.put("vote", vote);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void onAttitude(final Integer story) {
        request = new StringRequest(Request.Method.POST, getAttitudeURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                attitude = response.split("~`>]-");
                showAttitude(attitude, story);
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

    private void showAttitude(String[] attitude,final Integer story){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        String[] getAttitude = attitude[1].split("`~@!");
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getView().getContext()).inflate(R.layout.show_attitude, null);
        builder.setView(layout);
        ((TextView) layout.findViewById(R.id.attitude_title)).setText(attitude[0]);
        ((TextView) layout.findViewById(R.id.attitude_positive)).setText(getAttitude[0]);
        ((TextView) layout.findViewById(R.id.attitude_negative)).setText(getAttitude[1]);
        ((TextView) layout.findViewById(R.id.attitude_id)).setText(story.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog alert = (AlertDialog) dialogInterface;
                RadioButton positive = (RadioButton) alert.findViewById(R.id.attitude_positive_choice);
                RadioButton negative = (RadioButton) alert.findViewById(R.id.attitude_negative_choice);
                Integer positiveNum = Integer.parseInt(((TextView) alert.findViewById(R.id.attitude_positive)).getText().toString());
                Integer negativeNum = Integer.parseInt(((TextView) alert.findViewById(R.id.attitude_negative)).getText().toString());
                String story = ((TextView) alert.findViewById(R.id.attitude_id)).getText().toString();
                if(positive.isChecked()){
                    positiveNum++;
                    uploadAttitude(story, positiveNum, negativeNum);
                }
                else if(negative.isChecked()){
                    negativeNum++;
                    uploadAttitude(story, positiveNum, negativeNum);
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void uploadAttitude(final String storyid, final Integer positive, final Integer negetive){
        request = new StringRequest(Request.Method.POST, uploadAttitudeURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.contains("Success")){
                    setToast("attitude", "success");
                }
                else{
                    setToast("attitude", "fail");
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
                hashMap.put("storyid", storyid);
                hashMap.put("positive", positive.toString());
                hashMap.put("negative", negetive.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void onComment(final Integer story){
        request = new StringRequest(Request.Method.POST, getCommentURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                comment = response.split("~`>]-");
                showComment(comment, story);
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

    private void showComment(String[] comment,final Integer story){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        String[] getComment = comment[1].split("`~@!");
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getView().getContext()).inflate(R.layout.show_comment, null);
        builder.setView(layout);
        ((TextView) layout.findViewById(R.id.comment_title)).setText(comment[0]);
        ((TextView) layout.findViewById(R.id.comment_id)).setText(story.toString());
        LinearLayout linear = (LinearLayout) layout.findViewById(R.id.comment_comment);
        linear.removeAllViewsInLayout();
        for(int i = 0; i < getComment.length - 1; i++){
            if(getComment[i] != ""){
                LinearLayout showComment = (LinearLayout) LayoutInflater.from(getView().getContext()).inflate(R.layout.comment, null);
                TextView commentText = (TextView) showComment.findViewById(R.id.comment_text);
                commentText.setText(getComment[i]);
                linear.addView(showComment);
                linear.requestLayout();
            }
        }
        Button addComment = (Button) layout.findViewById(R.id.comment_add);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) view.getParent().getParent().getParent();
                String storyid = ((TextView) layout.findViewById(R.id.comment_id)).getText().toString();
                String newcomment = ((EditText) layout.findViewById(R.id.comment_new)).getText().toString();
                LinearLayout commentLayout = (LinearLayout) layout.findViewById(R.id.comment_comment);
                String comment = "";
                for(int i = 0; i < commentLayout.getChildCount(); i++){
                    LinearLayout commentlayout = (LinearLayout) commentLayout.getChildAt(i);
                    TextView commentText = (TextView) commentlayout.getChildAt(0);
                    comment = comment + commentText.getText().toString() + "`~@!";
                }
                comment = comment + newcomment + "`~@!";
                uploadComment(storyid, comment, commentLayout);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void refreshComment(LinearLayout linear, String[] getComment) {
        linear.removeAllViewsInLayout();
        for(int i = 0; i < getComment.length; i++){
            if(getComment[i] != ""){
                LinearLayout showComment = (LinearLayout) LayoutInflater.from(getView().getContext()).inflate(R.layout.comment, null);
                TextView commentText = (TextView) showComment.findViewById(R.id.comment_text);
                commentText.setText(getComment[i]);
                linear.addView(showComment);
                linear.requestLayout();
           }
        }
    }

    private void uploadComment(final String storyid, final String comment, final LinearLayout linear){
        request = new StringRequest(Request.Method.POST, uploadCommentURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.contains("Success")){
                    String refreshcomment = response.split("~`>]-")[1];
                    String[] getComment = refreshcomment.split("`~@!");
                    refreshComment(linear, getComment);
                    EditText newComment = (EditText) ((LinearLayout)linear.getParent().getParent()).findViewById(R.id.comment_new);
                    newComment.setText("");
                    setToast("comment", "success");
                }
                else{
                    setToast("comment", "fail");
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
                hashMap.put("storyid", storyid);
                hashMap.put("comment", comment);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void setToast(String type, String response) {
        if(response == "success"){
            Toast.makeText(getView().getContext(), "Present story " + type + " successful.", Toast.LENGTH_LONG).show();
        }
        else if(response == "fail"){
            Toast.makeText(getView().getContext(), "Present story " + type + " fail. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
