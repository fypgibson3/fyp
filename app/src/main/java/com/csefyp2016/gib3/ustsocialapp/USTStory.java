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


public class USTStory extends Fragment {
    private FloatingActionButton addStory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ust_story, container, false);

        addStory = (FloatingActionButton) view.findViewById(R.id.fb_ustStory_add_story);
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

}
