package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private EditText displayNameInput;
    private EditText personalDesInput;
    private RadioGroup genderGroup;
    private RadioButton defaultGenderInput;
    private RadioButton maleGenderInput;
    private RadioButton femaleGenderInput;
    private DatePicker dateOfBirthInput;
    private Spinner countryInput;
    private RadioGroup studentCategoryGroup;
    private RadioButton defaultStudentCategoryInput;
    private RadioButton localStudentCategoryInput;
    private RadioButton mainlandStudentCategoryInput;
    private RadioButton internationalStudentCategoryInput;
    private Spinner facultyInput;
    private Spinner majorInput;
    private Spinner yearOfStudyInput;

    private Switch genderShowInput;
    private Switch dateOfBirthShowInput;
    private Switch countryShowInput;
    private Switch studentCategoryShowInput;
    private Switch facultyShowInput;
    private Switch majorShowInput;
    private Switch yearOfStudyShowInput;

    private String id;

    private Boolean genderShow;
    private Boolean dateOfBirthShow;
    private Boolean countryShow;
    private Boolean studentCategoryShow;
    private Boolean facultyShow;
    private Boolean majorShow;
    private Boolean yearOfStudyShow;

    private Boolean genderShowNew;
    private Boolean dateOfBirthShowNew;
    private Boolean countryShowNew;
    private Boolean studentCategoryShowNew;
    private Boolean facultyShowNew;
    private Boolean majorShowNew;
    private Boolean yearOfStudyShowNew;

    private String displayName;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;
    private String personalDes;

    private String displayNameNew;
    private String genderNew;
    private String dateOfBirthNew;
    private String countryNew;
    private String studentCategoryNew;
    private String facultyNew;
    private String majorNew;
    private String yearOfStudyNew;
    private String personalDesNew;

    private static final String infoUpdateURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/profileInfoUpdate.php";
    private static final String switchUpdateURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/profileSwitchUpdate.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private static final String profilePreference = "ProfilePreference";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        optionListsSetup();

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);
        setProfileInfo();
        setProfileSwitch();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Button doneButton = (Button) findViewById(R.id.b_editProfile_done);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getAndCheckInfoInput()) {
                    getAndCheckSwitchInput();
                    httpInfoPostRequest();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.b_editProfile_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancelButton();
            }
        });
    }

    private void optionListsSetup() {
        Spinner countrySpinner = (Spinner) findViewById(R.id.op_editProfile_country);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> countrySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.countryList, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        // Apply the adapter to the spinner
        countrySpinner.setAdapter(countrySpinnerAdapter);

        Spinner facultySpinner = (Spinner) findViewById(R.id.op_editProfile_faculty);
        ArrayAdapter<CharSequence> facultySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.facultyList, android.R.layout.simple_spinner_item);
        facultySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        facultySpinner.setAdapter(facultySpinnerAdapter);

        Spinner majorSpinner = (Spinner) findViewById(R.id.op_editProfile_major);
        ArrayAdapter<CharSequence> majorSpinnerAdapter;
        majorSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.majorList, android.R.layout.simple_spinner_item);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        majorSpinner.setAdapter(majorSpinnerAdapter);

        Spinner yearOfStudySpinner = (Spinner) findViewById(R.id.op_editProfile_year_of_study);
        ArrayAdapter<CharSequence> yearOfStudySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.yearOfStudyList, android.R.layout.simple_spinner_item);
        yearOfStudySpinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        yearOfStudySpinner.setAdapter(yearOfStudySpinnerAdapter);
    }

    private void setProfileInfo() {
        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
        displayName = sharedPreferences.getString("DISPLAYNAME", null);
        gender = sharedPreferences.getString("GENDER", null);
        dateOfBirth = sharedPreferences.getString("BIRTHDATE", null);
        country = sharedPreferences.getString("COUNTRY", null);
        studentCategory = sharedPreferences.getString("STUDENTCATEGORY", null);
        faculty = sharedPreferences.getString("FACULTY", null);
        major = sharedPreferences.getString("MAJOR", null);
        yearOfStudy = sharedPreferences.getString("YEAROFSTUDY", null);
        personalDes = sharedPreferences.getString("PERSONALDES", null);

        displayNameInput = (EditText) findViewById(R.id.i_editProfile_display_name);
        personalDesInput = (EditText) findViewById(R.id.i_editProfile_personal_des);
        genderGroup = (RadioGroup) findViewById(R.id.rgp_editProfile_gender);
        defaultGenderInput = (RadioButton) findViewById(R.id.rb_editProfile_gender_default);
        maleGenderInput = (RadioButton) findViewById(R.id.rb_editProfile_gender_male);
        femaleGenderInput = (RadioButton) findViewById(R.id.rb_editProfile_gender_female);
        dateOfBirthInput = (DatePicker) findViewById(R.id.dp_editProfile_date_of_birth);
        countryInput = (Spinner) findViewById(R.id.op_editProfile_country);
        studentCategoryGroup = (RadioGroup) findViewById(R.id.rgp_editProfile_student_category);
        defaultStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_default);
        localStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_local);
        mainlandStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_mainland);
        internationalStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_international);
        facultyInput = (Spinner) findViewById(R.id.op_editProfile_faculty);
        majorInput = (Spinner) findViewById(R.id.op_editProfile_major);
        yearOfStudyInput = (Spinner) findViewById(R.id.op_editProfile_year_of_study);

        displayNameInput.setText(displayName);
        personalDesInput.setText(personalDes);

        if (gender.equals("M"))
            genderGroup.check(R.id.rb_editProfile_gender_male);
        else if (gender.equals("F"))
            genderGroup.check(R.id.rb_editProfile_gender_female);
        else
            genderGroup.check(R.id.rb_editProfile_gender_default);

        String[] yearMonthDate = dateOfBirth.split("-");
        dateOfBirthInput.updateDate(Integer.parseInt(yearMonthDate[0]), Integer.parseInt(yearMonthDate[1])-1, Integer.parseInt(yearMonthDate[2]));

        countryInput.setSelection(((ArrayAdapter)countryInput.getAdapter()).getPosition(country));

        if (studentCategory.equals("local"))
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_local);
        else if (studentCategory.equals("mainland"))
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_mainland);
        else if (studentCategory.equals("international"))
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_international);
        else
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_default);

        if (faculty.equals("SBM"))
            facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition("School of Business and Management"));
        else if (faculty.equals("SSCI"))
            facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition("School of Science"));
        else if (faculty.equals("SENG"))
            facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition("School of Engineering"));
        else if (faculty.equals("SHSS"))
            facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition("School of Humanities and Social Science"));
        else if (faculty.equals("IPO"))
            facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition("Interdisciplinary Programs Office"));
        else
            facultyInput.setSelection(0);

        majorInput.setSelection(((ArrayAdapter)majorInput.getAdapter()).getPosition(major));

        if (yearOfStudy.equals("6"))
            yearOfStudyInput.setSelection(((ArrayAdapter)yearOfStudyInput.getAdapter()).getPosition("6 or more"));
        else
            yearOfStudyInput.setSelection(((ArrayAdapter)yearOfStudyInput.getAdapter()).getPosition(yearOfStudy));
    }

    private void setProfileSwitch() {
        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
        genderShow = sharedPreferences.getBoolean("GENDER_SHOW", true);
        dateOfBirthShow = sharedPreferences.getBoolean("BIRTHDATE_SHOW", true);
        countryShow = sharedPreferences.getBoolean("COUNTRY_SHOW", true);
        studentCategoryShow = sharedPreferences.getBoolean("STUDENTCATEGORY_SHOW", true);
        facultyShow = sharedPreferences.getBoolean("FACULTY_SHOW", true);
        majorShow = sharedPreferences.getBoolean("MAJOR_SHOW", true);
        yearOfStudyShow = sharedPreferences.getBoolean("YEAROFSTUDY_SHOW", true);

        genderShowInput = (Switch) findViewById(R.id.s_editProfile_gender);
        dateOfBirthShowInput = (Switch) findViewById(R.id.s_editProfile_date_of_birth);
        countryShowInput = (Switch) findViewById(R.id.s_editProfile_country);
        studentCategoryShowInput = (Switch) findViewById(R.id.s_editProfile_student_category);
        facultyShowInput = (Switch) findViewById(R.id.s_editProfile_faculty);
        majorShowInput = (Switch) findViewById(R.id.s_editProfile_major);
        yearOfStudyShowInput = (Switch) findViewById(R.id.s_editProfile_year_of_study);

        genderShowInput.setChecked(genderShow);
        dateOfBirthShowInput.setChecked(dateOfBirthShow);
        countryShowInput.setChecked(countryShow);
        studentCategoryShowInput.setChecked(studentCategoryShow);
        facultyShowInput.setChecked(facultyShow);
        majorShowInput.setChecked(majorShow);
        yearOfStudyShowInput.setChecked(yearOfStudyShow);
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

    private boolean getAndCheckInfoInput() {
        if (checkFilledEditText(displayNameInput.getText().toString()) == false) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Your Display Name must not be empty. Please fill in your Display Name.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (checkFilledEditText(personalDesInput.getText().toString()) == false) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
            warning.setText("Your Personal Description must not be empty. Please fill in your Personal Description.");
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
            displayNameNew = displayNameInput.getText().toString();
            personalDesNew = personalDesInput.getText().toString();

            switch (genderGroup.getCheckedRadioButtonId()) {
                case R.id.rb_editProfile_gender_male:
                    genderNew = "M";
                    break;
                case R.id.rb_editProfile_gender_female:
                    genderNew = "F";
                    break;
            }

            Date unformatedDate = new Date(dateOfBirthInput.getYear()-1900, dateOfBirthInput.getMonth(), dateOfBirthInput.getDayOfMonth());
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            dateOfBirthNew = formatDate.format(unformatedDate);

            countryNew = countryInput.getSelectedItem().toString();

            switch (studentCategoryGroup.getCheckedRadioButtonId()) {
                case R.id.rb_editProfile_student_category_local:
                    studentCategoryNew = "local";
                    break;
                case R.id.rb_editProfile_student_category_mainland:
                    studentCategoryNew = "mainland";
                    break;
                case R.id.rb_editProfile_student_category_international:
                    studentCategoryNew = "international";
                    break;
            }

            switch (facultyInput.getSelectedItem().toString()) {
                case "School of Business and Management":
                    facultyNew = "SBM";
                    break;
                case "School of Science":
                    facultyNew = "SSCI";
                    break;
                case "School of Engineering":
                    facultyNew = "SENG";
                    break;
                case "School of Humanities and Social Science":
                    facultyNew = "SHSS";
                    break;
                case "Interdisciplinary Programs Office":
                    facultyNew = "IPO";
                    break;
            }

            majorNew = majorInput.getSelectedItem().toString();

            if (yearOfStudyInput.getSelectedItem().toString().contains("6"))
                yearOfStudyNew = "6";
            else
                yearOfStudyNew = yearOfStudyInput.getSelectedItem().toString();

            return true;
        }
    }

    private void getAndCheckSwitchInput() {
        genderShowNew = genderShowInput.isChecked();
        dateOfBirthShowNew = dateOfBirthShowInput.isChecked();
        countryShowNew = countryShowInput.isChecked();
        studentCategoryShowNew = studentCategoryShowInput.isChecked();
        facultyShowNew = facultyShowInput.isChecked();
        majorShowNew = majorShowInput.isChecked();
        yearOfStudyShowNew = yearOfStudyShowInput.isChecked();

        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
        System.out.println("EditProfile: getAndCheckSwitchInput() is " + yearOfStudyShowNew);
        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
    }

    private void httpInfoPostRequest() {
        request = new StringRequest(Request.Method.POST, infoUpdateURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorInfo = sharedPreferences.edit();
                    editorInfo.putString("DISPLAYNAME", displayNameNew);
                    editorInfo.putString("GENDER", genderNew);
                    editorInfo.putString("BIRTHDATE", dateOfBirthNew);
                    editorInfo.putString("COUNTRY", countryNew);
                    editorInfo.putString("STUDENTCATEGORY", studentCategoryNew);
                    editorInfo.putString("FACULTY", facultyNew);
                    editorInfo.putString("MAJOR", majorNew);
                    editorInfo.putString("YEAROFSTUDY", yearOfStudyNew);
                    editorInfo.putString("PERSONALDES", personalDesNew);
                    editorInfo.commit();

                    httpSwitchPostRequest();
                }
                else if (response.contains("Existed")){
                    TextView warning = (TextView) findViewById(R.id.view_editProfile_warning);
                    warning.setText("Your display name \"" + displayNameNew + "\" has already been used. Please try another one.");
                    warning.setVisibility(View.VISIBLE);
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_editProfile_warning);
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
                hashMap.put("ID", id);
                hashMap.put("displayName", displayNameNew);
                hashMap.put("gender", genderNew);
                hashMap.put("dateOfBirth", dateOfBirthNew);
                hashMap.put("country", countryNew);
                hashMap.put("studentCategory", studentCategoryNew);
                hashMap.put("faculty", facultyNew);
                hashMap.put("major", majorNew);
                hashMap.put("yearOfStudy", yearOfStudyNew);
                hashMap.put("personalDes", personalDesNew);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void httpSwitchPostRequest() {
        request = new StringRequest(Request.Method.POST, switchUpdateURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorInfo = sharedPreferences.edit();
                    editorInfo.putBoolean("GENDER_SHOW", genderShowNew);
                    editorInfo.putBoolean("BIRTHDATE_SHOW", dateOfBirthShowNew);
                    editorInfo.putBoolean("COUNTRY_SHOW", countryShowNew);
                    editorInfo.putBoolean("STUDENTCATEGORY_SHOW", studentCategoryShowNew);
                    editorInfo.putBoolean("FACULTY_SHOW", facultyShowNew);
                    editorInfo.putBoolean("MAJOR_SHOW", majorShowNew);
                    editorInfo.putBoolean("YEAROFSTUDY_SHOW", yearOfStudyShowNew);
                    editorInfo.commit();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
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
                hashMap.put("ID", id);
                hashMap.put("genderShow", genderShowNew.toString());
                hashMap.put("dateOfBirthShow", dateOfBirthShowNew.toString());
                hashMap.put("countryShow", countryShowNew.toString());
                hashMap.put("studentCategoryShow", studentCategoryShowNew.toString());
                hashMap.put("facultyShow", facultyShowNew.toString());
                hashMap.put("majorShow", majorShowNew.toString());
                hashMap.put("yearOfStudyShow", yearOfStudyShowNew.toString());

                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                System.out.println("EditProfile: httpPostRequest is " + yearOfStudyShowNew.toString());
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void onClickCancelButton() {
        final AlertDialog.Builder alertWindowBuilder = new AlertDialog.Builder(this);
        alertWindowBuilder.setTitle("Warning!");
        alertWindowBuilder.setMessage("Do you wish to discard the changes to your profile?");
        alertWindowBuilder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
