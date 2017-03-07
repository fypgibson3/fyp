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
                startActivity(new Intent(SignIn.this, ForgotPassword.class ));
                SignIn.this.finish();
            }
        });
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

        if (usernameInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()) {
            TextView warning = (TextView) findViewById(R.id.view_signIn_login_fail);
            warning.setVisibility(View.VISIBLE);
        }
        else {
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            return true;
        }
        return false;
    }

    //**************************************************************
    // Function: httpPostRequest
    // Description: To send HTTP post request to server
    // Parameter: CheckBox
    // Return Type: Boolean
    //**************************************************************
    private void httpPostRequest() {
        // New request for HTTP Post request
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    String[] split = response.split("-");
                    Intent intentOK = new Intent(getApplicationContext(), USTSocialAppMain.class);
                    intentOK.putExtra("id", split[split.length -1]);
                    startActivity(intentOK);
                    SignIn.this.finish();
                }
                else if (response.contains("Register")) {
                    String[] split = response.split("-");
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
