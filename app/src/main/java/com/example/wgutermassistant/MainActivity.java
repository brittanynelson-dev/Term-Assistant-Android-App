package com.example.wgutermassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

        //Called when user taps the CURRENT TERM button
    public void currentTermBtnAction (View view) {
        long termId = DBDataManager.getActiveTermID(this);
        //If there is an active term, load the TermDetailActivity, otherwise load NoTermsActivity
        if (termId==-1){
            Intent intent = new Intent(this, NoTermsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TermDetailActivity.class);
            intent.putExtra("termId",termId);
            startActivity(intent);
        }
    }

    //Called when user taps the VIEW ALL TERMS button
    public void viewTermsBtnAction (View view) {
        long termID = DBDataManager.getActiveTermID(this);
        //If there is an active term, load the TermListActivity, otherwise load NoTermsActivity
        if (termID==-1){
            Intent intent = new Intent(this, NoTermsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TermListActivity.class);
            startActivity(intent);
        }
    }

    public void progressTrackerBtnAction (View view) {
        Intent intent = new Intent(this, ProgressTrackerActivity.class);
        startActivity(intent);
    }
}
