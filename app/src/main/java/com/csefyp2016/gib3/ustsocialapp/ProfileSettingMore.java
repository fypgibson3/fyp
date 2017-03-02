package com.csefyp2016.gib3.ustsocialapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingMore extends AppCompatActivity {
    private String id;

    private static final int IMAGE_REQUEST_CODE = 5462;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1205;
    private static final int CROP_IMAGE = 6433;
    private Uri imageUri;
    private ImageView profilePictureShow;

    private Bitmap profilePicture;
    private String personalDes;

    private String displayName;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String studentCategory;
    private String faculty;
    private String major;
    private String yearOfStudy;

    private Boolean genderShow;
    private Boolean dateOfBirthShow;
    private Boolean countryShow;
    private Boolean studentCategoryShow;
    private Boolean facultyShow;
    private Boolean majorShow;
    private Boolean yearOfStudyShow;

    private static final String URL_profileSetup = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/profileInfoSetup.php";
    private static final String URL_profileSwitch = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/profileSwitchSetup.php";
    private RequestQueue requestQueue;
    private StringRequest request_profileInfo;
    private StringRequest request_profileSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_more);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent previousIntent = getIntent();
        Bundle previousContent = previousIntent.getExtras();
        if (previousContent != null) {
            id = previousContent.getString("id");
            displayName = previousContent.getString("displayName");
            gender = previousContent.getString("gender");
            dateOfBirth = previousContent.getString("dateOfBirth");
            country = previousContent.getString("country");
            studentCategory = previousContent.getString("studentCategory");
            faculty = previousContent.getString("faculty");
            major = previousContent.getString("major");
            yearOfStudy = previousContent.getString("yearOfStudy");

            genderShow = previousContent.getBoolean("genderShow");
            dateOfBirthShow = previousContent.getBoolean("dateOfBirthShow");
            countryShow = previousContent.getBoolean("countryShow");
            studentCategoryShow = previousContent.getBoolean("studentCategoryShow");
            facultyShow = previousContent.getBoolean("facultyShow");
            majorShow = previousContent.getBoolean("majorShow");
            yearOfStudyShow = previousContent.getBoolean("yearOfStudyShow");
        }

        requestQueue = Volley.newRequestQueue(this);

        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
        TextView debug = (TextView) findViewById(R.id.view_profileSettingMore_debug);
        debug.setText(id + "\n" + displayName + "/n" + gender + "/n" + dateOfBirth + "/n" + country + "/n" + studentCategory + "/n" + faculty + "/n" + major + "/n" + yearOfStudy + "\n" +
                genderShow + "/" + dateOfBirthShow + "/" + countryShow + "/" + studentCategoryShow + "/" + facultyShow + "/" + majorShow + "/" + yearOfStudyShow);
        debug.setVisibility(View.VISIBLE);
        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

        profilePictureShow = (ImageView) findViewById(R.id.image_profileSettingMore_profile_picture);

        FloatingActionButton addPicture = (FloatingActionButton) findViewById(R.id.fb_profileSettingMore_add_picture);
        addPicture.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    openGallery();
                else {
                    String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissionRequest, GALLERY_PERMISSION_REQUEST_CODE);
                }
            }
        });

        Button doneButton = (Button) findViewById(R.id.b_profileSettingMore_done);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (checkInput()) {
                profileInfoSetup();
                //startActivity(new Intent(ProfileSettingMore.this, USTSocialAppMain.class));
            }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.b_profileSettingMore_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancelButton();
            }
        });
    }



    // **************************************************************
    // Function: onRequestPermissionResult
    // Description: To grant permission from users  for another activity access (Read External Storage)
    // Parameter: int, String[], String[]
    // Return: /
    // **************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
            else {
                Toast.makeText(this, "Cannot open gallery to choose photo.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // **************************************************************
    // Function: openGallery
    // Description: To open the gallery which can select images
    // Parameter: /
    // Return: /
    // **************************************************************
    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
    }

    // **************************************************************
    // Function: onActivityResult
    // Description: To get result form the previously requested activity
    // Parameter: int, int, Intent
    // Return: /
    // **************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                imageUri = data.getData();
                InputStream inputStream;

                cropImage();

                /************************************************* Code for Picking Image Only *************************************************/
                /*
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    profilePicture = BitmapFactory.decodeStream(inputStream);
                    profilePictureShow.setImageBitmap(profilePicture);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open profilePicture", Toast.LENGTH_LONG).show();
                }
                */
                /************************************************* Code for Picking Image Only *************************************************/
            }
            else if (requestCode == CROP_IMAGE) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                profilePicture = extras.getParcelable("data");
                profilePictureShow.setImageBitmap(profilePicture);
            }
        }
    }

    // **************************************************************
    // Function: cropImage
    // Description: To crop image to 200x200 px
    // Parameter: /
    // Return: /
    // **************************************************************
    private void cropImage() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(imageUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 220);
            cropIntent.putExtra("outputY", 220);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // **************************************************************
    // Function: getStringImage
    // Description: To prepare the profile picture for server upload
    // Parameter: Bitmap
    // Return Type: String
    //***************************************************************
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageByte = outputStream.toByteArray();
        String stringImage = Base64.encodeToString(imageByte, Base64.DEFAULT);

        return stringImage;
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

    private boolean checkInput() {
        EditText personalDescriptionInput = (EditText) findViewById(R.id.i_profileSettingMore_personal_des);

        if (profilePicture == null) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
            warning.setText("Please upload an image as your Profile Picture.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else if (checkFilledEditText(personalDescriptionInput.getText().toString()) == false) {
            TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
            warning.setText("Please say something about yourself in Personal Description.");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            personalDes = personalDescriptionInput.getText().toString();

            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
            TextView debug = (TextView) findViewById(R.id.view_profileSettingMore_debug);
            debug.setText(profilePicture + "\n" + personalDes);
            debug.setVisibility(View.VISIBLE);
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

            return true;
        }
    }

    private void profileInfoSetup() {
        request_profileInfo = new StringRequest(Request.Method.POST, URL_profileSetup, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    profileSwitchSetup();
                    System.out.println("Setup profile finished.");
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
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
                String image = getStringImage(profilePicture);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                hashMap.put("displayName", displayName);
                hashMap.put("gender", gender);
                hashMap.put("dateOfBirth", dateOfBirth);
                hashMap.put("country", country);
                hashMap.put("studentCategory", studentCategory);
                hashMap.put("faculty", faculty);
                hashMap.put("major", major);
                hashMap.put("yearOfStudy", yearOfStudy);
                hashMap.put("profilePic", image);
                hashMap.put("personalDes", personalDes);

                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request_profileInfo);
    }

    private void profileSwitchSetup() {
        request_profileSwitch = new StringRequest(Request.Method.POST, URL_profileSwitch, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    Intent intent = new Intent(getApplicationContext(), USTSocialAppMain.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                }
                else {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
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
                hashMap.put("id", id);
                hashMap.put("genderShow", genderShow.toString());
                hashMap.put("dateOfBirthShow", dateOfBirthShow.toString());
                hashMap.put("countryShow", countryShow.toString());
                hashMap.put("studentCategoryShow", studentCategoryShow.toString());
                hashMap.put("facultyShow", facultyShow.toString());
                hashMap.put("majorShow", majorShow.toString());
                hashMap.put("yearOfStudyShow", yearOfStudyShow.toString());
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request_profileSwitch);
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
