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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseEditorActivity  extends AppCompatActivity {
    //Initialize flag to determine whether this is a new course or an edit to an existing course
    boolean isNew = true;
    //Initialize variables to hold form info
    long courseId = -1;
    long termId = -1;
    Course course;
    EditText courseName;
    EditText courseStartDate;
    EditText courseEndDate;
    Spinner courseStatusSpinner;
    EditText mentorName;
    EditText mentorPhone;
    EditText mentorEmail;
    ToggleButton courseAlertsTB;
    Context context;
    Calendar startCal;
    Calendar endCal;
    ArrayAdapter<CharSequence> spinnerAdapter;
    //Initialize an SDF pattern for dates
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //Initialize SDF to parse datetime
    SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //Initialize am AlarmManager to handle alarms
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        context = this;

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize form variables and Calendars
        findViewObjects();
        startCal = Calendar.getInstance(TimeZone.getDefault());
        endCal = Calendar.getInstance(TimeZone.getDefault());

        //Setup status spinner
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.course_status_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseStatusSpinner.setAdapter(spinnerAdapter);

        //If an id was pulled, populate EditText fields with data
        if (courseId!=-1) {
            isNew = false;
            populateDBData(courseId);
        }

        //Setup listeners and date pickers
        createDatePicker(startCal,courseStartDate);
        createDatePicker(endCal,courseEndDate);

        //Instantiate AlarmManager
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, if this is an existing term add the term button
        if (courseId!=-1) {
            getMenuInflater().inflate(R.menu.toolbar_home_term_course, menu);
        } else {
            getMenuInflater().inflate(R.menu.toolbar_home_term, menu);
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
        courseName = findViewById(R.id.courseName);
        courseStartDate = findViewById(R.id.courseStartDate);
        courseEndDate = findViewById(R.id.courseEndDate);
        courseStatusSpinner = findViewById(R.id.courseStatusSpinner);
        mentorName = findViewById(R.id.mentorName);
        mentorPhone = findViewById(R.id.mentorPhone);
        mentorEmail = findViewById(R.id.mentorEmail);
        courseAlertsTB = findViewById(R.id.courseAlertsTB);
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

    public void populateDBData(long courseId) {
        course = DBDataManager.getCourse(this,courseId);
        courseName.setText(course.getName());
        courseStartDate.setText(course.getStartDate());
        //Split start date on - symbol
        String[] startDateArr = course.getStartDate().split("-");
        //Month is zero based so subtract 1
        startCal.set(Integer.parseInt(startDateArr[0]),(Integer.parseInt(startDateArr[1])) - 1, Integer.parseInt(startDateArr[2]));
        courseEndDate.setText(course.getEndDate());
        //Split end date on - symbol
        String[] endDateArr = course.getEndDate().split("-");
        //Month is zero based so subtract 1
        endCal.set(Integer.parseInt(endDateArr[0]), (Integer.parseInt(endDateArr[1])) - 1, Integer.parseInt(endDateArr[2]));
        //Get int position of status string in spinner and set default value
        int statusPos = spinnerAdapter.getPosition(course.getStatus());
        courseStatusSpinner.setSelection(statusPos);
        mentorName.setText(course.getMentorName());
        mentorPhone.setText(course.getMentorPhone());
        mentorEmail.setText(course.getMentorEmail());
        if (course.getCourseAlerts()==1 && !courseAlertsTB.isChecked()) {
            courseAlertsTB.toggle();
        }
    }

    public void courseSubmitBtnAction(View view) {
        findViewObjects();
        //Verify title is not blank, no other restrictions
        if (courseName.getText().toString().matches("")) {
            //If name is blank, display a quick message informing user they must enter a name
            Toast nameAlert = Toast.makeText(getApplicationContext(),"You must enter a name for the course.",Toast.LENGTH_LONG);
            nameAlert.show();
            return;
        }
        //Verify start and end date are not blank
        if (courseStartDate.getText().toString().matches("") || courseEndDate.getText().toString().matches("")) {
            //If either start or end date is blank, display a quick message informing user they must enter valid dates
            Toast blankDateAlert = Toast.makeText(this,"You must enter a start and end date for the course.",Toast.LENGTH_LONG);
            blankDateAlert.show();
            return;
        }
        //Verify end date is not before start date
        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(courseStartDate.getText().toString());
            end = sdf.parse(courseEndDate.getText().toString());
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        if (end.before(start)) {
            //If end date is before start date, display a quick message informing user they must enter logical dates
            Toast badDateAlert = Toast.makeText(this,"The end date must be after the start date.",Toast.LENGTH_LONG);
            badDateAlert.show();
            return;
        }
        //Verify mentor name is not blank, no other restrictions
        if (mentorName.getText().toString().matches("")) {
            //If mentor name is blank, display a quick message informing user they must enter a name
            Toast mentorNameAlert = Toast.makeText(this,"You must enter the name of your course mentor.",Toast.LENGTH_LONG);
            mentorNameAlert.show();
            return;
        }
        //Verify mentor phone is not blank
        if(mentorPhone.getText().toString().matches("")) {
            //If mentor phone is blank, display a quick message informing user they must enter a phone number
            Toast mentorBlankPhoneAlert = Toast.makeText(this,"You must enter the valid phone number of your course mentor.",Toast.LENGTH_LONG);
            mentorBlankPhoneAlert.show();
            return;
        }
        //Verify phone number is in a valid format
        if (!mentorPhone.getText().toString().matches("^\\(?([0-9]{3})\\)?[-.●]?([0-9]{3})[-.●]?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+)\\s*)?$")) {
            //If mentor phone number doesn't match validation regex, display a quick message informing user they must enter a valid number
            Toast mentorInvalidPhoneAlert = Toast.makeText(this,"You must enter a valid phone number consisting of 7-10 digits with or without an extension.",Toast.LENGTH_LONG);
            mentorInvalidPhoneAlert.show();
            return;
        }
        //Verify mentor email is not blank
        if(mentorEmail.getText().toString().matches("")) {
            //If mentor email is blank, display a quick message informing user they must enter an email address
            Toast mentorBlankEmailAlert = Toast.makeText(this,"You must enter the valid email address of your course mentor.",Toast.LENGTH_LONG);
            mentorBlankEmailAlert.show();
            return;
        }
        //Verify email address is in a valid format
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern p = Pattern.compile(emailRegex);
        Matcher m = p.matcher(mentorEmail.getText().toString());
        if (!m.matches()) {
            //If mentor email doesn't match validation regex, display a quick message informing user they must enter a valid email
            Toast mentorInvalidEmailAlert = Toast.makeText(this,"You must enter a valid email address (Ex. name@email.com).",Toast.LENGTH_LONG);
            mentorInvalidEmailAlert.show();
            return;
        }
        //All data has been verified, prepare objects to pass to DB
        final String nameStr = courseName.getText().toString();
        String startDateStr = courseStartDate.getText().toString();
        String endDateStr = courseEndDate.getText().toString();
        String statusStr = courseStatusSpinner.getSelectedItem().toString();
        String mentorNameStr = mentorName.getText().toString();
        String mentorPhoneStr = mentorPhone.getText().toString();
        String mentorEmailStr = mentorEmail.getText().toString();
        int courseAlerts = 0;
        if (courseAlertsTB.isChecked()) {
            courseAlerts = 1;
        }

        //If this isn't a new course, delete any existing course alerts before creating new ones
        if (!isNew) {
            //Get CourseAlert objects for start and end alerts
            CourseAlert startCourseAlert = DBDataManager.getCourseAlert(this, courseId, "'Course " + course.getName() + " starts'");
            CourseAlert endCourseAlert = DBDataManager.getCourseAlert(this, courseId, "'Course " + course.getName() + " ends'");

            //If a start alert was found, delete its alarm
            if (startCourseAlert!=null) {
                deleteAlertAlarm(startCourseAlert);
            }
            //If an end alert was found, delete its alarm
            if (endCourseAlert!=null) {
                deleteAlertAlarm(endCourseAlert);
            }
            //Delete any related course alerts from DB
            DBDataManager.deleteCourseAlerts(this,courseId);
        }

        //Data verified, proceed with insert/update
        if (isNew) {
            //Insert the course in the DB
            DBDataManager.insertCourse(this,termId,nameStr,startDateStr,endDateStr,statusStr,mentorNameStr,mentorPhoneStr,mentorEmailStr,courseAlerts);
            //Since this is a new course, need to update the course ID
            courseId = DBDataManager.getLastAddedCourseId(context);
        } else {
            //Update the existing course in the DB
            DBDataManager.updateCourse(this,courseId,termId,nameStr,startDateStr,endDateStr,statusStr,mentorNameStr,mentorPhoneStr,mentorEmailStr,courseAlerts);
        }
        //If course alerts are on, add them to the DB and create alarm if they are in the future
        if (courseAlerts==1) {
            //Verify the alert is not in the past
            //Get the entered dates as Calendar objects
            Calendar alertStartCal = Calendar.getInstance(TimeZone.getDefault());
            Calendar alertEndCal = Calendar.getInstance(TimeZone.getDefault());
            try {
                alertStartCal.setTime(sdf.parse(startDateStr));
                alertEndCal.setTime(sdf.parse(endDateStr));
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            //Get current Date
            Calendar currentCal = Calendar.getInstance(TimeZone.getDefault());
            //If start date is not before current date, add to DB and create an alarm for it
            if (!alertStartCal.before(currentCal)) {
                DBDataManager.insertCourseAlert(this,courseId,startDateStr, "Course " + nameStr + " starts");
                CourseAlert startCourseAlert = DBDataManager.getCourseAlert(this,courseId,"'Course " + nameStr + " starts'");
                createAlertAlarm(startCourseAlert);
            }
            //If start date is not before current date, add to DB and create an alarm for it
            if (!alertEndCal.before(currentCal)) {
                DBDataManager.insertCourseAlert(this,courseId,endDateStr, "Course " + nameStr + " ends");
                CourseAlert endCourseAlert = DBDataManager.getCourseAlert(this,courseId,"'Course " + nameStr + " ends'");
                createAlertAlarm(endCourseAlert);
            }
        }
        //Build and show an AlertDialog to advise user the save was successful
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Course was successfully saved. Where to now?");
        builder.setItems(R.array.course_saved_options_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i) {
                    case 0: //Add assessments was clicked, clear data and move to AssessmentEditorActivity
                        refreshPage();
                        Intent aeIntent = new Intent(CourseEditorActivity.this, AssessmentEditorActivity.class);
                        aeIntent.putExtra("termId",termId);
                        aeIntent.putExtra("courseId",courseId);
                        startActivity(aeIntent);
                        break;
                    case 1: //Add course notes button was clicked, clear data and move to CourseNotesEditorActivity
                        refreshPage();
                        //If this is a new course, need to get the course ID
                        if (isNew) {
                            courseId = DBDataManager.getLastAddedCourseId(context);
                        }
                        Intent cnIntent = new Intent(CourseEditorActivity.this,CourseNotesEditorActivity.class);
                        cnIntent.putExtra("courseId",courseId);
                        cnIntent.putExtra("termId",termId);
                        startActivity(cnIntent);
                        break;
                    case 2: //Add another course button was clicked, clear current data for new input
                        isNew = true;
                        refreshPage();
                        break;
                    case 3: //Go to course list button was clicked, clear data and move to CourseListActivity
                        refreshPage();
                        Intent intent = new Intent(CourseEditorActivity.this,CourseListActivity.class);
                        intent.putExtra("termId",termId);
                        startActivity(intent);
                        break;
                    default: //Should never hit this, just in case clear course info and break
                        isNew = true;
                        refreshPage();
                        break;
                }
            }
        });
        AlertDialog success = builder.create();
        success.show();
    }

    public void createAlertAlarm(CourseAlert courseAlert) {
        //Get the entered date as a Calendar object
        Calendar alertCal = Calendar.getInstance(TimeZone.getDefault());
        try {
            alertCal.setTime(sdf.parse(courseAlert.getTimeStr()));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        //Create a notification and schedule it as an alarm
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(CourseEditorActivity.this)
                .setContentTitle("Course Alert")
                .setWhen(alertCal.getTimeInMillis())
                .setContentText(courseAlert.getMsgStr())
                .setSmallIcon(R.drawable.icons_bell_50);
        Notification notify = notiBuilder.getNotification();
        Intent notificationIntent = new Intent(CourseEditorActivity.this, NotificationPublisherCourse.class);
        notificationIntent.putExtra(NotificationPublisherCourse.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisherCourse.NOTIFICATION, notify);
        //Request code needs to be unique for cancellation purposes, pass in the courseAlertId
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) courseAlert.getCourseAlertId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP,alertCal.getTimeInMillis(),pendingIntent);
    }

    public void deleteAlertAlarm(CourseAlert courseAlert) {
        //Get the entered date as a Calendar object
        Calendar alertCal = Calendar.getInstance(TimeZone.getDefault());
        try {
            alertCal.setTime(sdf.parse(courseAlert.getTimeStr()));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        //Mimic the intent from the original alert creation
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(CourseEditorActivity.this)
                .setContentTitle("Course Alert")
                .setWhen(alertCal.getTimeInMillis())
                .setContentText(courseAlert.getMsgStr())
                .setSmallIcon(R.drawable.icons_bell_50);
        Notification notify = notiBuilder.getNotification();
        Intent notificationIntent = new Intent(CourseEditorActivity.this, NotificationPublisherCourse.class);
        notificationIntent.putExtra(NotificationPublisherCourse.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisherCourse.NOTIFICATION, notify);
        //Cancel the alarm
        am.cancel(PendingIntent.getBroadcast(context, (int) courseAlert.getCourseAlertId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }


    //Clear all entered text when clear button is clicked
    public void courseClearBtnAction(View view) {
        refreshPage();
    }

    //Clear all data on page/reset to DB info
    public void refreshPage() {
        findViewObjects();
        //If this is a new course, clear everything, otherwise reset it to what's in the DB
        if (isNew) {
            courseName.setText("");
            courseStartDate.setText("");
            startCal = Calendar.getInstance(TimeZone.getDefault());
            createDatePicker(startCal,courseStartDate);
            courseEndDate.setText("");
            endCal = Calendar.getInstance(TimeZone.getDefault());
            createDatePicker(endCal,courseEndDate);
            courseStatusSpinner.setSelection(0);
            mentorName.setText("");
            mentorPhone.setText("");
            mentorEmail.setText("");
            //Only toggle button if it's currently set to on
            if (courseAlertsTB.isChecked()) {
                courseAlertsTB.toggle();
            }
        } else {
            populateDBData(courseId);
        }
    }
}
