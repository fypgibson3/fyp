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


public class Chatroom extends Fragment {
    private FloatingActionButton addChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        addChat = (FloatingActionButton) view.findViewById(R.id.fab_chatroom_add_chat);
        addChat.setOnClickListener(new FloatingActionButton.OnClickListener() {

            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[] {"Create an Individual Chat", "Create a Group Chat"};

                AlertDialog.Builder createChat = new AlertDialog.Builder(getActivity());
                createChat.setTitle("Create New Chat");
                createChat.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intentIndividual = new Intent(getActivity(), CreateIndividualChat.class);
                            startActivity(intentIndividual);
                        }
                        else {
                            Intent intentGroup = new Intent(getActivity(), CreateGroupChat.class);
                            startActivity(intentGroup);
                        }
                    }
                });
                createChat.show();
            }
        });

        return view;
    }
}
