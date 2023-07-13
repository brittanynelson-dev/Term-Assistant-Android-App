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

public class AssessmentNotesEditorActivity extends AppCompatActivity {
    //Initialize flag to determine whether this is a new note or an edit to an existing course
    boolean isNew = true;
    //Initialize variables to hold form info
    long assessmentNoteId = -1;
    long assessmentId = -1;
    long courseId = -1;
    long termId = -1;
    AssessmentNote assessmentNote;
    TextView assessmentName;
    EditText assessmentNoteTxt;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment_note);
        context = this;

        Intent intent = getIntent();
        assessmentNoteId = intent.getLongExtra("assessmentNoteId",-1);
        assessmentId = intent.getLongExtra("assessmentId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendars
        findViewObjects();

        //If an id was pulled, populate EditText fields with data
        if (assessmentNoteId!=-1) {
            isNew = false;
            populateDBData(assessmentNoteId);
        }
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
        assessmentName = findViewById(R.id.aeAssessmentName);
        assessmentNoteTxt = findViewById(R.id.assessmentNoteTxt);
    }

    public void populateDBData(long assessmentNoteId) {
        assessmentNote = DBDataManager.getAssessmentNote(this,assessmentNoteId);
        Assessment assessment = DBDataManager.getAssessment(this,assessmentId);
        assessmentName.setText(assessment.getAssessmentName());
        assessmentNoteTxt.setText(assessmentNote.getTextStr());
    }

    public void assessmentNotesSubmitBtnAction(View view) {
        findViewObjects();
        //Verify note text is not blank, no other restrictions
        if (assessmentNoteTxt.getText().toString().matches("")) {
            //If note is blank, display a quick message informing user they must enter a note
            Toast nameAlert = Toast.makeText(getApplicationContext(),"You must enter some text for the note.",Toast.LENGTH_LONG);
            nameAlert.show();
            return;
        }
        //All data has been verified, prepare objects to pass to DB
        String text = assessmentNoteTxt.getText().toString();

        if (isNew) {
            //Insert the course note in the DB
            DBDataManager.insertAssessmentNote(this,assessmentId,text);
        } else {
            //Update the existing course note in the DB
            DBDataManager.updateAssessmentNote(this,assessmentNoteId,assessmentId,text);
        }
        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Assessment note was successfully saved. Where to now?");
        builder.setPositiveButton("Go to assessment detail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Assessment list button was clicked, move to AssessmentAlertsListActivity
                Intent intent = new Intent(AssessmentNotesEditorActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("assessmentId",assessmentId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Create a new note",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create a new note button was clicked, clear current data for new input
                isNew = true;
                assessmentNoteTxt.setText("");
            }
        });
        builder.setNegativeButton("Go to course detail page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to course detail page button was clicked, clear data and move to CourseDetailActivity
                Intent intent = new Intent(AssessmentNotesEditorActivity.this, CourseDetailActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    //Delete this course note from DB
    public void assessmentNotesDeleteBtnAction(View view) {
        //Display confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, remove course note from DB then return to AssessmentDetailActivity
                DBDataManager.deleteAssessmentNote(AssessmentNotesEditorActivity.this,assessmentNoteId);
                Intent intent = new Intent(AssessmentNotesEditorActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("assessmentId",assessmentId);
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

    //Allow user to go to AssessmentDetailActivity on button click
    public void aeViewAssessmentBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentDetailActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        intent.putExtra("assessmentId",assessmentId);
        startActivity(intent);
    }
}
