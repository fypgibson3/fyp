package com.csefyp2016.gib3.ustsocialapp;

import android.content.ContentProvider;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Connection;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class SignIn extends AppCompatActivity {
    private String username;
    private String password;
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/check.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);
        setSupportActionBar(toolbar);

        EditText usernameInput = (EditText) findViewById(R.id.i_signIn_username);
        EditText passwordInput = (EditText) findViewById(R.id.i_signIn_password);
        requestQueue = Volley.newRequestQueue(this);

        Button signInButton = (Button) findViewById(R.id.b_signIn_sign_in);
        signInButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignInButton();

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.names().get(0).toString().equals("Success")) {
                                startActivity(new Intent(SignIn.this, USTSocialAppMain.class));
                            }
                            else {
                                TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
                                warning.setText(data.names().get(0).toString());
                                warning.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        hashMap.put("username", username);
                        hashMap.put("password", password);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        Button signUpButton = (Button) findViewById(R.id.b_signIn_sign_up);
        signUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUpButton();
            }
        });


    }

    private void onClickSignInButton() {
        EditText usernameInput = (EditText) findViewById(R.id.i_signIn_username);
        EditText passwordInput = (EditText) findViewById(R.id.i_signIn_password);

        if (usernameInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()) {
            TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
            warning.setVisibility(View.VISIBLE);
        }
        else {
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
        }
    }

    private void onClickSignUpButton() {
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

}
