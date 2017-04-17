package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewVote extends AppCompatActivity {

    private String voteTitle;
    private String voteHashtags;
    private String voteQuestion;
    private String[] voteOptions;
    private Integer voteNumOfOptions;
    private String voteWarning;
    private int[] moreOptionId;

    private static final String uploadStoryVoteURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/uploadStoryVote.php";

    private RequestQueue requestQueue;
    private StringRequest request;

    private ScrollView scrollView;
    private EditText title;
    private EditText hashtags;
    private EditText question;
    private TextView numOfOptions;
    private EditText[] options;
    private TextView warning;
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        moreOptionId = new int[]{R.id.option3, R.id.option4, R.id.option5, R.id.option6, R.id.option7, R.id.option8, R.id.option9, R.id.option10};

        scrollView = (ScrollView) findViewById(R.id.scroll_addNewVote);
        title = (EditText) findViewById(R.id.i_addNewVote_title);
        hashtags = (EditText) findViewById(R.id.i_addNewVote_hashtags);
        question = (EditText) findViewById(R.id.i_addNewVote_question);
        numOfOptions = (TextView) findViewById(R.id.view_addNewVote_numOfVote);
        warning = (TextView) findViewById(R.id.view_addNewVote_warning);
        options = new EditText[2];
        options[0] = (EditText) findViewById(R.id.i_addNewVote_option1);
        options[1] = (EditText) findViewById(R.id.i_addNewVote_option2);
        table = (TableLayout) findViewById(R.id.table_addNewVote);

        Button cancelButton = (Button) findViewById(R.id.b_addNewVote_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonAction();
            }
        });

        Button submitButton = (Button) findViewById(R.id.b_addNewVote_submit);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonAction();
            }
        });

        FloatingActionButton addOptionButton = (FloatingActionButton) findViewById(R.id.fab_addNewVote_more_options);
        addOptionButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOptionButtonAction();
            }
        });
    }

    private void cancelButtonAction(){
        this.finish();
    }

    private void submitButtonAction(){
        if(checkFillIn()) {
            voteTitle = title.getText().toString();
            voteHashtags = hashtags.getText().toString();
            voteQuestion = question.getText().toString();
            voteOptions = new String[table.getChildCount()];
            for(int i = 0; i < table.getChildCount(); i++){
                View view = table.getChildAt(i);
                if(view instanceof TableRow){
                    TableRow row = (TableRow) view;
                    view = row.getChildAt(1);
                    if(view instanceof TextInputLayout) {
                        TextInputLayout layout = (TextInputLayout) view;
                        voteOptions[i] = layout.getEditText().getText().toString();
                    }
                }
            }
            uploadVote();
            this.finish();
        }
        else{
            warning.setText(voteWarning);
            warning.setVisibility(View.VISIBLE);
            scrollView.pageScroll(View.FOCUS_UP);
        }
    }

    private void addOptionButtonAction(){
        voteNumOfOptions = table.getChildCount();
        voteNumOfOptions += 1;
        numOfOptions.setText(String.valueOf(voteNumOfOptions));
        TableRow newRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.add_vote_new_option_row, null);
        ((TextView) newRow.findViewById(R.id.view_addNewVote_newOption)).setText("Option " + voteNumOfOptions);
        table.addView(newRow);
        table.requestLayout();
    }

    private boolean checkFillIn(){
        boolean moreThanOne = false;
        voteWarning = "Please fill in";
        if(title.getText().toString().isEmpty()){
            moreThanOne = true;
            voteWarning = voteWarning + " title";
        }
        if(question.getText().toString().isEmpty()){
            if(moreThanOne == true){
                voteWarning = voteWarning + ", question";
            }
            else{
                moreThanOne = true;
                voteWarning = voteWarning + " question";
            }
        }
        if(options[0].getText().toString().isEmpty() || options[1].getText().toString().isEmpty()){
            if(moreThanOne == true){
                voteWarning = voteWarning + ", options";
            }
            else{
                moreThanOne = true;
                voteWarning = voteWarning + " options";
            }
        }
        if(moreThanOne == true){
            voteWarning = voteWarning + " field.";
            return false;
        }
        else{
            return true;
        }
    }

    private void uploadVote() {
        request = new StringRequest(Request.Method.POST, uploadStoryVoteURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.contains("Success")){
                    setToast("success");
                }
                else if(response.contains("Fail")){
                    setToast("fail");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                hashMap.put("date", dateFormat.format(date));
                hashMap.put("title", voteTitle);
                hashMap.put("hashtag", voteHashtags);
                hashMap.put("question", voteQuestion);
                String option = "";
                for(int i = 0; i < table.getChildCount(); i++){
                    if(!voteOptions[i].isEmpty()) {
                        option = option + voteOptions[i] + "{[option]}";
                    }
                }
                hashMap.put("option", option);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void setToast(String response){
        if(response == "success"){
            Toast.makeText(this, "Create story vote successful.", Toast.LENGTH_LONG).show();
        }
        else if(response == "fail"){
            Toast.makeText(this, "Create story vote fail. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
