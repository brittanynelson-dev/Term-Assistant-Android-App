package com.example.wgutermassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ChangeCourseTermActivity extends AppCompatActivity {
    //Initialize variables
    long courseId;
    long termId;
    TextView changeTermCourseName;
    Spinner changeTermSpinner;
    SpinnerAdapter spinnerAdapter;
    Course course;
    ArrayList<Term> termsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_course_term);

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables
        findViewObjects();

        //Get an ArrayList of terms
        termsArr = DBDataManager.getAllTerms(ChangeCourseTermActivity.this);
        //Build a second list of titles only, will share the same pos int to retrieve term Id later
        ArrayList<String> termTitles = new ArrayList<>();
        for (int i = 0; i < termsArr.size(); i++) {
            termTitles.add(termsArr.get(i).getTitle());
        }

        //Add the term titles to an ArrayAdapter and setup Spinner
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeCourseTermActivity.this,
                android.R.layout.simple_spinner_item, termTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeTermSpinner.setAdapter(adapter);

        //Populate the course name on the page
        course = DBDataManager.getCourse(this,courseId);
        changeTermCourseName.setText(course.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.toolbar_home_term_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_term:
                Intent termIntent = new Intent(this,TermDetailActivity.class);
                termIntent.putExtra("termId",termId);
                startActivity(termIntent);
                return true;

            case R.id.action_course:
                Intent courseIntent = new Intent(this,CourseDetailActivity.class);
                courseIntent.putExtra("termId",termId);
                courseIntent.putExtra("courseId",courseId);
                startActivity(courseIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //Find view objects
    public void findViewObjects() {
        changeTermCourseName = findViewById(R.id.changeTermCourseName);
        changeTermSpinner = findViewById(R.id.changeTermSpinner);
    }

    public void courseChangeTermSubmitBtnAction(View view) {
        //Get ID of selected term
        int selected = changeTermSpinner.getSelectedItemPosition();
        termId = termsArr.get(selected).getTermId();
        //Update term in DB
        DBDataManager.updateCourseTerm(ChangeCourseTermActivity.this,courseId,termId);
        //Display an AlertDialog advising the change was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Term change was successfully saved. Where to now?");
        builder.setPositiveButton("Back to Course Detail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Course Detail button was clicked, move to CourseDetailActivity
                Intent intent = new Intent(ChangeCourseTermActivity.this, CourseDetailActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Go to course list",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Course list button was clicked, clear current data for new input
                Intent intent = new Intent(ChangeCourseTermActivity.this, CourseListActivity.class);
                intent.putExtra("termId",termId);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Go to term list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to term list button was clicked, clear data and move to TermListActivity
                Intent intent = new Intent(ChangeCourseTermActivity.this, TermListActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    public void courseChangeTermCancelBtnAction(View view) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }


}
