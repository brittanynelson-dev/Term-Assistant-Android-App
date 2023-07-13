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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class TermEditorActivity extends AppCompatActivity {
    //Initialize flag to determine whether this is a new term or an edit to an existing term
    boolean isNew = true;
    //Initialize variables to hold form info
    long termId;
    Term term;
    EditText title;
    EditText termStartDate;
    EditText termEndDate;
    CheckBox current;
    Context context;
    Calendar startCal;
    Calendar endCal;
    //Initialize an SDF pattern for dates
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        context = this;

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendars
        findViewObjects();
        startCal = Calendar.getInstance(TimeZone.getDefault());
        endCal = Calendar.getInstance(TimeZone.getDefault());

        //If an id was pulled, populate EditText fields with data
        if (termId!=-1) {
            isNew = false;
            populateDBData(termId);
        }

        //Setup listeners and date pickers
        createDatePicker(startCal,termStartDate);
        createDatePicker(endCal,termEndDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, if this is an existing term add the term button
        if (termId!=-1) {
            getMenuInflater().inflate(R.menu.toolbar_home_term, menu);
        } else {
            getMenuInflater().inflate(R.menu.toolbar_home, menu);
        }
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //Read all data entered in form
    public void findViewObjects() {
        title = findViewById(R.id.termTitle);
        termStartDate = findViewById(R.id.termStartDate);
        termEndDate = findViewById(R.id.termEndDate);
        current = findViewById(R.id.currentTermCB);
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

        //Display DatePicker dialog when the user taps in the startDate field
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

    //Read and validate data then submit to DB
    public void submitBtnAction(final View view) {
        findViewObjects();
        //Verify title is not blank, no other restrictions
        if (title.getText().toString().matches("")) {
            //If title is blank, display a quick message informing user they must enter a title
            Toast titleAlert = Toast.makeText(this,"You must enter a title for the term.",Toast.LENGTH_LONG);
            titleAlert.show();
            return;
        }
        //Verify start and end date are not blank
        if (termStartDate.getText().toString().matches("") || termEndDate.getText().toString().matches("")) {
            //If either start or end date is blank, display a quick message informing user they must enter valid dates
            Toast blankDateAlert = Toast.makeText(this,"You must enter a start and end date for the term.",Toast.LENGTH_LONG);
            blankDateAlert.show();
            return;
        }
        //Verify end date is not before start date
        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(termStartDate.getText().toString());
            end = sdf.parse(termEndDate.getText().toString());
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        if (end.before(start)) {
            //If end date is before start date, display a quick message informing user they must enter logical dates
            Toast badDateAlert = Toast.makeText(this,"The end date must be after the start date.",Toast.LENGTH_LONG);
            badDateAlert.show();
            return;
        }

        //All data has been verified, prepare objects to pass to DB
        String titleStr = title.getText().toString();
        String startDateStr = termStartDate.getText().toString();
        String endDateStr = termEndDate.getText().toString();
        int isCurrent = 0;
        if (current.isChecked()) {
            isCurrent = 1;
        } else {
            //If the current term box isn't checked and there is no current term selected in the DB or this is the active term, set the current flag to true for this term anyway
            long activeID = DBDataManager.getActiveTermID(this);
            if (activeID==-1 || activeID==termId){
                isCurrent = 1;
                if (!isNew) {
                    //If this isn't a new term, create an alert to inform the user they must have an active term, select or create a new active term instead of deactivating this one
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You must have an active term. Select a new active term from the Term List or create a new term.");
                    builder.setPositiveButton("Discard changes and create new term", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Discard changes was clicked, set isNew flag to true and reset all fields
                            isNew = true;
                            refreshPage();
                            return;
                        }
                    });
                    builder.setNeutralButton("Continue editing this term", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing, allow user to make changes and resubmit
                            return;
                        }
                    });
                    builder.setNegativeButton("Go to term list", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Go to term list button was clicked, clear data and move to TermListActivity
                            refreshPage();
                            goToTermList(view);
                        }
                    });
                    AlertDialog activeTermWarning = builder.create();
                    activeTermWarning.show();
                    return;
                }
            }
        }
        //If isCurrent is 1 after checks, deactivate the currently active term in the DB as long as it isn't this term
        long activeTermId = DBDataManager.getActiveTermID(this);
        if (isCurrent==1 && activeTermId!=-1 && activeTermId!=termId) {
            DBDataManager.deactivateTerm(this, activeTermId);
        }
        if (isNew) {
            //Insert the term in the DB
            DBDataManager.insertTerm(this,titleStr,startDateStr,endDateStr,isCurrent);
        } else {
            //Update the existing term in the DB
            DBDataManager.updateTerm(this,titleStr,startDateStr,endDateStr,isCurrent,termId);
        }
        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Term was successfully saved. Where to now?");
        builder.setPositiveButton("Add courses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add courses button was clicked, clear data and move to CourseEditorActivity
                refreshPage();
                //If this is a new term, need to get the term ID
                if (isNew) {
                    termId = DBDataManager.getLastAddedTermId(context);
                }
                Intent intent = new Intent(TermEditorActivity.this, CourseEditorActivity.class);
                intent.putExtra("termId",termId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Create a new term",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add another term button was clicked, clear current data for new input
                isNew = true;
                refreshPage();
            }
        });
        builder.setNegativeButton("Go to term list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to term list button was clicked, clear data and move to TermListActivity
                refreshPage();
                goToTermList(view);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    //Clear all entered text when clear button is clicked
    public void clearBtnAction(View view) {
        refreshPage();
    }

    //Load the Term List activity
    public void goToTermList(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivity(intent);
    }

    //Clear all data on page/reset to DB info
    public void refreshPage() {
        findViewObjects();
        //If this is a new term, clear everything, otherwise reset it to what's in the DB
        if (isNew) {
            title.setText("");
            termStartDate.setText("");
            startCal = Calendar.getInstance(TimeZone.getDefault());
            createDatePicker(startCal,termStartDate);
            termEndDate.setText("");
            endCal = Calendar.getInstance(TimeZone.getDefault());
            createDatePicker(endCal,termEndDate);
            //Only toggle checkbox if it's currently checked
            if (current.isChecked()) {
                current.toggle();
            }
        } else {
            populateDBData(termId);
        }
    }

    public void populateDBData(long id) {
        term = DBDataManager.getTerm(this, id);
        title.setText(term.getTitle());
        termStartDate.setText(term.getStartDate());
        //Split start date on - symbol
        String[] startDateArr = term.getStartDate().split("-");
        //Month is zero based so subtract 1
        startCal.set(Integer.parseInt(startDateArr[0]),(Integer.parseInt(startDateArr[1])) - 1, Integer.parseInt(startDateArr[2]));
        termEndDate.setText(term.getEndDate());
        //Split end date on - symbol
        String[] endDateArr = term.getEndDate().split("-");
        //Month is zero based so subtract 1
        endCal.set(Integer.parseInt(endDateArr[0]), (Integer.parseInt(endDateArr[1])) - 1, Integer.parseInt(endDateArr[2]));
        if (term.getCurrent()==1 && !current.isChecked()) {
            current.toggle();
        }
    }
}
