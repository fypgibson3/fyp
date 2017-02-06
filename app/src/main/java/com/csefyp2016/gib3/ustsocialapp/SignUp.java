package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String fullName;
    private String itsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button signUpButton = (Button) findViewById(R.id.b_signUp_sign_up);
        signUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                else
                    startActivity(new Intent(SignUp.this, ProfileSettingBasic.class));
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

}
