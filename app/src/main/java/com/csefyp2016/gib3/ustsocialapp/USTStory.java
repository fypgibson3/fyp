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
    private Integer[] storyId = new Integer[10];

    private static final String getStoryIdURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStoryId.php";
    private static final String getStoryURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getStory.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private FloatingActionButton addStory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ust_story, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

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

        return view;
    }

    private void requestStoryId(){
        request = new StringRequest(Request.Method.POST, getStoryIdURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String[] story = response.split("~");
                for(int i = 0; i < story.length; i++){
                    storyId[i] = Integer.parseInt(story[i]);
                }
                addStory();
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

    private String[] requestStoryContent(final Integer storyId){
        String[] content= new String[4];

        request = new StringRequest(Request.Method.POST, getStoryURL, new Response.Listener<String>() {

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
                hashMap.put("storyid", storyId.toString());
                return hashMap;
            }
        };
        requestQueue.add(request);

        return content;
    }
    private void addStory(){

    }
}
