package com.csefyp2016.gib3.ustsocialapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class ProfileSetting extends Fragment {
    private Button viewMyFdList;
    private Button viewMyBlog;
    private FloatingActionButton viewAddHashTag;
    private Button editButton;
    private FloatingActionButton editProfilePicButton;

    private static final int EDIT_PROFILE_FINISH_CODE = 4414;
    private static final int IMAGE_REQUEST_CODE = 1087;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 7735;
    private static final int CROP_IMAGE = 2364;
    private static final int HASHTAG_ADD_REQUEST = 0;
    private Uri imageUri;
    private Bitmap profilePicture;


    private static final String updateProfilePicURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/profilePicUpdate.php";
    private static final String getFdIdListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendIdList.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private static final String getHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getHashtag.php";
    private static final String delHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/delHashtag.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private static final String profilePreference = "ProfilePreference";
    private static final String imagePreference = "ImagePreference";
    private static final String friendListPreference = "FriendList";
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
    private String fdIdList;
    private String fdDisplayNameList;

    private ListView listView;

    private final ArrayList<String> hashtag_id = new ArrayList<String>();
    private final ArrayList<String> hashtag_content = new ArrayList<String>();
    TableLayout hashtagTableLayout;
    TableRow tr;

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
        getFriendIdList();

        editProfilePicButton = (FloatingActionButton) view.findViewById(R.id.fab_profile_picture_edit);
        editProfilePicButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    openGallery();
                else {
                    String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissionRequest, GALLERY_PERMISSION_REQUEST_CODE);
                    openGallery();
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

        getHashtagHttpPost();


        viewAddHashTag = (FloatingActionButton) view.findViewById(R.id.fab_profile_add_hashtags);
        viewAddHashTag.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHashtag = new Intent(getActivity(), AddNewHashtag.class);

                Bundle b = new Bundle();
                b.putStringArrayList("current_id",hashtag_id);
                b.putStringArrayList("current_content",hashtag_content);
                intentHashtag.putExtras(b);
                startActivityForResult(intentHashtag, HASHTAG_ADD_REQUEST);

                //startActivity(intentHashtag);
                Log.d("fab clicked","");


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
        startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
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
                cropImage();
            }
            else if (requestCode == CROP_IMAGE) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                profilePicture = extras.getParcelable("data");
                profilePictureShow.setImageBitmap(profilePicture);
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                System.out.println("CROP IMAGE finished.");
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                updateProfilePicHttpPost();
            }
            else if (requestCode == EDIT_PROFILE_FINISH_CODE) {
                getProfileInfo();
                getProfileSwitch();
            }
            else if (requestCode == HASHTAG_ADD_REQUEST){
                //clear previous hashtags
                hashtagTableLayout = (TableLayout) getView().findViewById(R.id.table_hashtags);
                hashtagTableLayout.removeAllViews();

                getHashtagHttpPost();
            }
        }
    }

    private void updateProfilePicHttpPost() {
        request = new StringRequest(Request.Method.POST, updateProfilePicURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    sharedPreferences = getContext().getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorProfile = sharedPreferences.edit();
                    editorProfile.putString("PROFILEPIC", saveImageToInternalStorage(id, profilePicture));
                    editorProfile.commit();
                }
                else {
                    Toast toast = Toast.makeText(getContext(), response, Toast.LENGTH_SHORT);
                    toast.show();
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
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                System.out.println(image);
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                hashMap.put("profilePic", image);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void getFriendIdList() {
        request = new StringRequest(Request.Method.POST, getFdIdListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {

                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    String fdList = response.split(":")[1];
                    fdIdList = fdList;
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(fdList);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdIdListEditor = sharedPreferences.edit();
                    fdIdListEditor.putString("FDLIST_ID", fdList);
                    fdIdListEditor.commit();

                    getFriendDisplayNameList();
                }
                else {
                    sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("FDLIST_ID", null);
                    editor.putString("FDLIST_DISPLAYNAME", null);
                    editor.commit();
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
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }

    private void getFriendDisplayNameList() {
        request = new StringRequest(Request.Method.POST, getFdDisplayNameListURL, new Response.Listener<String>() {

            @Override
            // Response to request result
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(response);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    String fdList = response.split(":")[1];
                    fdDisplayNameList = fdList;
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                    System.out.println(fdList);
                    //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                    sharedPreferences = getContext().getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor fdNameListEditor = sharedPreferences.edit();
                    fdNameListEditor.putString("FDLIST_DISPLAYNAME", fdList);
                    fdNameListEditor.commit();
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
                hashMap.put("list", fdIdList);
                return hashMap;
            }
        };
        // Put the request to the queue
        requestQueue.add(request);
    }




    //////////////////////////////////////////////////////////////////////////////////



    private void getHashtagHttpPost() {
        // prepare the Request
        /*Log.d("getHashtagHttpPost","getHashtagHttpPost");
        Toast toast = Toast.makeText(getContext(), "getHashtagHttpPost", Toast.LENGTH_SHORT);
        toast.show();*/

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);

        JsonObjectRequest getRequest = new JsonObjectRequest( getHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response


                        Log.d("getHashtagHttpPost",response.toString());
                        Toast toast = Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();


                        try
                        {
                            if(response.getBoolean("success"))
                            {
                                Log.d("getHashtagHttpPost","have hashtag");
                                showHashtag(response.getJSONArray("readHashtags"));

                            }
                            else
                            {
                                Log.d("getHashtagHttpPost","no hashtag");
                                Toast t = Toast.makeText(getContext(), "You have no hashtags!", Toast.LENGTH_SHORT);
                                t.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

        requestQueue.add(getRequest);



    }

    private void delHashtagHttpPost(String remove_hashtag_id) {
        // prepare the Request

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("delete_hashtag_id", remove_hashtag_id);


        JsonObjectRequest delRequest = new JsonObjectRequest( delHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            if(response.getBoolean("success"))
                            {
                                Toast toast = Toast.makeText(getContext(), "Hashtag has been removed!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getContext(), "Hashtag doees not exist!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

        requestQueue.add(delRequest);



    }


    public void showHashtag(JSONArray readHashtags){
        Log.d("php id", id);

        hashtag_id.clear();
        hashtag_content.clear();

        if (readHashtags != null) {
            for (int i=0;i<readHashtags.length();i++){
                try {

                    hashtag_id.add(readHashtags.getJSONObject(i).getString("hashtag_id"));
                    hashtag_content.add(readHashtags.getJSONObject(i).getString("hashtag_content"));
                    Log.d("hashtag_id",readHashtags.getJSONObject(i).getString("hashtag_id"));
                    Log.d("hashtag_content",readHashtags.getJSONObject(i).getString("hashtag_content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        hashtagTableLayout = (TableLayout) getView().findViewById(R.id.table_hashtags);


        int i = 0;
        while (i < hashtag_content.size()) {
            if (i % 3 == 0) {
                tr = new TableRow(getActivity());
                hashtagTableLayout.addView(tr);

            }
            final Button btn = new Button(getActivity());
            btn.setText(hashtag_content.get(i));
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    // TODO Auto-generated method stub
                    System.out.println("v.getid is:- " + v.getId());
                    CharSequence options[] = new CharSequence[] {"Yes", "No"};

                    AlertDialog.Builder deleteTagOption = new AlertDialog.Builder(getActivity());
                    deleteTagOption.setTitle("Are you sure to delete this tag?");
                    deleteTagOption.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Log.d("delete tag","yes");

                                TableRow tr_clicked = (TableRow) (ViewGroup) v.getParent();


                                int id = v.getId();
                                String del_id = id+"";
                                Log.d("delete tag obj",del_id);

                                //delete in app view
                                tr_clicked.removeView(btn);


                           /*     for(int i=0; i < hashtag_content.size(); i++){
                                    String s = hashtag_content.get(i);
                                    Log.d("print hashtag_content 1",s);
                                }
                                for(int i=0; i < hashtag_id.size(); i++){
                                    String s = hashtag_id.get(i);
                                    Log.d("print hashtag_id 1",s);
                                }*/

                                //delete datalist
                                String data_clicked = btn.getText().toString();
                                String remove_hashtag_id = "";

                                if(hashtag_content.indexOf(data_clicked) >= 0){
                                    String s = hashtag_content.indexOf(data_clicked)+"";
                                    Log.d("index",s);
                                    remove_hashtag_id = hashtag_id.get(hashtag_content.indexOf(data_clicked));
                                    hashtag_id.remove(hashtag_content.indexOf(data_clicked));
                                    hashtag_content.remove(hashtag_content.indexOf(data_clicked));

                                }

/*
                                for(int i=0; i < hashtag_content.size(); i++){
                                    String s = hashtag_content.get(i);
                                    Log.d("print hashtag_content 2",s);
                                }
                                for(int i=0; i < hashtag_id.size(); i++){
                                    String s = hashtag_id.get(i);
                                    Log.d("print hashtag_id 2",s);
                                }
                                */
                                delHashtagHttpPost(remove_hashtag_id);

                            }
                            else {
                                Log.d("delete tag","no");

                            }
                        }
                    });
                    deleteTagOption.show();
                }
            });

            tr.addView(btn);
            i++;
        }
    }
}
