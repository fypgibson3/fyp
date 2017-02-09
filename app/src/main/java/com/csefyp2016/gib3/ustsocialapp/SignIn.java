package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignIn extends AppCompatActivity {
    private String username;
    private String password;
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/loginCheck.php";
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

        // "Sign In" button is clicked
        signInButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignInButton();

                // New request for HTTP Post request
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                    @Override
                    // Response to request result
                    public void onResponse(String response) {
                            if (response.contains("Success")) {
                                startActivity(new Intent(SignIn.this, USTSocialAppMain.class));
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
        });

        Button signUpButton = (Button) findViewById(R.id.b_signIn_sign_up);

        // "Register for a new account" button is clicked
        signUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUpButton();
            }
        });
    }

    // **************************************************************
    // Function: onClickSignInButton
    // Description: Action when "Sign In" button is clicked (Input checking)
    // Parameter: /
    // Return Type: /
    //***************************************************************
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

    // **************************************************************
    // Function: onClickSignUpButton
    // Description: Action when "Sign Up" button is clicked (Direct to Sign Up page)
    // Parameter: /
    // Return Type: /
    //***************************************************************
    private void onClickSignUpButton() {
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

}
