package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import static android.app.Activity.RESULT_OK;

public class Chatroom extends Fragment {
    private FloatingActionButton addChat;

    private String id;
    private String fdIdList;
    private String fdDisplayNameList;
    private String[] mfdIdList;
    private String[] mfdDisplayNameList;

    private static final int INDIVIDUAL_CHAT = 510384;
    private static final int GROUP_CHAT = 670662;

    private static final String getFdIdListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendIdList.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreference;

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);

        sharedPreference = view.getContext().getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreference.getString("ID", null);

        requestQueue = Volley.newRequestQueue(view.getContext());

        getFriendIdList();

        sharedPreference = view.getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        String idList = sharedPreference.getString("FDLIST_ID", null);
        String nameList = sharedPreference.getString("FDLIST_DISPLAYNAME", null);
        if (idList != null) {

            getFdListFromPreference();

        } else {
            TextView warning = (TextView) view.findViewById(R.id.view_myFriendList_warning);
            warning.setVisibility(View.VISIBLE);
        }

//        addChat = (FloatingActionButton) view.findViewById(R.id.fab_chatroom_add_chat);
//        addChat.setOnClickListener(new FloatingActionButton.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                getFriendIdList();
//
//                CharSequence options[] = new CharSequence[] {"Create an Individual Chat", "Create a Group Chat"};
//
//                AlertDialog.Builder createChat = new AlertDialog.Builder(getActivity());
//                createChat.setTitle("Create New Chat");
//                createChat.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            Intent intentIndividual = new Intent(getActivity(), CreateIndividualChat.class);
//                            startActivityForResult(intentIndividual, INDIVIDUAL_CHAT);
//                        }
//                        else {
//                            Intent intentGroup = new Intent(getActivity(), CreateGroupChat.class);
//                            startActivity(intentGroup);
//                        }
//                    }
//                });
//                createChat.show();
//            }
//        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == INDIVIDUAL_CHAT) {

            } else if (requestCode == GROUP_CHAT) {

            }
        }
    }

    private void getFdListFromPreference() {
        mfdIdList = sharedPreference.getString("FDLIST_ID", null).split(",");
        mfdDisplayNameList = sharedPreference.getString("FDLIST_DISPLAYNAME", null).split(",");

        ArrayAdapter<String> adaptor = new ArrayAdapter<>(getActivity(), R.layout.friend_list_layout, mfdDisplayNameList);

        listView.setAdapter(adaptor);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void getFriendIdList() {
        request = new StringRequest(Request.Method.POST, getFdIdListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {

                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    String fdList = response.split(":")[1];
                    fdIdList = fdList;
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(fdList);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdIdListEditor = sharedPreferences.edit();
                    fdIdListEditor.putString("FDLIST_ID", fdList);
                    fdIdListEditor.commit();

                    getFriendDisplayNameList();
                } else {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("FDLIST_ID", null);
                    editor.putString("FDLIST_DISPLAYNAME", null);
                    editor.commit();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            // Post request parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void getFriendDisplayNameList() {
        request = new StringRequest(Request.Method.POST, getFdDisplayNameListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    String fdList = response.split(":")[1];
                    fdDisplayNameList = fdList;
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(fdList);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdNameListEditor = sharedPreferences.edit();
                    fdNameListEditor.putString("FDLIST_DISPLAYNAME", fdList);
                    fdNameListEditor.commit();


                    // update friend list ui using the latest info
                    getFdListFromPreference();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            // Post request parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("list", fdIdList);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }
}
