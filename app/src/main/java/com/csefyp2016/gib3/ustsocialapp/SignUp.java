package com.csefyp2016.gib3.ustsocialapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String fullName;
    private String itsc;
    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/createNewAccount.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        Button signUpButton = (Button) findViewById(R.id.b_signUp_next);
        signUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubmitButton();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.b_signUp_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancelButton();
            }
        });
    }

    // **************************************************************
    // Function: checkFilledEditText
    // Description: To check if an EditText field has input
    // Parameter: String
    // Return Type: Boolean
    //***************************************************************
    private boolean checkFilledEditText(String input) {
        if (input.isEmpty())
            return false;
        else
            return true;
    }

    // **************************************************************
    // Function: checkInput
    // Description: To check if all EditText fields have input
    // Parameter: N.A.
    // Return Type: Boolean
    //***************************************************************
    private boolean checkInput() {
        if (checkFilledEditText(username) && checkFilledEditText(password) && checkFilledEditText(confirmPassword) && checkFilledEditText(email)
                && checkFilledEditText(fullName) && checkFilledEditText(itsc))
            return true;
        else
            return false;
    }

    // **************************************************************
    // Function: duobleCheckPassword
    // Description: To check if "password" and "confirm_password" are matched
    // Parameter: String, String
    // Return Type: Boolean
    //***************************************************************
    private boolean doubleCheckPassword() {
        if (password.compareTo(confirmPassword) == 0)
            return true;
        else
            return false;
    }

    //**************************************************************
    // Function: checkAgreeCheckBox
    // Description: To check if  a check box is checked
    // Parameter: CheckBox
    // Return Type: Boolean
    //**************************************************************
    private boolean checkAgreeCheckBox (CheckBox box) {
        if (box.isChecked())
            return true;
        else
            return false;
    }

    //**************************************************************
    // Function: httpPostRequest
    // Description: To send HTTP post request to server
    // Parameter: /
    // Return Type: /
    //**************************************************************
    private void httpPostRequest() {
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    String[] split = response.split("-");

                    Intent intentOK = new Intent(getApplicationContext(), ProfileSettingBasic.class);
                    intentOK.putExtra("id", split[split.length-1]);
                    startActivity(intentOK);
                }
                else {
                    String errorText = "";
                    if (response.contains("username")) {
                        errorText = "Username is used. Please try another one.";
                    }
                    else if (response.contains("email")) {
                        errorText = "Email is used. Pleas try another one.";
                    }
                    else if (response.contains("name")) {
                        errorText = "According to the record, it seems you have already registered for the account. " +
                                "If you forgot your password, please click \"Forgot Password\" in the Sign In page to reset your password. " +
                                "If not, please check your full name input.";
                    }
                    else if (response.contains("itsc")) {
                        errorText = "According to the record, it seems you have already registered for the account. " +
                                "If you forgot your password, please click \"Forgot Password\" in the Sign In page to reset your password. " +
                                "If not, please check your ITSC input.";
                    }

                    TextView warning = (TextView) findViewById(R.id.view_signUp_warning);
                    warning.setText(errorText);
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
                hashMap.put("email", email);
                hashMap.put("fullName", fullName);
                hashMap.put("itsc", itsc);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    //**************************************************************
    // Function: onClickSubmitButton
    // Description: Action when clicking "Submit" button
    // Parameter: /
    // Return Type: /
    //**************************************************************
    private void onClickSubmitButton() {
        EditText usernameInput = (EditText) findViewById(R.id.i_signUp_username);
        EditText passwordInput = (EditText) findViewById(R.id.i_signUp_password);
        EditText passwordConfirmInput = (EditText) findViewById(R.id.i_signUp_password_confirm);
        EditText emailInput = (EditText) findViewById(R.id.i_signUp_email);
        EditText fullNameInput = (EditText) findViewById(R.id.i_signUp_full_name);
        EditText itscInput = (EditText) findViewById(R.id.i_signUp_itsc);
        CheckBox agreeCheckBoxInput = (CheckBox) findViewById(R.id.checkBox_signUp_agree);

        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        confirmPassword = passwordConfirmInput.getText().toString();
        email = emailInput.getText().toString();
        fullName = fullNameInput.getText().toString();
        itsc = itscInput.getText().toString();

        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
        TextView debug = (TextView) findViewById(R.id.debug_signUp);
        debug.setText(username + "/n" + password + "/n" + confirmPassword + "/n" + email + "/n" + fullName + "/n" + itsc);
        debug.setVisibility(View.VISIBLE);
        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

        if (checkInput() == false) {
            TextView warning = (TextView) findViewById(R.id.view_signUp_warning);
            warning.setText("Please fill in all the fields.");
            warning.setVisibility(View.VISIBLE);
        }
        else if (doubleCheckPassword() == false) {
            TextView warning = (TextView) findViewById(R.id.view_signUp_warning);
            warning.setText("Your password inputs are mismatched.");
            warning.setVisibility(View.VISIBLE);
        }
        else if (checkAgreeCheckBox(agreeCheckBoxInput) == false) {
            TextView warning = (TextView) findViewById(R.id.view_signUp_warning);
            warning.setText("Please click the check box to indicate yor have understand the terms of services and regulations.");
            warning.setVisibility(View.VISIBLE);
        }
        else {
            httpPostRequest();
        }
    }

    //**************************************************************
    // Function: onClickCancelButton
    // Description: Action when "Cancel" button is clicked
    // Parameter: /
    // Return Type: /
    //**************************************************************
    private void onClickCancelButton() {
        final AlertDialog.Builder alertWindowBuilder = new AlertDialog.Builder(this);
        alertWindowBuilder.setTitle("Warning!");
        alertWindowBuilder.setMessage("Do you wish to discard your registration?");
        alertWindowBuilder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();
            }
        });
        alertWindowBuilder.setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertWindowBuilder.create();
        alertDialog.show();
    }
}
