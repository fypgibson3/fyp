package com.csefyp2016.gib3.ustsocialapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.Date;

public class ProfileSettingBasic extends AppCompatActivity {
    private Boolean genderShow;
    private Boolean dateOfBirthShow;
    private Boolean countryShow;
    private Boolean studentCategoryShow;
    private Boolean facultyShow;
    private Boolean majorShow;
    private Boolean yearOfStudyShow;

    private String displayName;
    private String gender;
    private Date dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;

    String selectedFaculty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_basic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                if (checkFilledEditText(displayNameInput.getText().toString()) == false) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Your Display Name must not be empty. Please fill in your Display Name.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (checkDisplayNameUsed(displayNameInput.getText().toString()) == false) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Your Display Name has already been used. Please choose another one.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (genderGroup.getCheckedRadioButtonId() == defaultGenderInput.getId()) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Gender.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (countryInput.getSelectedItemPosition() == 0) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Country.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (studentCategoryGroup.getCheckedRadioButtonId() == defaultStudentCategoryInput.getId()) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Student Category.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (facultyInput.getSelectedItemPosition() == 0) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Faculty.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (majorInput.getSelectedItemPosition() == 0) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Major.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (yearOfStudyInput.getSelectedItemPosition() == 0) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingBasic_warning);
                    warning.setText("Please select your Year Of Study.");
                    warning.setVisibility(View.VISIBLE);
                }
                else {
                    displayName = displayNameInput.getText().toString();

                    if (genderGroup.getCheckedRadioButtonId() == maleGenderInput.getId())
                        gender = "M:";
                    else
                        gender = "F";

                    dateOfBirth = new Date(dateOfBirthInput.getYear()-1900, dateOfBirthInput.getMonth(), dateOfBirthInput.getDayOfMonth());

                    country = countryInput.getSelectedItem().toString();

                    if (studentCategoryGroup.getCheckedRadioButtonId() == localStudentCategoryInput.getId())
                        studentCategory = "Local";
                    else if (studentCategoryGroup.getCheckedRadioButtonId() == mainlandStudentCategoryInput.getId())
                        studentCategory = "Mainland";
                    else
                        studentCategory = "International";

                    faculty = facultyInput.getSelectedItem().toString();

                    major = majorInput.getSelectedItem().toString();

                    yearOfStudy = yearOfStudyInput.getSelectedItem().toString();

                    startActivity(new Intent(ProfileSettingBasic.this, ProfileSettingMore.class));
                }

                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                TextView debug = (TextView) findViewById(R.id.debug_profileSettingBasic);
                debug.setText(displayName + "/n" + gender + "/n" + dateOfBirth + "/n" + country + "/n" + studentCategory + "/n" + faculty + "/n" + major + "/n" + yearOfStudy + "\n" +
                        genderShow + "/" + dateOfBirthShow + "/" + countryShow + "/" + studentCategoryShow + "/" + facultyShow + "/" + majorShow + "/" + yearOfStudyShow);
                debug.setVisibility(View.VISIBLE);
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

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
    // Parameter: String
    // Return Type: Boolean
    // **************************************************************
    private boolean checkDisplayNameUsed(String input) {
        return true;
    }

}
