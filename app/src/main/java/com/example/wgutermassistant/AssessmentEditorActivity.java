package com.example.wgutermassistant;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AssessmentEditorActivity extends AppCompatActivity {
    //Initialize flag to determine whether this is a new term or an edit to an existing term
    boolean isNew = true;
    //Initialize variables to hold form info
    long assessmentId;
    long courseId;
    long termId;
    Assessment assessment;
    EditText assessmentName;
    EditText assessmentDueDate;
    Spinner assessmentStatusSpinner;
    Context context;
    Calendar cal;
    ArrayAdapter<CharSequence> spinnerAdapter;
    //Initialize an SDF pattern for dates
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        context = this;

        Intent intent = getIntent();
        assessmentId = intent.getLongExtra("assessmentId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendars
        findViewObjects();
        cal = Calendar.getInstance(TimeZone.getDefault());

        //Setup status spinner
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.assessment_status_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assessmentStatusSpinner.setAdapter(spinnerAdapter);

        //If an id was pulled, populate EditText fields with data
        if (assessmentId!=-1) {
            isNew = false;
            populateDBData(assessmentId);
        }

        //Setup listeners and date pickers
        createDatePicker(cal,assessmentDueDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, if this is an existing term add the term button
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
        assessmentName = findViewById(R.id.assessmentName);
        assessmentDueDate = findViewById(R.id.assessmentDueDate);
        assessmentStatusSpinner = findViewById(R.id.assessmentStatusSpinner);
    }

    //Create DatePicker logic
    public void createDatePicker(final Calendar cal, final EditText field) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH,day);
                updateText(cal, field);
            }
        };

        //Display DatePicker dialog when the user taps in the due date field
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datepicker = new DatePickerDialog(context, date, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                datepicker.show();
            }
        });
    }

    //Update text on screen
    public void updateText(Calendar cal, EditText field) {
        field.setText(sdf.format(cal.getTime()));
    }

    public void populateDBData(long id) {
        assessment = DBDataManager.getAssessment(this, id);
        assessmentName.setText(assessment.getAssessmentName());
        assessmentDueDate.setText(assessment.getAssessmentDueDate());
        //Split start date on - symbol
        String[] startDateArr = assessment.getAssessmentDueDate().split("-");
        //Month is zero based so subtract 1
        cal.set(Integer.parseInt(startDateArr[0]),(Integer.parseInt(startDateArr[1])) - 1, Integer.parseInt(startDateArr[2]));
        //Get int position of status string in spinner and set default value
        int statusPos = spinnerAdapter.getPosition(assessment.getAssessmentStatus());
        assessmentStatusSpinner.setSelection(statusPos);
    }

    //Read and validate data then submit to DB
    public void assessmentSubmitBtnAction(final View view) {
        findViewObjects();
        //Verify name is not blank, no other restrictions
        if (assessmentName.getText().toString().matches("")) {
            //If name is blank, display a quick message informing user they must enter a title
            Toast titleAlert = Toast.makeText(this,"You must enter a name for the assessment.",Toast.LENGTH_LONG);
            titleAlert.show();
            return;
        }
        //Verify due date is not blank
        if (assessmentDueDate.getText().toString().matches("")) {
            //If either start or end date is blank, display a quick message informing user they must enter valid dates
            Toast blankDateAlert = Toast.makeText(this,"You must enter a due date for the assessment.",Toast.LENGTH_LONG);
            blankDateAlert.show();
            return;
        }

        //All data has been verified, prepare objects to pass to DB
        String nameStr = assessmentName.getText().toString();
        String dueDate = assessmentDueDate.getText().toString();
        String status = assessmentStatusSpinner.getSelectedItem().toString();

        if (isNew) {
            //Insert the assessment in the DB
            DBDataManager.insertAssessment(this,courseId,nameStr,dueDate,status);
        } else {
            //Update the existing term in the DB
            DBDataManager.updateAssessment(this,assessmentId,courseId,nameStr,dueDate,status);
        }
        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Assessment was successfully saved. Where to now?");
        builder.setPositiveButton("Add alerts for this assessment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add alerts button was clicked, clear data and move to AssessmentAlertEditorActivity
                refreshPage();
                //If this is a new assessment, need to get the assessment ID
                if (isNew) {
                    assessmentId = DBDataManager.getLastAddedAssessmentId(context);
                }
                Intent intent = new Intent(AssessmentEditorActivity.this, AssessmentAlertEditorActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("assessmentId",assessmentId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Create a new assessment",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add another assessment button was clicked, clear current data for new input
                isNew = true;
                refreshPage();
            }
        });
        builder.setNegativeButton("Go to assessment list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to assessment list button was clicked, clear data and move to AssessmentListActivity
                refreshPage();
                Intent intent = new Intent(AssessmentEditorActivity.this, AssessmentListActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("assessmentId",assessmentId);
                startActivity(intent);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    //Clear all entered text when clear button is clicked
    public void assessmentClearBtnAction(View view) {
        refreshPage();
    }

    //Clear all data on page/reset to DB info
    public void refreshPage() {
        findViewObjects();
        //If this is a new assessment, clear everything, otherwise reset it to what's in the DB
        if (isNew) {
            assessmentName.setText("");
            assessmentDueDate.setText("");
            cal = Calendar.getInstance(TimeZone.getDefault());
            createDatePicker(cal,assessmentDueDate);
            assessmentStatusSpinner.setSelection(0);
        } else {
            populateDBData(assessmentId);
        }
    }

}
