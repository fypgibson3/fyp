package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ViewProfile extends AppCompatActivity {
    private static final String profileURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getProfileInfo.php";
    private static final String profilePicURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getProfilePic.php";
    private static final String switchURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getSwitchInfo.php";
    private static final String friendShipURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/createFriendship.php";
    private static final String getHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getHashtag.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private String id;
    private String friendId;

    private Bitmap profilePicture;

    private String displayName;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;
    private String personalDes;

    private boolean genderShow;
    private boolean dateOfBirthShow;
    private boolean countryShow;
    private boolean studentCategoryShow;
    private boolean facultyShow;
    private boolean majorShow;
    private boolean yearOfStudyShow;

    private TextView displayNameView;
    private TextView genderView;
    private TextView dateOfBirthView;
    private TextView countryView;
    private TextView studentCategoryView;
    private TextView facultyView;
    private TextView majorView;
    private TextView yearOfStudyView;
    private TextView personalDesView;
    private ImageView profilePictureShow;

    private static  final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreferences;

    private Button viewBlog;
    private Button addFriend;
    private Space separator;

    private final ArrayList<String> hashtag_id = new ArrayList<String>();
    private final ArrayList<String> hashtag_content = new ArrayList<String>();
    private TableLayout hashtagTableLayout;
    private TableRow tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);

        Intent previousIntent = getIntent();
        friendId = previousIntent.getStringExtra("friendId");

        sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
        String fdIdString = sharedPreferences.getString("FDLIST_ID", null);
        if (fdIdString != null) {
            String[] friendIdList = fdIdString.split(",");
            if (!Arrays.asList(friendIdList).contains(friendId)) {
                addFriend = (Button) findViewById(R.id.b_viewProfile_addFriend);
                addFriend.setVisibility(View.VISIBLE);
                separator = (Space) findViewById(R.id.viewProfile_separator);
                separator.setVisibility(View.VISIBLE);

                addFriend.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendFriendshipRequest();
                    }
                });
            }
        }

        String pendingString = sharedPreferences.getString("pendingList", null);
        if (pendingString != null) {
            String[] pendingList = pendingString.split(",");
            if (Arrays.asList(pendingList).contains(friendId)) {
                addFriend = (Button) findViewById(R.id.b_viewProfile_addFriend);
                addFriend.setText("Pending");
                addFriend.setBackgroundColor(Color.GRAY);
                addFriend.setClickable(false);
                addFriend.setVisibility(View.VISIBLE);
                separator = (Space) findViewById(R.id.viewProfile_separator);
                separator.setVisibility(View.VISIBLE);
            }
        }

        displayNameView = (TextView) findViewById(R.id.view_viewProfile_display_name);
        genderView = (TextView) findViewById(R.id.view_viewProfile_gender);
        dateOfBirthView = (TextView) findViewById(R.id.view_viewProfile_date_of_birth);
        countryView = (TextView) findViewById(R.id.view_viewProfile_country);
        studentCategoryView = (TextView) findViewById(R.id.view_viewProfile_student_category);
        facultyView = (TextView) findViewById(R.id.view_viewProfile_faculty);
        majorView = (TextView) findViewById(R.id.view_viewProfile_major);
        yearOfStudyView = (TextView) findViewById(R.id.view_viewProfile_year_of_study);
        personalDesView = (TextView) findViewById(R.id.view_viewProfile_personal_des);
        profilePictureShow = (ImageView) findViewById(R.id.image_viewProfile_picture);

        requestQueue = Volley.newRequestQueue(this);

        getFriendInfo();
        getHashtagHttpPost();

        viewBlog = (Button) findViewById(R.id.b_viewProfile_blog);
        viewBlog.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, ViewBlog.class);
                intent.putExtra("userid", friendId);
                startActivity(intent);
            }
        });

    }

    private void getFriendInfo() {
        request = new StringRequest(Request.Method.POST, profileURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    try {
                        String[] profileContent = response.split(":");
                        displayName = profileContent[1];
                        gender = profileContent[2];
                        dateOfBirth = profileContent[3];
                        country = profileContent[4];
                        studentCategory = profileContent[5];
                        faculty = profileContent[6];
                        major = profileContent[7];
                        yearOfStudy = profileContent[8];

                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(displayName + "/" + gender + "/" + dateOfBirth + "/" + country + "/" + studentCategory + "/"
                         + faculty + "/" + major + "/" + yearOfStudy);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        setFriendInfo();
                        getFriendPersonalDes();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                        warning.setText("Profile Information NOT FOUND");
                        warning.setVisibility(View.VISIBLE);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("HTTP Request for Friend information - Aray Index Out of Boundary");
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                    warning.setText(response);
                    warning.setVisibility(View.VISIBLE);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println("Failure(Personal Description): " + response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
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
                hashMap.put("id", friendId);
                hashMap.put("content", "simple");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setFriendInfo() {
        displayNameView.setText(displayName);

        if (gender.equals("M"))
            genderView.setText("Male");
        else if (gender.equals("F"))
            genderView.setText("Female");

        dateOfBirthView.setText(dateOfBirth);

        countryView.setText(country);

        if (studentCategory.equals("local"))
            studentCategoryView.setText("Local Student");
        else if (studentCategory.equals("mainland"))
            studentCategoryView.setText("Mainland Student");
        else if (studentCategory.equals("international"))
            studentCategoryView.setText("International Student");

        if (faculty.equals("SBM"))
            facultyView.setText("School of Business and Management");
        else if (faculty.equals("SSCI"))
            facultyView.setText("School of Science");
        else if (faculty.equals("SENG"))
            facultyView.setText("School of Engineering");
        else if (faculty.equals("SHSS"))
            facultyView.setText("School of Humanities and Social Science");
        else if (faculty.equals("IPO"))
            facultyView.setText("Interdisciplinary Programs Office");

        majorView.setText(major);

        if (yearOfStudy.equals("6"))
            yearOfStudyView.setText("Year 6 or more");
        else
            yearOfStudyView.setText("Year " + yearOfStudy);
    }

    private void getFriendPersonalDes() {
        request = new StringRequest(Request.Method.POST, profileURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    try {
                        String[] profileContent = response.split("-");
                        personalDes = profileContent[1];

                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("Personal Description: " + personalDes);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        personalDesView.setText(personalDes);
                        getFriendProfilePic();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                        warning.setText("Profile Information (Personal Description) NOT FOUND");
                        warning.setVisibility(View.VISIBLE);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("HTTP Request for Friend information - Aray Index Out of Boundary");
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                    warning.setText(response);
                    warning.setVisibility(View.VISIBLE);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println("Failure(Personal Description): " + response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
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
                hashMap.put("id", friendId);
                hashMap.put("content", "personalDes");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void getFriendProfilePic() {
        request = new StringRequest(Request.Method.POST, profilePicURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("base64")) {
                    String imageString = response.split(":base64,")[1];
                    byte[] imageByte = Base64.decode(imageString, Base64.DEFAULT);
                    profilePicture = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    profilePictureShow.setImageBitmap(profilePicture);

                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println("Profile picture downloaded.");
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    getFriendSwitch();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                    warning.setText("Fail to retrieve profile picture.");
                    warning.setVisibility(View.VISIBLE);
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
                hashMap.put("id", friendId);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void getFriendSwitch() {
        request = new StringRequest(Request.Method.POST, switchURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    try {
                        String[] profileContent = response.split("Success-")[1].split(":");

                        if (profileContent[0].equals("0"))
                            genderShow = false;
                        else
                            genderShow = true;

                        if (profileContent[1].equals("0"))
                            dateOfBirthShow = false;
                        else
                            dateOfBirthShow = true;

                        if (profileContent[2].equals("0"))
                            countryShow = false;
                        else
                            countryShow = true;

                        if (profileContent[3].equals("0"))
                            studentCategoryShow = false;
                        else
                            studentCategoryShow = true;

                        if (profileContent[4].equals("0"))
                            facultyShow = false;
                        else
                            facultyShow = true;

                        if (profileContent[5].equals("0"))
                            majorShow = false;
                        else
                            majorShow = true;

                        if (profileContent[6].equals("0"))
                            yearOfStudyShow = false;
                        else
                            yearOfStudyShow = true;

                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(genderShow + "/" + dateOfBirthShow + "/" + countryShow + "/" + studentCategoryShow + "/"
                                + facultyShow + "/" + majorShow + "/" + yearOfStudyShow);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        setFriendSwitch();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                        warning.setText("Profile Information (Switch) NOT FOUND");
                        warning.setVisibility(View.VISIBLE);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println("HTTP Request for Friend information - Aray Index Out of Boundary");
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_viewProfile_warning);
                    warning.setText(response);
                    warning.setVisibility(View.VISIBLE);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println("Failure(Switch): " + response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
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
                hashMap.put("id", friendId);
                hashMap.put("content", "simple");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void sendFriendshipRequest() {
        request = new StringRequest(Request.Method.POST, friendShipURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    addFriend.setText("Pending");
                    addFriend.setBackgroundColor(Color.GRAY);
                    addFriend.setClickable(false);

                    Toast.makeText(getApplicationContext(), "Request is sent. \nPlease wait for acceptance.", Toast.LENGTH_LONG).show();

                    sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String pendingList = sharedPreferences.getString("pendingList", null);
                    if (pendingList != null) {
                        pendingList = pendingList + "," + friendId;
                    }
                    else {
                        pendingList = friendId;
                    }
                    editor.putString("pendingList", pendingList);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed to send request. \nPlease try again later.", Toast.LENGTH_LONG).show();
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
                hashMap.put("friendId", friendId);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setFriendSwitch() {
        if (genderShow == false)
            genderView.setVisibility(View.GONE);

        if (dateOfBirthShow == false)
            dateOfBirthView.setVisibility(View.GONE);

        if (countryShow == false)
            countryView.setVisibility(View.GONE);

        if (studentCategoryShow == false)
            studentCategoryView.setVisibility(View.GONE);

        if (facultyShow == false)
            facultyView.setVisibility(View.GONE);

        if (majorShow == false)
            majorView.setVisibility(View.GONE);

        if (yearOfStudyShow == false)
            yearOfStudyView.setVisibility(View.GONE);
    }

    private void getHashtagHttpPost() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", friendId);

        JsonObjectRequest getRequest = new JsonObjectRequest( getHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("getHashtagHttpPost",response.toString());

                        //Toast toast = Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT);
                        //toast.show();

                        try {
                            if(response.getBoolean("success")) {
                                Log.d("getHashtagHttpPost","have hashtag");
                                showHashtag(response.getJSONArray("readHashtags"));

                            }
                            else {
                                Log.d("getHashtagHttpPost","no hashtag");
                                TextView noHashtag = (TextView) findViewById(R.id.view_viewProfile_noHashtag);
                                noHashtag.setVisibility(View.VISIBLE);
                                //Toast t = Toast.makeText(getApplicationContext(), "You have no hashtags!", Toast.LENGTH_SHORT);
                                //t.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", error.getMessage());
                    }
                }
        );

        requestQueue.add(getRequest);
    }

    public void showHashtag(JSONArray readHashtags){
        Log.d("php id", friendId);

        hashtag_id.clear();
        hashtag_content.clear();

        if (readHashtags != null) {
            for (int i=0;i<readHashtags.length();i++){
                try {
                    hashtag_id.add(readHashtags.getJSONObject(i).getString("hashtag_id"));
                    hashtag_content.add(readHashtags.getJSONObject(i).getString("hashtag_content"));
                    //Log.d("hashtag_id",readHashtags.getJSONObject(i).getString("hashtag_id"));
                    //Log.d("hashtag_content",readHashtags.getJSONObject(i).getString("hashtag_content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        hashtagTableLayout = (TableLayout) findViewById(R.id.table_viewProfile_hashtags);

        int i = 0;
        while (i < hashtag_content.size()) {
            if (i % 3 == 0) {
                tr = new TableRow(getApplicationContext());
                hashtagTableLayout.addView(tr);
            }

            final Button btn = new Button(getApplicationContext());
            btn.setText(hashtag_content.get(i));
            btn.setId(i);

            tr.addView(btn);
            i++;
        }
    }
}
