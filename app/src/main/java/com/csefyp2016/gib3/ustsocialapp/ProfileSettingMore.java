package com.csefyp2016.gib3.ustsocialapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileSettingMore extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 5462;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1205;
    private static final int CROP_IMAGE = 6433;
    private Uri imageUri;
    private ImageView profilePictureShow;
    private Bitmap profilePicture;
    private String personalDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_more);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                EditText personalDescriptionInput = (EditText) findViewById(R.id.i_profileSettingMore_personal_des);

                if (profilePicture == null) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
                    warning.setText("Please upload an image as your Profile Picture.");
                    warning.setVisibility(View.VISIBLE);
                }
                else if (checkFilledEditText(personalDescriptionInput.getText().toString()) == false) {
                    TextView warning = (TextView) findViewById(R.id.view_profileSettingMore_warning);
                    warning.setText("Please say something about yourself in Personal Description.");
                    warning.setVisibility(View.VISIBLE);
                }
                else {
                    personalDes = personalDescriptionInput.getText().toString();

                    startActivity(new Intent(ProfileSettingMore.this, USTSocialAppMain.class));
                }

                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                TextView debug = (TextView) findViewById(R.id.view_profileSettingMore_debug);
                debug.setText(profilePicture + "\n" + personalDes);
                debug.setVisibility(View.VISIBLE);
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
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

}
