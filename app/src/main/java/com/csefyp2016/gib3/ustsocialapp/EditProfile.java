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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

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

    private String displayName;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;
    private String personalDes;

    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/checkDisplayNameUnique.php";
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

        Button doneButton = (Button) findViewById(R.id.b_editProfile_done);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        localStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_local);
        mainlandStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_mainland);
        internationalStudentCategoryInput = (RadioButton) findViewById(R.id.rb_editProfile_student_category_international);
        facultyInput = (Spinner) findViewById(R.id.op_editProfile_faculty);
        majorInput = (Spinner) findViewById(R.id.op_editProfile_major);
        yearOfStudyInput = (Spinner) findViewById(R.id.op_editProfile_year_of_study);

        displayNameInput.setText(displayName);
        personalDesInput.setText(personalDes);

        if (gender == "M")
            genderGroup.check(R.id.rb_editProfile_gender_male);
        else if (gender == "F")
            genderGroup.check(R.id.rb_editProfile_gender_female);
        else
            genderGroup.check(R.id.rb_editProfile_gender_default);

        String[] yearMonthDate = dateOfBirth.split("-");
        dateOfBirthInput.updateDate(Integer.parseInt(yearMonthDate[0]), Integer.parseInt(yearMonthDate[1]), Integer.parseInt(yearMonthDate[2]));

        countryInput.setSelection(((ArrayAdapter)countryInput.getAdapter()).getPosition(country));

        if (studentCategory == "local")
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_local);
        else if (studentCategory == "mainland")
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_mainland);
        else if (studentCategory == "international")
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_international);
        else
            studentCategoryGroup.check(R.id.rb_editProfile_student_category_default);

        facultyInput.setSelection(((ArrayAdapter)facultyInput.getAdapter()).getPosition(faculty));
        majorInput.setSelection(((ArrayAdapter)majorInput.getAdapter()).getPosition(major));

        if (yearOfStudy == "6")
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
