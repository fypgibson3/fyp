package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ForgotPassword extends AppCompatActivity {
    private static final String URL = "";
    private String username;
    private String email;

    EditText usernameInput;
    EditText emailInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameInput = (EditText) findViewById(R.id.i_forgotPassword_username);
        emailInput = (EditText) findViewById(R.id.i_forgotPassword_email);

        Button submitButton = (Button) findViewById(R.id.b_forgotPassword_submit);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameInput.getText().toString();
                email = emailInput.getText().toString();

                if (true) {
                    startActivity(new Intent(ForgotPassword.this, ConfirmResetPassword.class));
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_forgotPassword_warning);
                    warning.setText("No record for your username or email. Please use the email during the registration and try again.");
                    warning.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}
