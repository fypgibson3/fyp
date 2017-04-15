package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {
    private String id;
    private String username;
    private String password;
    private Boolean remember = false;

    private String fdIdList;
    private String fdDisplayNameList;

    private static final String loginURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/loginCheck.php";
    private static final String profileURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getProfileInfo.php";
    private static final String profilePicURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getProfilePic.php";
    private static final String switchURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getSwitchInfo.php";
    private static final String getFdIdListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendIdList.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private RequestQueue requestQueue;
    private StringRequest request;
    private ImageRequest imageRequest;

    private static final String loginPreference = "LoginPreference";
    private static final String profilePreference = "ProfilePreference";
    private static final String imagePreference = "ImagePreference";
    private static final String friendListPreference = "FriendList";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");
        password = sharedPreferences.getString("PASSWORD", "");

        if (username.equals("") || password.equals("")) {
            setContentView(R.layout.activity_sign_in);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);
            setSupportActionBar(toolbar);

            Button signInButton = (Button) findViewById(R.id.b_signIn_sign_in);
            Button signUpButton = (Button) findViewById(R.id.b_signIn_sign_up);
            final Button forgotPswdButton = (Button) findViewById(R.id.b_signIn_forgot_password);

            // "Sign In" button is clicked
            signInButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignInButton();
                }
            });

            // "Register for a new account" button is clicked
            signUpButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignUpButton();
                    SignIn.this.finish();
                }
            });

            // "Forgot Password" button is clicked
            forgotPswdButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickForgotPasswordButton();
                    SignIn.this.finish();
                }
            });
        }
        else {
            httpPostRequest();
        }
    }

    // **************************************************************
    // Function: checkInput
    // Description: Input checking when "Sign In" button is clicked
    // Parameter: /
    // Return Type: /
    //***************************************************************
    private boolean checkInput() {
        EditText usernameInput = (EditText) findViewById(R.id.i_signIn_username);
        EditText passwordInput = (EditText) findViewById(R.id.i_signIn_password);
        CheckBox rememberInput = (CheckBox) findViewById(R.id.checkBox_signIn_remember);

        if (usernameInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()) {
            TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
            warning.setVisibility(View.VISIBLE);
        }
        else {
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            remember = rememberInput.isChecked();
            return true;
        }
        return false;
    }

    private String saveImageToInternalStorage(String id, Bitmap bitmap) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File imageDirectory = contextWrapper.getDir(imagePreference, Context.MODE_PRIVATE);
        File imageFile = new File(imageDirectory, id+".jpg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageFile.getAbsolutePath();
    }

    private void setSimpleProfileSharedPreference() {
        request = new StringRequest(Request.Method.POST, profileURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    try {
                        String[] profileContent = response.split(":");
                        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                        SharedPreferences.Editor profileEditor = sharedPreferences.edit();
                        profileEditor.putString("DISPLAYNAME", profileContent[1]);
                        profileEditor.putString("GENDER", profileContent[2]);
                        profileEditor.putString("BIRTHDATE", profileContent[3]);
                        profileEditor.putString("COUNTRY", profileContent[4]);
                        profileEditor.putString("STUDENTCATEGORY", profileContent[5]);
                        profileEditor.putString("FACULTY", profileContent[6]);
                        profileEditor.putString("MAJOR", profileContent[7]);
                        profileEditor.putString("YEAROFSTUDY", profileContent[8]);
                        profileEditor.commit();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                        warning.setText("Profile Information NOT FOUND");
                        warning.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                    warning.setText(response);
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
                hashMap.put("id", id);
                hashMap.put("content", "simple");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setPersonalDescriptionSharedPreference() {
        request = new StringRequest(Request.Method.POST, profileURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success-")) {
                    try {
                        String[] profileContent = response.split("Success-");
                        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                        SharedPreferences.Editor profileEditor = sharedPreferences.edit();
                        profileEditor.putString("PERSONALDES", profileContent[1]);
                        profileEditor.commit();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                        warning.setText("Personal Description Not Found");
                        warning.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                    warning.setText(response);
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
                hashMap.put("id", id);
                hashMap.put("content", "personalDes");
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setSwitchSharedPreference() {
        request = new StringRequest(Request.Method.POST, switchURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    try {
                        String[] profileContent = response.split("Success-")[1].split(":");
                        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                        SharedPreferences.Editor profileEditor = sharedPreferences.edit();

                        if (profileContent[0].equals("0"))
                            profileEditor.putBoolean("GENDER_SHOW", false);
                        else
                            profileEditor.putBoolean("GENDER_SHOW", true);

                        if (profileContent[1].equals("0"))
                            profileEditor.putBoolean("BIRTHDATE_SHOW", false);
                        else
                            profileEditor.putBoolean("BIRTHDATE_SHOW", true);

                        if (profileContent[2].equals("0"))
                            profileEditor.putBoolean("COUNTRY_SHOW", false);
                        else
                            profileEditor.putBoolean("COUNTRY_SHOW", true);

                        if (profileContent[3].equals("0"))
                            profileEditor.putBoolean("STUDENTCATEGORY_SHOW", false);
                        else
                            profileEditor.putBoolean("STUDENTCATEGORY_SHOW", true);

                        if (profileContent[4].equals("0"))
                            profileEditor.putBoolean("FACULTY_SHOW", false);
                        else
                            profileEditor.putBoolean("FACULTY_SHOW", true);

                        if (profileContent[5].equals("0"))
                            profileEditor.putBoolean("MAJOR_SHOW", false);
                        else
                            profileEditor.putBoolean("MAJOR_SHOW", true);

                        if (profileContent[6].equals("0"))
                            profileEditor.putBoolean("YEAROFSTUDY_SHOW", false);
                        else
                            profileEditor.putBoolean("YEAROFSTUDY_SHOW", true);

                        profileEditor.commit();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                        warning.setText("Profile Switch Information NOT FOUND");
                        warning.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                    warning.setText("Fail to retrieve profile. Please try again.");
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
                hashMap.put("id", id);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setProfilePicSharedPreference() {
        request = new StringRequest(Request.Method.POST, profilePicURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("base64")) {
                    String imageString = response.split(":base64,")[1];
                    byte[] imageByte = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

                    sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorImage = sharedPreferences.edit();
                    editorImage.putString("PROFILEPIC", saveImageToInternalStorage(id, image));
                    editorImage.commit();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
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
                hashMap.put("id", id);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void setFriendListIdPreference() {
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

                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdIdListEditor = sharedPreferences.edit();
                    fdIdListEditor.putString("FDLIST_ID", fdList);
                    fdIdListEditor.commit();

                    setFriendListDisplayNamePreference();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
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

    private void setFriendListDisplayNamePreference() {
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

                    SharedPreferences sharedPreferences = getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdNameListEditor = sharedPreferences.edit();
                    fdNameListEditor.putString("FDLIST_DISPLAYNAME", fdList);
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
                hashMap.put("list", fdIdList);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    //**************************************************************
    // Function: httpPostRequest
    // Description: To send HTTP post request to server
    // Parameter: CheckBox
    // Return Type: Boolean
    //**************************************************************
    private void httpPostRequest() {
        // New request for HTTP Post request
        request = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    String[] split = response.split("-");
                    id = split[split.length-1];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ID", split[split.length-1]);
                    if (remember) {
                        editor.putBoolean("REMEMBER", remember);
                        editor.putString("USERNAME", username);
                        editor.putString("PASSWORD", password);
                    }
                    else {
                        editor.putBoolean("REMEMBER", remember);
                    }
                    editor.commit();

                    setSimpleProfileSharedPreference();
                    setPersonalDescriptionSharedPreference();
                    setProfilePicSharedPreference();
                    setSwitchSharedPreference();
                    setFriendListIdPreference();

                    Intent intentOK = new Intent(getApplicationContext(), USTSocialAppMain.class);
                    //intentOK.putExtra("id", split[split.length -1]);
                    startActivity(intentOK);
                    SignIn.this.finish();
                }
                else if (response.contains("Register")) {
                    String[] split = response.split("-");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (remember) {
                        editor.putBoolean("REMEMBER", remember);
                    }
                    else {
                        editor.putBoolean("REMEMBER", remember);
                    }
                    editor.commit();

                    Intent intentRegister = new Intent(getApplicationContext(), ProfileSettingBasic.class);
                    intentRegister.putExtra("id", split[split.length -1]);
                    startActivity(intentRegister);
                    SignIn.this.finish();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                    warning.setText(response);
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
                hashMap.put("username", username);
                hashMap.put("password", password);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    // **************************************************************
    // Function: onClickSignInButton
    // Description: HTTP request when "Sign In" button is clicked
    // Parameter: /
    // Return Type: /
    //***************************************************************
    private void onClickSignInButton() {
        if (checkInput())
            httpPostRequest();
    }

    // **************************************************************
    // Function: onClickSignUpButton
    // Description: Action when "Sign Up" button is clicked (Direct to Sign Up page)
    // Parameter: /
    // Return Type: /
    //***************************************************************
    private void onClickSignUpButton() {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
        finish();
    }

    // **************************************************************
    // Function: onClickForgotPasswordButton
    // Description: Action when "Forgot Password" button is clicked (Direct to Forgot Password page)
    // Parameter: /
    // Return Type: /
    //***************************************************************
    private void onClickForgotPasswordButton() {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(intent);
        finish();
    }

}
