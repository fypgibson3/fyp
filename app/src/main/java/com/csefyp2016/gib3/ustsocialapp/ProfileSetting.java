package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfileSetting extends Fragment {
    private Button viewMyFdList;
    private Button viewMyBlog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

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
}
