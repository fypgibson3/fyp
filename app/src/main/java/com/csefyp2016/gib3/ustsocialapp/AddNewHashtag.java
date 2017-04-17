package com.csefyp2016.gib3.ustsocialapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.layout.simple_list_item_1;
import static com.csefyp2016.gib3.ustsocialapp.R.id.container;
import static java.security.AccessController.getContext;

public class AddNewHashtag extends AppCompatActivity {

    View myFragmentView;
    SearchView search;
    ListView searchResults;
    Button save;


    private static final String getFullHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFullHashtag.php";
    private static final String addHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/addHashtag.php";
    private static final String getReturnHashtagURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getHashtag.php";
    private RequestQueue requestQueue;
    private static final String loginPreference = "LoginPreference";
    private static final String HashtagPreference = "HashtagPreference";
    private SharedPreferences sharedPreferences;
    private String id;

    private boolean taskfinish = false;

    //full hashtag id, content from database
    ArrayList<String> full_id = new ArrayList<String>();
    ArrayList<String> full_content = new ArrayList<String>();

    //existing hashtag id, content from full list
    ArrayList<String> current_id = new ArrayList<String>();
    ArrayList<String> current_content = new ArrayList<String>();

    //display hashtag id, full list exclude existing
    ArrayList<String> display_id = new ArrayList<String>();
    ArrayList<String> display_content = new ArrayList<String>();

    //Based on the search string, only filtered products will be moved here from display_content
    ArrayList<String> match_result = new ArrayList<String>();

    ArrayList<String> add_id = new ArrayList<String>();
    ArrayList<String> add_content = new ArrayList<String>();

    ArrayList<String> return_id = new ArrayList<String>();
    ArrayList<String> return_content = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_hashtag);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View view = inflater.inflate(R.layout.activity_add_new_hashtag, container, false);

        search=(SearchView) findViewById(R.id.hashtag_search_field);
        search.setQueryHint("Start typing to search...");

        searchResults = (ListView) findViewById(R.id.hashtag_search_list);

        save = (Button) findViewById(R.id.hashtag_search_save);


        requestQueue = Volley.newRequestQueue(search.getContext());

        sharedPreferences = search.getContext().getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", "");


        getFullHashtagHttpPost();

        // Create an ArrayAdapter from List
        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, simple_list_item_1, match_result);
        searchResults.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_checked,match_result);
        Log.d("context", this.toString());
        // DataBind ListView with items from ArrayAdapter
        searchResults.setAdapter(adapter);


        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                String s = String.valueOf(hasFocus);
                Log.d("onFocusChange",s);
            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                if(match_result.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Sorry! The hashtag does not exist!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                Log.d("onQueryTextSubmit",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {

                    Log.d("onQueryTextChange","len > 0 ");
                    searchResults.setVisibility(searchResults.VISIBLE);
                    filterProductArray(newText);
                    adapter.notifyDataSetChanged();
                    //myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(newText);
                }
                else {
                    Log.d("onQueryTextChange","len < 0 ");
                    for(int i = 0; i < searchResults.getCount();i++) {
                        searchResults.setItemChecked(i,false);
                    }
                    searchResults.setVisibility(searchResults.INVISIBLE);

                }
                return false;
            }


        });


        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) arg0;
                Context context = getApplicationContext();
                Toast.makeText(context,
                        "ID：" + arg3 +
                                "   selected hashtag："+ listView.getItemAtPosition(arg2).toString(),
                        Toast.LENGTH_LONG).show();

                for(int i = 0; i < searchResults.getCount();i++) {
                    if(searchResults.isItemChecked(i)) {
                        Log.d("check","");
                        int index = display_content.indexOf(listView.getItemAtPosition(arg2).toString());
                        if(index >= 0) {
                            if(add_content.indexOf(listView.getItemAtPosition(arg2).toString()) < 0) add_content.add(listView.getItemAtPosition(arg2).toString());
                            for(int j = 0 ; j < add_content.size(); j++) Log.d("(check+) add_content",add_content.get(j));

                            display_content.remove(index);
                            display_id.remove(full_id.get(full_content.indexOf(listView.getItemAtPosition(arg2).toString())));

                            for(int j = 0 ; j < display_content.size(); j++) Log.d("check-) display_content",display_content.get(j));
                            for(int j = 0; j < display_id.size(); j++) Log.d("(check-) display_id",display_id.get(j));

                        }
                    }
                    else {
                        //handle uncheck case
                        int index = display_content.indexOf(listView.getItemAtPosition(i).toString());

                        Log.d("(uncheck) i",Integer.toString(i));
                        Log.d("(uncheck) index",Integer.toString(index));

                        //checked hashtag not in display list
                        if(index < 0) {
                            if(add_content.indexOf(listView.getItemAtPosition(i).toString()) >= 0) add_content.remove(listView.getItemAtPosition(i).toString());
                            for(int j = 0 ; j < add_content.size(); j++) Log.d("(uncheck-)  add_content",add_content.get(j));

                            if(display_content.indexOf(listView.getItemAtPosition(i).toString()) < 0)
                                display_content.add(listView.getItemAtPosition(i).toString());
                            if(display_id.indexOf(full_id.get(full_content.indexOf(listView.getItemAtPosition(i).toString()))) < 0)
                                display_id.add(full_id.get(full_content.indexOf(listView.getItemAtPosition(i).toString())));

                            for(int j = 0 ; j < display_content.size(); j++) Log.d("uncheck display_content",display_content.get(j));
                            for(int j = 0; j < display_id.size(); j++) Log.d("(uncheck)   display_id",display_id.get(j));

                        }

                    }

                }

            }
        });

        save.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StringBuilder sb = new StringBuilder();
                for (String s:add_content) {

                    sb.append(s);
                    sb.append("\n");
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if(add_content.size() > 0) {
                                    for (int i = 0; i < add_content.size(); i++) {
                                        add_id.add(full_id.get(full_content.indexOf(add_content.get(i))));
                                        Log.d("add_id",add_id.get(i));
                                    }
                                    addHashtagHttpPost(add_id);
                                }

                                getReturnHashtagHttpPost();


/*                                sharedPreferences = getSharedPreferences(hashtagPreference, Context.MODE_PRIVATE);
                                SharedPreferences.Editor returnEditor = sharedPreferences.edit();
                                //Set the values
                                Set<String> set = new HashSet<String>();
                                set.addAll(return_id);
                                set.addAll(return_content);
                                returnEditor.putStringSet("key", set);
                                returnEditor.commit();*/

                                //onBackPressed();


                                setResult(RESULT_OK);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Do you want to save these hashtags?").setMessage(sb.toString()).setNegativeButton("No", dialogClickListener).setPositiveButton("Yes", dialogClickListener).show();
            }
        });
    }


    //this filters products from productResults and copies to match_result based on search text
    public void filterProductArray(String newText) {

        String pName;

        Log.d("filterProductArray", newText);
        Log.d("filter current", Integer.toString(current_content.size()));
        Log.d("filter display", Integer.toString(display_content.size()));
        Log.d("filter match_resultsize", Integer.toString(match_result.size()));
        match_result.clear();
        for (int i = 0; i < display_content.size(); i++) {
            Log.d("filter newText", newText);
            pName = display_content.get(i).toLowerCase().replaceAll("\\s+","");
            if ( pName.contains(newText.toLowerCase().replaceAll("\\s+",""))) {
                Log.d("filter match_result", pName);
                match_result.add(display_content.get(i));
            }
        }
    }

    private void getFullHashtagHttpPost() {
        // prepare the Request

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);

        JsonObjectRequest getRequest = new JsonObjectRequest( getFullHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("getFullHashtagHttpPost",response.toString());
                        Toast toast = Toast.makeText(search.getContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        try {
                            if(response.getBoolean("success")) {
                                Log.d("getFullHashtagHttpPost","success");

                                JSONArray readHashtags = response.getJSONArray("readHashtags");
                                if (readHashtags != null) {
                                    for (int i=0;i<readHashtags.length();i++){
                                        try {

                                            full_id.add(readHashtags.getJSONObject(i).getString("hashtag_id"));
                                            full_content.add(readHashtags.getJSONObject(i).getString("hashtag_content"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            else {
                                Log.d("getFullHashtagHttpPost","fail");

                            }

                            taskfinish = true;
                            createDisplay();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

        requestQueue.add(getRequest);
    }

    private void createDisplay() {


        /*Toast toast = Toast.makeText(search.getContext(), "createDisplay", Toast.LENGTH_SHORT);
        toast.show();*/
        if(taskfinish){

            /*Toast t = Toast.makeText(search.getContext(), "taskfinish true", Toast.LENGTH_SHORT);
            t.show();*/

            // Get the intent, verify the action and get the query
            Intent intent = getIntent();
        /* Obtain String from Intent  */
            if(intent != null) {

                Log.d("intent !=null","");
                //get existing hashtag ids from last page
                current_id = intent.getExtras().getStringArrayList("current_id");
                current_content = intent.getExtras().getStringArrayList("current_content");



                //if user had hashtag, search  full list with id for content, save content
                if(!current_content.isEmpty()) {

                    for(int i = 0; i < full_id.size(); i++) {
                        //add non-exist hashtag to display list
                        String matchFound = "N";

                        for (int j=0; j < current_id.size();j++) {

                            if (current_id.get(j).equals(full_id.get(i))) {
                                matchFound = "Y";
                            }
                        }

                        if (matchFound == "N") {
                            display_id.add(full_id.get(i));
                        }
                        Log.d("matchFound",matchFound);
                    }

                    //construct display hashtag by id
                    for(int i = 0; i < display_id.size(); i++) {
                        Log.d("display_id",display_id.get(i));
                        display_content.add(full_content.get(full_id.indexOf(display_id.get(i))));
                        Log.d("display_content",display_content.get(i));
                    }

                }
                else {
                    //no hashtag, display = full
                    for(int i = 0; i < full_id.size(); i++) {
                        display_id.add(full_id.get(i));
                        Log.d("(notag) display_id", display_id.get(i));
                    }
                    for(int i = 0; i < full_content.size(); i++) {
                        display_content.add(full_content.get(i));
                        Log.d("(notag) display_content", display_content.get(i));
                    }
                }
            }
            else {
                Log.d("intent.getExtras() fail", "");
            }
        }
    }

    private void addHashtagHttpPost(ArrayList<String> add_id) {
        // prepare the Request

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        String put_id = "";
        for(int i = 0; i < add_id.size(); i++) {
            put_id += add_id.get(i)+":";
        }
        params.put("update_hashtag_id", put_id);

        JsonObjectRequest getRequest = new JsonObjectRequest( addHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("addHashtagHttpPost",response.toString());
                        Toast toast = Toast.makeText(search.getContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                        try {
                            if(response.getString("success").contains("false")) {
                                Toast fail_toast = Toast.makeText(search.getContext(), "Sorry, you cannot add duplicate hashtags!", Toast.LENGTH_SHORT);
                                fail_toast.show();

                            }
                            else {
                                Toast success_toast = Toast.makeText(search.getContext(), "Hashtag added successfully!", Toast.LENGTH_SHORT);
                                success_toast.show();
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

    private void getReturnHashtagHttpPost() {
        // prepare the Request

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);

        JsonObjectRequest getRequest = new JsonObjectRequest( getReturnHashtagURL, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response


                        /*Log.d("getHashtagHttpPost",response.toString());
                        Toast toast = Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();
*/

                        try {
                            if(response.getBoolean("success")) {
                                Log.d("getHashtagHttpPost","have hashtag");

                                return_id.clear();
                                return_content.clear();

                                JSONArray readHashtags = response.getJSONArray("readHashtags");
                                if (readHashtags != null) {
                                    for (int i=0;i<readHashtags.length();i++){
                                        try {
                                            return_id.add(readHashtags.getJSONObject(i).getString("hashtag_id"));
                                            return_content.add(readHashtags.getJSONObject(i).getString("hashtag_content"));
                                            Log.d("return_id",readHashtags.getJSONObject(i).getString("hashtag_id"));
                                            Log.d("return_content",readHashtags.getJSONObject(i).getString("hashtag_content"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }
                            else {
                                Log.d("getHashtagHttpPost","no hashtag");
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

}
