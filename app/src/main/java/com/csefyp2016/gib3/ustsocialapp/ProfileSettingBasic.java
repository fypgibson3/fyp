package com.csefyp2016.gib3.ustsocialapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingBasic extends AppCompatActivity {
    private String id;

    private Boolean genderShow;
    private Boolean dateOfBirthShow;
    private Boolean countryShow;
    private Boolean studentCategoryShow;
    private Boolean facultyShow;
    private Boolean majorShow;
    private Boolean yearOfStudyShow;

    private String displayName;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;

    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/checkDisplayNameUnique.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_basic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent previousIntent = getIntent();
        Bundle previousContent = previousIntent.getExtras();
        if (previousContent != null) {
            id = previousContent.getString("id");
        }

        requestQueue = Volley.newRequestQueue(this);

        Spinner countrySpinner = (Spinner) findViewById(R.id.op_profileSettingBasic_country);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> countrySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.countryList, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        // Apply the adapter to the spinner
        countrySpinner.setAdapter(countrySpinnerAdapter);

        Spinner facultySpinner = (Spinner) findViewById(R.id.op_profileSettingBasic_faculty);
        ArrayAdapter<CharSequence> facultySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.facultyList, android.R.layout.simple_spinner_item);
        facultySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        facultySpinner.setAdapter(facultySpinnerAdapter);

        Spinner majorSpinner = (Spinner) findViewById(R.id.op_profileSettingBasic_major);
        ArrayAdapter<CharSequence> majorSpinnerAdapter;
        majorSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.majorList, android.R.layout.simple_spinner_item);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        majorSpinner.setAdapter(majorSpinnerAdapter);

        Spinner yearOfStudySpinner = (Spinner) findViewById(R.id.op_profileSettingBasic_year_of_study);
        ArrayAdapter<CharSequence> yearOfStudySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.yearOfStudyList, android.R.layout.simple_spinner_item);
        yearOfStudySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        yearOfStudySpinner.setAdapter(yearOfStudySpinnerAdapter);

        Button nextButton = (Button) findViewById(R.id.b_profileSettingBasic_next);
        nextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSwitchOption();
                if (checkAndGetInput())
                    checkDisplayNameUsed();
            }

        });

        Button cancelButton = (Button) findViewById(R.id.b_profileSettingBasic_cancel);
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
    // Function: checkDisplayNameUsed
    // Description: To check if the Display Name is used from datebase
    // Parameter: /
    // Return Type: /
    // **************************************************************
    private void checkDisplayNameUsed() {
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Unique")) {
                    Intent intent = new Intent(getApplicationContext(), ProfileSettingMore.class);
                    intent.putExtra("id", id);
                    intent.putExtra("displayName", displayName);
                    intent.putExtra("gender", gender);
                    intent.putExtra("dateOfBirth", dateOfBirth);
                    intent.putExtra("country", country);
                    intent.putExtra("studentCategory", studentCategory);
                    intent.putExtra("faculty", faculty);
                    intent.putExtra("major", major);
                    intent.putExtra("yearOfStudy", yearOfStudy);
                    intent.putExtra("genderShow", genderShow);
                    intent.putExtra("dateOfBirthShow", dateOfBirthShow);
                    intent.putExtra("countryShow", countryShow);
                    intent.putExtra("studentCategoryShow", studentCategoryShow);
                    intent.putExtra("facultyShow", facultyShow);
                    intent.putExtra("majorShow", majorShow);
                    intent.putExtra("yearOfStudyShow", yearOfStudyShow);
                    startActivity(intent);
                    finish();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Your display name \"" + displayName + "\" has already been used. Please try another one.");
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
                hashMap.put("displayName", displayName);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    //**************************************************************
    // Function: getSwitchOption
    // Description: To get the input of Switches
    // Parameter: /
    // Return Type: /
    //**************************************************************
    private void getSwitchOption() {
        Switch genderSwitch = (Switch) findViewById(R.id.s_profileSettingBasic_gender);
        genderShow = genderSwitch.isChecked();
        Switch dateOfBirthSwitch = (Switch) findViewById(R.id.s_profileSettingBasic_date_of_birth);
        dateOfBirthShow = dateOfBirthSwitch.isChecked();
        Switch countrySwitch = (Switch) findViewById(R.id.s_profileSettingBasic_country);
        countryShow = countrySwitch.isChecked();
        Switch studentCategorySwitch = (Switch) findViewById(R.id.s_profileSettingBasic_student_category);
        studentCategoryShow = studentCategorySwitch.isChecked();
        Switch facultySwitch = (Switch) findViewById(R.id.s_profileSettingBasic_faculty);
        facultyShow = facultySwitch.isChecked();
        Switch majorSwitch = (Switch) findViewById(R.id.s_profileSettingBasic_major);
        majorShow = majorSwitch.isChecked();
        Switch yearOfStudySwitch = (Switch) findViewById(R.id.s_profileSettingBasic_year_of_study);
        yearOfStudyShow = yearOfStudySwitch.isChecked();
    }

    //**************************************************************
    // Function: checkAndGetInput
    // Description: To check user inputs and store them
    // Parameter: /
    // Return Type: Boolean
    //**************************************************************
    private boolean checkAndGetInput() {
        EditText displayNameInput = (EditText) findViewById(R.id.i_profileSettingBasic_display_name);
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.rgp_profileSettingBasic_gender);
        RadioButton defaultGenderInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_gender_default);
        RadioButton maleGenderInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_gender_male);
        RadioButton femaleGenderInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_gender_female);
        DatePicker dateOfBirthInput = (DatePicker) findViewById(R.id.dp_profileSettingBasic_date_of_birth);
        Spinner countryInput = (Spinner) findViewById(R.id.op_profileSettingBasic_country);
        RadioGroup studentCategoryGroup = (RadioGroup) findViewById(R.id.rgp_profileSettingBasic_student_category);
        RadioButton defaultStudentCategoryInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_student_category_default);
        RadioButton localStudentCategoryInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_student_category_local);
        RadioButton mainlandStudentCategoryInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_student_category_mainland);
        RadioButton internationalStudentCategoryInput = (RadioButton) findViewById(R.id.rb_profileSettingBasic_student_category_international);
        Spinner facultyInput = (Spinner) findViewById(R.id.op_profileSettingBasic_faculty);
        Spinner majorInput = (Spinner) findViewById(R.id.op_profileSettingBasic_major);
        Spinner yearOfStudyInput = (Spinner) findViewById(R.id.op_profileSettingBasic_year_of_study);

        if (checkFilledEditText(displayNameInput.getText().toString()) == false) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Your Display Name must not be empty. Please fill in your Display Name.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (genderGroup.getCheckedRadioButtonId() == defaultGenderInput.getId()) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Gender.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (countryInput.getSelectedItemPosition() == 0) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Country.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (studentCategoryGroup.getCheckedRadioButtonId() == defaultStudentCategoryInput.getId()) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Student Category.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (facultyInput.getSelectedItemPosition() == 0) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Faculty.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (majorInput.getSelectedItemPosition() == 0) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Major.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (yearOfStudyInput.getSelectedItemPosition() == 0) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Please select your Year Of Study.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            displayName = displayNameInput.getText().toString();

            if (genderGroup.getCheckedRadioButtonId() == maleGenderInput.getId())
                gender = "M";
            else
                gender = "F";

            Date unformatedDate = new Date(dateOfBirthInput.getYear()-1900, dateOfBirthInput.getMonth(), dateOfBirthInput.getDayOfMonth());
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            dateOfBirth = formatDate.format(unformatedDate);

            country = countryInput.getSelectedItem().toString();

            if (studentCategoryGroup.getCheckedRadioButtonId() == localStudentCategoryInput.getId())
                studentCategory = "local";
            else if (studentCategoryGroup.getCheckedRadioButtonId() == mainlandStudentCategoryInput.getId())
                studentCategory = "mainland";
            else
                studentCategory = "international";

            switch (facultyInput.getSelectedItem().toString()) {
                case "School of Business and Management":
                    faculty = "SBM";
                    break;
                case "School of Science":
                    faculty = "SSCI";
                    break;
                case "School of Engineering":
                    faculty = "SENG";
                    break;
                case "School of Humanities and Social Science":
                    faculty = "SHSS";
                    break;
                case "Interdisciplinary Programs Office":
                    faculty = "IPO";
                    break;
            }


            major = majorInput.getSelectedItem().toString();

            if (yearOfStudyInput.getSelectedItem().toString().contains("6"))
                yearOfStudy = "6";
            else
                yearOfStudy = yearOfStudyInput.getSelectedItem().toString();

            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
            TextView debug = (TextView) findViewById(R.id.debug_profileSettingBasic);
            debug.setText(id + "\n" + displayName + "/n" + gender + "/n" + dateOfBirth + "/n" + country + "/n" + studentCategory + "/n" + faculty + "/n" + major + "/n" + yearOfStudy + "\n" +
                    genderShow + "/" + dateOfBirthShow + "/" + countryShow + "/" + studentCategoryShow + "/" + facultyShow + "/" + majorShow + "/" + yearOfStudyShow);
            debug.setVisibility(View.VISIBLE);
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

            return true;
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
        alertWindowBuilder.setMessage("Do you wish to discard your profile setup? \n\nNOTE:\nProfile setup is an essential step to successfully register for an account. " +
                "You CANNOT use the UST Socicial App functions if you do not finish profile setup.");
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
