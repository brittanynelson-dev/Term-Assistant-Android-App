package com.example.wgutermassistant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProgressTrackerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Initialize CursorAdapter
    private CursorAdapter assessmentAlertCursorAdapter;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Find all view objects
        TextView coursesPlanned = findViewById(R.id.coursesPlanned);
        TextView coursesCompleted = findViewById(R.id.coursesCompleted);
        TextView coursesDropped = findViewById(R.id.coursesDropped);
        TextView assessmentsNotTaken = findViewById(R.id.assessmentsNotTaken);
        TextView assessmentsPassed = findViewById(R.id.assessmentsPassed);
        TextView assessmentsFailed = findViewById(R.id.assessmentsFailed);
        TextView coursePercentageTxt = findViewById(R.id.coursePercentageTxt);
        TextView assessmentPercentageTxt = findViewById(R.id.assessmentPercentageTxt);

        //Get the numbers of courses and assessments by status
        int coursesPlannedCount = DBDataManager.getCoursesByStatus(this,"'Plan To Take'") +
                DBDataManager.getCoursesByStatus(this,"'In Progress'");
        int coursesCompletedCount = DBDataManager.getCoursesByStatus(this,"'Completed'");
        int coursesDroppedCount = DBDataManager.getCoursesByStatus(this,"'Dropped'");
        int assessmentsNotTakenCount = DBDataManager.getAssessmentsByStatus(this,"'Not Yet Taken'");
        int assessmentsPassedCount = DBDataManager.getAssessmentsByStatus(this,"'Passed'");
        int assessmentsFailedCount = DBDataManager.getAssessmentsByStatus(this,"'Failed'");

        //Set view object text to the pulled numbers
        coursesPlanned.setText(Integer.toString(coursesPlannedCount));
        coursesCompleted.setText(Integer.toString(coursesCompletedCount));
        coursesDropped.setText(Integer.toString(coursesDroppedCount));
        assessmentsNotTaken.setText(Integer.toString(assessmentsNotTakenCount));
        assessmentsPassed.setText(Integer.toString(assessmentsPassedCount));
        assessmentsFailed.setText(Integer.toString(assessmentsFailedCount));

        //Calculate the percentage of completed courses compared to planned courses, don't count dropped courses in either
        double totalCourses = coursesPlannedCount + coursesCompletedCount;
        //If there are no courses, set a message informing users to set it
        if (totalCourses==0) {
            coursePercentageTxt.setText(R.string.course_percentage_error);
        } else {
            //Set text with completed percentage
            double coursePercent = ((double) coursesCompletedCount / totalCourses) * 100;
            coursePercentageTxt.setText("You are " + String.format("%.2f", coursePercent) + "% done with your courses.");
        }

        //Calculate the percentage of passed assessments compared to failed ones, don't count not yet taken assessments
        double totalAssessments = assessmentsFailedCount + assessmentsPassedCount;
        //If there are no courses, set a message informing users to set it
        if (totalAssessments==0) {
            //If there are no completed assessments but there are noy yet taken ones, display a different message.
            if (assessmentsNotTakenCount != 0) {
                assessmentPercentageTxt.setText(R.string.assessment_not_taken_error);
            } else {
                assessmentPercentageTxt.setText(R.string.assessment_percentage_error);
            }
        } else {
            //Set text with completed percentage
            double assessmentPercent = ((double) assessmentsPassedCount / totalAssessments) * 100;
            assessmentPercentageTxt.setText("You have successfully passed " + assessmentPercent + "% of assessments attempted.");
        }

        //Set up Course Alerts ListView
        //Get a list of all courses with an end date on or after today
        ArrayList<Course> coursesArr = DBDataManager.getfutureCourses(this);
        //Build a String array containing the course course name - start date - end date to be passed to the ListView
        ArrayList<String> courseStrArr = new ArrayList<>();
        //Verify coursesArr is not null, if there are courses populate the list
        if (coursesArr!=null) {
            //Iterate through course list and add data to String ArrayList
            for (int i = 0; i < coursesArr.size(); i++) {
                courseStrArr.add(coursesArr.get(i).getName() + " - Start Date: " + coursesArr.get(i).getStartDate() +
                        " - End Date: " + coursesArr.get(i).getEndDate());
            }
        }
        //Create ArrayAdapter to handle list
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courseStrArr);

        //Get reference to ListView and set adapter
        ListView courseListView = findViewById(R.id.scheduleCourseAlertsListView);
        courseListView.setAdapter(courseAdapter);

        //Set up assessment alerts ListView
        Cursor cursor = this.getContentResolver().query(DBProvider.ASSESSMENT_ALERTS_URI,DBOpenHelper.ASSESSMENT_ALERT_COLUMNS,
                DBOpenHelper.ASSESSMENT_ALERT_TIME + " >= date('now')",null, "datetime(" + DBOpenHelper.ASSESSMENT_ALERT_TIME + ") ASC Limit 1");

        String[] from = {DBOpenHelper.ASSESSMENT_ALERT_TIME, DBOpenHelper.ASSESSMENT_ALERT_MSG};
        int[] to = {R.id.lvTime,R.id.lvMsg};

        assessmentAlertCursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_alert_list_item, cursor, from, to);

        ListView assessmentAlertLv = findViewById(R.id.scheduleAssessmentAlertsListView);
        assessmentAlertLv.setAdapter(assessmentAlertCursorAdapter);

        getSupportLoaderManager().initLoader(0, null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, DBProvider.ASSESSMENT_ALERTS_URI, DBOpenHelper.ASSESSMENT_ALERT_COLUMNS,
                DBOpenHelper.ASSESSMENT_ALERT_TIME + " >= date('now')", null, "datetime(" + DBOpenHelper.ASSESSMENT_ALERT_TIME + ") ASC Limit 1");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        assessmentAlertCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        assessmentAlertCursorAdapter.swapCursor(null);
    }

}
