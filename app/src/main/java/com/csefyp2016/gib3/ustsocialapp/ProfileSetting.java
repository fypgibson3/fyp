package com.csefyp2016.gib3.ustsocialapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class ProfileSetting extends Fragment {
    private Button viewMyFdList;
    private Button viewMyBlog;
    private Button editButton;
    private FloatingActionButton editProfilePicButton;

    private static final int EDIT_PROFILE_FINISH_CODE = 4414;
    private static final int IMAGE_REQUEST_CODE = 1087;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 7735;
    private static final int CROP_IMAGE = 2364;
    private Uri imageUri;
    private Bitmap profilePicture;


    private static final String URL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private static final String profilePreference = "ProfilePreference";
    private static final String imagePreference = "ImagePreference";
    private SharedPreferences sharedPreferences;

    private TextView displayName;
    private TextView gender;
    private TextView dateOfBirth;
    private TextView country;
    private TextView studentCategory;
    private TextView faculty;
    private TextView major;
    private TextView yearOfStudy;
    private TextView personalDes;
    private ImageView profilePictureShow;

    private Boolean genderShow;
    private Boolean dateOfBirthShow;
    private Boolean countryShow;
    private Boolean studentCategoryShow;
    private Boolean facultyShow;
    private Boolean majorShow;
    private Boolean yearOfStudyShow;

    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        displayName = (TextView) view.findViewById(R.id.view_profile_display_name);
        gender = (TextView) view.findViewById(R.id.view_profile_gender);
        dateOfBirth = (TextView) view.findViewById(R.id.view_profile_date_of_birth);
        country = (TextView) view.findViewById(R.id.view_profile_country);
        studentCategory = (TextView) view.findViewById(R.id.view_profile_student_category);
        faculty = (TextView) view.findViewById(R.id.view_profile_faculty);
        major = (TextView) view.findViewById(R.id.view_profile_major);
        yearOfStudy = (TextView) view.findViewById(R.id.view_profile_year_of_study);
        personalDes = (TextView) view.findViewById(R.id.view_profile_personal_des);
        profilePictureShow = (ImageView) view.findViewById(R.id.image_profile_picture_edit);

        requestQueue = Volley.newRequestQueue(view.getContext());

        sharedPreferences = getContext().getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", "");

        getProfileInfo();
        getProfileSwitch();

        editProfilePicButton = (FloatingActionButton) view.findViewById(R.id.fab_profile_picture_edit);
        editProfilePicButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    openGallery();
                else {
                    String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissionRequest, GALLERY_PERMISSION_REQUEST_CODE);
                }
            }
        });

        editButton = (Button) view.findViewById(R.id.b_profile_edit_info);
        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit = new Intent(getActivity(), EditProfile.class);
                startActivityForResult(intentEdit, EDIT_PROFILE_FINISH_CODE);
            }
        });

        viewMyFdList = (Button) view.findViewById(R.id.b_profile_friend_list);
        viewMyFdList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFdList  = new Intent(getActivity(), MyFriendList.class);
                startActivity(intentFdList);
            }
        });

        viewMyBlog = (Button) view.findViewById(R.id.b_profile_blog);
        viewMyBlog.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBlog = new Intent(getActivity(), MyBlog.class);
                startActivity(intentBlog);
            }
        });

        return view;
    }

    private Bitmap loadProfilePicture(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            Bitmap image = BitmapFactory.decodeStream(new FileInputStream(imageFile));
            return image;
        } catch (FileNotFoundException ex) {
            System.out.println("Image not found.");
            return null;
        }
    }

    private String saveImageToInternalStorage(String id, Bitmap bitmap) {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File imageDirectory = contextWrapper.getDir(imagePreference, Context.MODE_PRIVATE);
        File imageFile = new File(imageDirectory, id+".jpg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageFile.getAbsolutePath();
    }

    private void getProfileInfo() {
        String imagePath = "";

        sharedPreferences = getContext().getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
        if (sharedPreferences.getString("DISPLAYNAME", null) != null) {
            displayName.setText(sharedPreferences.getString("DISPLAYNAME", ""));

            if (sharedPreferences.getString("GENDER", null).equals("M"))
                gender.setText("Male");
            else if (sharedPreferences.getString("GENDER", null).equals("F"))
                gender.setText("Female");

            dateOfBirth.setText(sharedPreferences.getString("BIRTHDATE", ""));
            country.setText(sharedPreferences.getString("COUNTRY", ""));

            if (sharedPreferences.getString("STUDENTCATEGORY", null).equals("local"))
                studentCategory.setText("Local Student");
            else if (sharedPreferences.getString("STUDENTCATEGORY", null).equals("mainland"))
                studentCategory.setText("Mainland Student");
            else if (sharedPreferences.getString("STUDENTCATEGORY", null).equals("international"))
                studentCategory.setText("International Student");

            if (sharedPreferences.getString("FACULTY", null).equals("SBM"))
                faculty.setText("School of Business and Management");
            else if (sharedPreferences.getString("FACULTY", null).equals("SSCI"))
                faculty.setText("School of Science");
            else if (sharedPreferences.getString("FACULTY", null).equals("SENG"))
                faculty.setText("School of Engineering");
            else if (sharedPreferences.getString("FACULTY", null).equals("SHSS"))
                faculty.setText("School of Humanities and Social Science");
            else if (sharedPreferences.getString("FACULTY", null).equals("IPO"))
                faculty.setText("Interdisciplinary Programs Office");

            major.setText(sharedPreferences.getString("MAJOR", ""));

            if (sharedPreferences.getString("YEAROFSTUDY", null).equals("6"))
                yearOfStudy.setText("Year 6 or more");
            else
                yearOfStudy.setText("Year " + sharedPreferences.getString("YEAROFSTUDY", ""));

            personalDes.setText(sharedPreferences.getString("PERSONALDES", ""));
            imagePath = sharedPreferences.getString("PROFILEPIC", null);

            if (imagePath != null) {
                profilePictureShow.setImageBitmap(loadProfilePicture(imagePath));
            }
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
            else {
                System.out.println("Fail to load profile picture from internal storage");
            }
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
        }
    }

    private void getProfileSwitch() {
        sharedPreferences = getContext().getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("GENDER_SHOW") && sharedPreferences.contains("BIRTHDATE_SHOW") && sharedPreferences.contains("COUNTRY_SHOW")
                && sharedPreferences.contains("STUDENTCATEGORY_SHOW") && sharedPreferences.contains("FACULTY_SHOW") && sharedPreferences.contains("MAJOR_SHOW")
                && sharedPreferences.contains("YEAROFSTUDY_SHOW")) {
            genderShow = sharedPreferences.getBoolean("GENDER_SHOW", true);
            dateOfBirthShow = sharedPreferences.getBoolean("BIRTHDATE_SHOW", true);
            countryShow = sharedPreferences.getBoolean("COUNTRY_SHOW", true);
            studentCategoryShow = sharedPreferences.getBoolean("STUDENTCATEGORY_SHOW", true);
            facultyShow = sharedPreferences.getBoolean("FACULTY_SHOW", true);
            majorShow = sharedPreferences.getBoolean("MAJOR_SHOW", true);
            yearOfStudyShow = sharedPreferences.getBoolean("YEAROFSTUDY_SHOW", true);
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
        getActivity().startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
    }

    // **************************************************************
    // Function: onActivityResult
    // Description: To get result form the previously requested activity
    // Parameter: int, int, Intent
    // Return: /
    // **************************************************************
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                imageUri = data.getData();
                InputStream inputStream;
                cropImage();
            }
            else if (requestCode == CROP_IMAGE) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                profilePicture = extras.getParcelable("data");
                profilePictureShow.setImageBitmap(profilePicture);

                sharedPreferences = getContext().getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PROFILEPIC", saveImageToInternalStorage(id, profilePicture));
                editor.commit();
            }
            else if (requestCode == EDIT_PROFILE_FINISH_CODE) {
                System.out.println("Return from EditProfile.");
                getProfileInfo();
                getProfileSwitch();
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
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
