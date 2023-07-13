package com.example.wgutermassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CourseNotesEditorActivity extends AppCompatActivity {
    //Initialize flag to determine whether this is a new note or an edit to an existing course
    boolean isNew = true;
    //Initialize variables to hold form info
    long courseNoteId = -1;
    long courseId = -1;
    long termId = -1;
    String courseName = "Course Note";
    CourseNote courseNote;
    TextView courseNotesEditorCourseName;
    EditText courseNotesTxt;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_note);
        context = this;

        Intent intent = getIntent();
        courseNoteId = intent.getLongExtra("courseNoteId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);
        courseName = intent.getStringExtra("courseName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendars
        findViewObjects();

        //If an id was pulled, populate EditText fields with data
        if (courseNoteId!=-1) {
            isNew = false;
            populateDBData(courseNoteId);
        }

        //Populate course name
        Course course = DBDataManager.getCourse(this, courseId);
        courseNotesEditorCourseName.setText(course.getName());
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

    //Read all data entered in form
    public void findViewObjects() {
        courseNotesEditorCourseName = findViewById(R.id.courseNotesEditorCourseName);
        courseNotesTxt = findViewById(R.id.courseNotesTxt);
    }

    public void populateDBData(long courseNoteId) {
        courseNote = DBDataManager.getCourseNote(this,courseNoteId);
        courseNotesEditorCourseName.setText(courseName);
        courseNotesTxt.setText(courseNote.getTextStr());
    }

    public void courseNotesSubmitBtnAction(View view) {
        findViewObjects();
        //Verify note text is not blank, no other restrictions
        if (courseNotesTxt.getText().toString().matches("")) {
            //If note is blank, display a quick message informing user they must enter a note
            Toast nameAlert = Toast.makeText(getApplicationContext(),"You must enter some text for the note.",Toast.LENGTH_LONG);
            nameAlert.show();
            return;
        }
        //All data has been verified, prepare objects to pass to DB
        String text = courseNotesTxt.getText().toString();

        if (isNew) {
            //Insert the course note in the DB
            DBDataManager.insertCourseNote(this,courseId,text);
        } else {
            //Update the existing course note in the DB
            DBDataManager.updateCourseNote(this,courseNoteId,courseId,text);
        }
        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Course note was successfully saved. Where to now?");
        builder.setPositiveButton("Go to course notes list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add courses button was clicked, move to CourseNotesListActivity
                Intent intent = new Intent(CourseNotesEditorActivity.this, CourseNotesListActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Create a new note",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create a new note button was clicked, clear current data for new input
                isNew = true;
                courseNotesTxt.setText("");
            }
        });
        builder.setNegativeButton("Go to course detail page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to course detail page button was clicked, clear data and move to CourseDetailActivity
                Intent intent = new Intent(CourseNotesEditorActivity.this, CourseDetailActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    //Delete this course note from DB
    public void courseNotesDeleteBtnAction(View view) {
        //Display confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, remove course note from DB then return to CourseNoteListActivity
                DBDataManager.deleteCourseNote(CourseNotesEditorActivity.this,courseNoteId);
                Intent intent = new Intent(CourseNotesEditorActivity.this, CourseNotesListActivity.class);
                intent.putExtra("courseId",courseId);
                intent.putExtra("termId",termId);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User changed mind, do nothing else
                return;
            }
        });
    }

    //Allow user to go to AssessmentEditorActivity on button click
    public void courseNotesViewNotesBtnAction(View view) {
        Intent intent = new Intent(this,CourseNotesListActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }

    //Allow user to go to AssessmentEditorActivity on button click
    public void courseNotesViewCourseBtnAction(View view) {
        Intent intent = new Intent(this,CourseDetailActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }
}
