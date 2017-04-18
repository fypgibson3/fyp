package com.csefyp2016.gib3.ustsocialapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class USTStory extends Fragment {

    private Integer shownStory = 0;
    private Integer[] storyId = new Integer[10];
    private String[] content;
    private Boolean firstTime = true;

    private static final String getStoryIdURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStoryId.php";
    private static final String getStoryURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStory.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private FloatingActionButton addStory;
    private TableLayout table;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ust_story, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

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

    private void requestStoryId(){
        request = new StringRequest(Request.Method.POST, getStoryIdURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {System.out.println(response);
                String[] story = response.split("~");
                for(int i = 0; i < story.length; i++){
                    storyId[i] = Integer.parseInt(story[i]);System.out.println(storyId[i]);
                }
                requestStoryContent(storyId[0], 0);
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
                if(firstTime){
                    firstTime = false;
                    hashMap.put("lastest", "0");
                }
                else{
                    hashMap.put("lastest", storyId[storyId.length-1].toString());
                }
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void requestStoryContent(final Integer storyid,final Integer i){System.out.println("request" + storyid);

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
        TextView currentId = (TextView) newRow.findViewById(R.id.story_currentId);
        Button attitude = (Button) newRow.findViewById(R.id.story_attitude);
        Button comment = (Button) newRow.findViewById(R.id.story_comment);
        title.setText(content[2]);
        datetime.setText(content[1]);;
        if(content[0].contains("post")){
            wording.setText(content[4]);
        }
        else{
            wording.setText(content[4]);
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

    private void onAttitude(Integer story) {

    }

    private void onComment(Integer story){

    }
}
