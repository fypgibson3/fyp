package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class USTSocialAppMain extends AppCompatActivity {
    private static final String loginPreference = "LoginPreference";
    private static final String profilePreference = "ProfilePreference";
    private static final String imagePreference = "ImagePreference";
    private static final String friendListPreference = "FriendList";

    private static final String pendingListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/retrievePendingFdship.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private String friendRequestIdList;
    private String friendRequestDisplayNameList;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustsocial_app_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Bundle previousIntent = getIntent().getExtras();
        if (previousIntent != null) {
            previousIntent.getInt("chatroomFragment", 0);
            ViewPager viewPager = USTSocialAppMain.mViewPager;
            viewPager.setCurrentItem(2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ustsocial_app_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_ustSocialAppMain_settings) {
            startActivity(new Intent(USTSocialAppMain.this, Settings.class));
            return true;
        }
        else if (id == R.id.menu_ustSocialAppMain_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            sharedPreferences = getSharedPreferences(imagePreference, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();

            startActivity(new Intent(USTSocialAppMain.this, SignIn.class));
            this.finish();
            return true;
        }
        else if (id == R.id.menu_ustSocialAppMain_friend_request) {
            setFriendRequestIdPreference();
            startActivity(new Intent(USTSocialAppMain.this, FriendRequest.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    USTStory ustStory = new USTStory();
                    return ustStory;
                case 1:
                    USTMap map = new USTMap();
                    return map;
                case 2:
                    Chatroom chatroom = new Chatroom();
                    return chatroom;
                case 3:
                    ProfileSetting profileSetting = new ProfileSetting();
                    return profileSetting;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "UST Story";
                case 1:
                    return "UST Map";
                case 2:
                    return "Chatroom";
                case 3:
                    return "My Profile";
                default:
                    return null;
            }
        }
    }

    private void setFriendRequestIdPreference() {
        request = new StringRequest(com.android.volley.Request.Method.POST, pendingListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    String friendRequestListString = response.split(":")[1];
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println("Friend request received: " + friendRequestListString);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    friendRequestIdList = friendRequestListString;

                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("friendRequestList_ID", friendRequestListString);
                    editor.commit();

                    setFriendRequestListDisplayNamePreference();
                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("friendRequestList_ID", null);
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
                SharedPreferences sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
                String id = sharedPreferences.getString("ID", null);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                hashMap.put("flag", "2");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setFriendRequestListDisplayNamePreference() {
        request = new StringRequest(com.android.volley.Request.Method.POST, getFdDisplayNameListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    String friendRequestDisplayNameListString = response.split(":")[1];
                    friendRequestDisplayNameList = friendRequestDisplayNameListString;
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(friendRequestDisplayNameListString);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdNameListEditor = sharedPreferences.edit();
                    fdNameListEditor.putString("friendRequestList_DisplayName", friendRequestDisplayNameListString);
                    fdNameListEditor.commit();
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
                hashMap.put("list", friendRequestIdList);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }
}
