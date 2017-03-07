package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class ProfileSetting extends Fragment {
    private Button viewMyFdList;
    private Button viewMyBlog;
    private Button editButton;
    private FloatingActionButton editProfilePicButton;

    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/createNewAccount.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        requestQueue = Volley.newRequestQueue(view.getContext());

        editProfilePicButton = (FloatingActionButton) view.findViewById(R.id.fab_profile_picture_edit);
        editProfilePicButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editButton = (Button) view.findViewById(R.id.b_profile_edit_info);
        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewMyFdList = (Button) view.findViewById(R.id.b_profile_friend_list);
        viewMyFdList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFdList  = new Intent(getActivity(), MyFriendList.class);
                startActivity(intentFdList);
            }
        });

        viewMyBlog = (Button) view.findViewById(R.id.b_profile_blog);
        viewMyBlog.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBlog = new Intent(getActivity(), MyBlog.class);
                startActivity(intentBlog);
            }
        });

        return view;
    }

    private void setProfileInfo() {

    }

       private void httpPostRequest() {

    }
}
