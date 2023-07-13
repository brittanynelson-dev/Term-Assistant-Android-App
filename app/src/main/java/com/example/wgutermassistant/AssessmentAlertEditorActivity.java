package com.example.wgutermassistant;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AssessmentAlertEditorActivity extends AppCompatActivity {
    //Initialize flag to determine whether this is a new alert or an edit to an existing one
    boolean isNew = true;
    //Initialize variables to hold form info
    long assessmentAlertId = -1;
    long assessmentId = -1;
    long courseId = -1;
    long termId = -1;
    AssessmentAlert assessmentAlert;
    TextView assessmentName;
    EditText aaeDate;
    Spinner aaeHourSpinner;
    Spinner aaeMinSpinner;
    EditText aaeMessage;
    ArrayAdapter<CharSequence> hourSpinnerAdapter;
    ArrayAdapter<CharSequence> minSpinnerAdapter;
    Context context;
    Calendar cal;
    //Initialize an SDF pattern for dates
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //Initialize SDF to parse datetime
    SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //Initialize am AlarmManager to handle alarms
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment_alert);
        context = this;

        Intent intent = getIntent();
        assessmentAlertId = intent.getLongExtra("assessmentAlertId", -1);
        assessmentId = intent.getLongExtra("assessmentId", -1);
        courseId = intent.getLongExtra("courseId", -1);
        termId = intent.getLongExtra("termId", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendar
        findViewObjects();
        cal = Calendar.getInstance(TimeZone.getDefault());

        //Populate Assessment name
        Assessment assessment = DBDataManager.getAssessment(this,assessmentId);
        assessmentName.setText(assessment.getAssessmentName());

        //Set up hour spinner
        hourSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.hour_array,
                android.R.layout.simple_spinner_item);
        hourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aaeHourSpinner.setAdapter(hourSpinnerAdapter);

        //Set up minute spinner
        minSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.minute_array,
                android.R.layout.simple_spinner_item);
        minSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aaeMinSpinner.setAdapter(minSpinnerAdapter);

        //If an id was pulled, populate EditText fields with data
        if (assessmentAlertId != -1) {
            isNew = false;
            populateDBData(assessmentAlertId);
        }

        //Setup listener and date picker
        createDatePicker(cal, aaeDate);

        //Instantiate AlarmManager
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
        assessmentName = findViewById(R.id.aaeAssessmentName);
        aaeDate = findViewById(R.id.aaeDate);
        aaeHourSpinner = findViewById(R.id.aaeHourSpinner);
        aaeMinSpinner = findViewById(R.id.aaeMinSpinner);
        aaeMessage = findViewById(R.id.aaeMessage);
    }

    //Create DatePicker logic
    public void createDatePicker(final Calendar cal, final EditText field) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                updateText(cal, field);
            }
        };

        //Display DatePicker dialog when the user taps in the startDate field
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datepicker = new DatePickerDialog(context, date, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datepicker.show();
            }
        });
    }

    //Update text on screen
    public void updateText(Calendar cal, EditText field) {
        field.setText(sdf.format(cal.getTime()));
    }

    public void populateDBData(long assessmentNoteId) {
        assessmentAlert = DBDataManager.getAssessmentAlert(this, assessmentAlertId);
        //Split datetime into components
        String date = assessmentAlert.getTimeStr().substring(0, 10);
        String hour = assessmentAlert.getTimeStr().substring(11, 13);
        String min = assessmentAlert.getTimeStr().substring(14, 16);
        aaeDate.setText(date);
        //Split start date on - symbol
        String[] dateArr = date.split("-");
        //Month is zero based so subtract 1
        cal.set(Integer.parseInt(dateArr[0]), (Integer.parseInt(dateArr[1])) - 1, Integer.parseInt(dateArr[2]));
        //Get int position of hour in spinner and set default value
        int hourPos = hourSpinnerAdapter.getPosition(hour);
        aaeHourSpinner.setSelection(hourPos);
        //Get int position of minute in spinner and set default value
        int minPos = minSpinnerAdapter.getPosition(min);
        aaeMinSpinner.setSelection(minPos);
        aaeMessage.setText(assessmentAlert.getMsgStr());
    }

    public void aaeSubmitBtnAction(View view) {
        findViewObjects();
        //Verify date is not blank
        if (aaeDate.getText().toString().matches("")) {
            //If date is blank, display a quick message informing user they must enter a valid date
            Toast blankDateAlert = Toast.makeText(this,"You must enter a date and time for the alert.",Toast.LENGTH_LONG);
            blankDateAlert.show();
            return;
        }
        //Verify message text is not blank, no other restrictions
        if (aaeMessage.getText().toString().matches("")) {
            //If note is blank, display a quick message informing user they must enter a note
            Toast msgAlert = Toast.makeText(getApplicationContext(), "You must enter some text for the alert message.", Toast.LENGTH_LONG);
            msgAlert.show();
            return;
        }

        //Most data has been verified, prepare objects to pass to DB
        String date = aaeDate.getText().toString();
        String hour = aaeHourSpinner.getSelectedItem().toString();
        String minute = aaeMinSpinner.getSelectedItem().toString();
        String dateTime = date + " " + hour + ":" + minute + ":00";
        String msg = aaeMessage.getText().toString();

        //Verify the alert is not in the past
        //Get the entered datetime as a Calendar object
        Calendar alertCal = Calendar.getInstance(TimeZone.getDefault());
        try {
            alertCal.setTime(dateTimeSdf.parse(dateTime));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        //Get current Date/Time
        Calendar currentCal = Calendar.getInstance(TimeZone.getDefault());
        //If the entered datetime is before the current time, display an error, alert is pointless
        if (alertCal.before(currentCal)) {
            Toast pastTimeAlert = Toast.makeText(getApplicationContext(), "The alert must be set for a future time.", Toast.LENGTH_LONG);
            pastTimeAlert.show();
            return;
        }

        //All checks completed, process insert/update
        if (isNew) {
            //Insert the course note in the DB
            DBDataManager.insertAssessmentAlert(this, assessmentId, dateTime, msg);
            //Get the assessmentAlertId if this is a new alert
            assessmentAlertId = DBDataManager.getLastAddedAssessmentAlertId(this);
        } else {
            //Delete the existing alert alarm
            deleteAlertAlarm();
            //Update the existing course note in the DB
            DBDataManager.updateAssessmentAlert(this, assessmentAlertId, assessmentId, dateTime, msg);
        }

        //Create a notification to be displayed at the given time
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(AssessmentAlertEditorActivity.this)
                .setContentTitle("Assessment Alert")
                .setWhen(alertCal.getTimeInMillis())
                .setContentText(assessmentName.getText().toString() + " - " + msg)
                .setSmallIcon(R.drawable.icons_bell_50);
        Notification notify = notiBuilder.getNotification();
        Intent notificationIntent = new Intent(AssessmentAlertEditorActivity.this, NotificationPublisherAssessment.class);
        notificationIntent.putExtra(NotificationPublisherAssessment.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisherAssessment.NOTIFICATION, notify);
        //Request code needs to be unique for cancellation purposes, pass in the assessmentId
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) assessmentAlertId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP,alertCal.getTimeInMillis(),pendingIntent);

        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Assessment alert was successfully saved. Where to now?");
        builder.setPositiveButton("Go to assessment alerts list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add courses button was clicked, move to CourseNotesListActivity
                Intent intent = new Intent(AssessmentAlertEditorActivity.this, AssessmentAlertsListActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Create a new alert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create a new alert button was clicked, clear current data for new input
                isNew = true;
                aaeDate.setText("");
                cal = Calendar.getInstance(TimeZone.getDefault());
                createDatePicker(cal,aaeDate);
                aaeHourSpinner.setSelection(0);
                aaeMinSpinner.setSelection(0);
                aaeMessage.setText("");
            }
        });
        builder.setNegativeButton("Go to assessment detail page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Go to course detail page button was clicked, clear data and move to CourseDetailActivity
                Intent intent = new Intent(AssessmentAlertEditorActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    //Delete this course note from DB
    public void aaeDeleteAlertBtnAction(View view) {
        //Display confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this alert?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, first cancel the system alarm
                deleteAlertAlarm();

                //Remove alert from DB then return to AssessmentDetailActivity
                DBDataManager.deleteAssessmentAlert(AssessmentAlertEditorActivity.this, assessmentAlertId);
                Intent intent = new Intent(AssessmentAlertEditorActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User changed mind, do nothing else
                return;
            }
        });
        AlertDialog confirmDelete = builder.create();
        confirmDelete.show();
    }

    public void deleteAlertAlarm() {
        //Parse the datetime into a Calendar object
        Calendar alertCal = Calendar.getInstance(TimeZone.getDefault());
        try {
            alertCal.setTime(dateTimeSdf.parse(assessmentAlert.getTimeStr()));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        //Mimic the intent from the original alert creation
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(AssessmentAlertEditorActivity.this)
                .setContentTitle("Assessment Alert")
                .setWhen(alertCal.getTimeInMillis())
                .setContentText(assessmentName.getText().toString() + " - " + assessmentAlert.getMsgStr())
                .setSmallIcon(R.drawable.icons_bell_50);
        Notification notify = notiBuilder.getNotification();
        Intent notificationIntent = new Intent(AssessmentAlertEditorActivity.this, NotificationPublisherAssessment.class);
        notificationIntent.putExtra(NotificationPublisherAssessment.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisherAssessment.NOTIFICATION, notify);
        //Cancel the alarm
        am.cancel(PendingIntent.getBroadcast(context, (int) assessmentAlertId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    //Allow user to go to AssessmentDetailActivity on button click
    public void aaeViewAssessmentBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentDetailActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        intent.putExtra("assessmentId",assessmentId);
        startActivity(intent);
    }
}
