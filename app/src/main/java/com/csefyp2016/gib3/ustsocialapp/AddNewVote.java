package com.csefyp2016.gib3.ustsocialapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

public class AddNewVote extends AppCompatActivity {

    private String voteTitle;
    private String voteHashtags;
    private String voteQuestion;
    private String[] voteOptions;
    private int voteNumOfOptions;
    private String voteWarning;

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

            this.finish();
        }
        else{
            warning.setText(voteWarning);
            warning.setVisibility(View.VISIBLE);
            scrollView.pageScroll(View.FOCUS_UP);
        }
    }

    private void addOptionButtonAction() {
        getOptionNum();
        voteNumOfOptions += 1;
        numOfOptions.setText(String.valueOf(voteNumOfOptions));

        TableLayout table = (TableLayout) findViewById(R.id.table_addNewVote);

        TableRow newRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.add_vote_new_option_row, null);
        ((TextView) newRow.findViewById(R.id.view_addNewVote_newOption)).setText("Option " + voteNumOfOptions);
        table.addView(newRow);

        table.requestLayout();
    }

    private boolean checkFillIn(){
        boolean moreThanOne = false;
        voteWarning = "Please fill in";
        if(title.getText().toString() == ""){
            moreThanOne = true;
            voteWarning = voteWarning + " title";
        }
        if(question.getText().toString() == ""){
            if(moreThanOne == true){
                voteWarning = voteWarning + ", question";
            }
            else{
                moreThanOne = true;
                voteWarning = voteWarning + " question";
            }
        }
        if(options[0].getText().toString() == "" || options[1].getText().toString() == ""){
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

    private void getOptionNum(){
        voteNumOfOptions = Integer.parseInt(numOfOptions.getText().toString());
    }

}
