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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseDetailActivity extends AppCompatActivity {
    //Initialize variables
    long courseId;
    long termId;
    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Call method to pull course info using id
        course = DBDataManager.getCourse(this,courseId);

        //Get TextView objects from activity
        TextView courseName = findViewById(R.id.courseName);
        TextView startDate = findViewById(R.id.courseDetailStartDate);
        TextView endDate = findViewById(R.id.courseDetailEndDate);
        TextView courseAlerts = findViewById(R.id.courseAlertsTxt);
        TextView mentorName = findViewById(R.id.mentorName);
        TextView mentorPhone = findViewById(R.id.mentorPhone);
        TextView mentorEmail = findViewById(R.id.mentorEmail);
        TextView status = findViewById(R.id.statusTxt);

        //Populate TextViews with course data
        courseName.setText(course.getName());
        startDate.setText("Start Date:  " + course.getStartDate());
        endDate.setText("End Date:    " + course.getEndDate());
        if (course.getCourseAlerts()==1) {
            courseAlerts.setText(R.string.course_alerts_active);
        } else {
            courseAlerts.setText(R.string.course_alerts_inactive);
        }
        mentorName.setText(course.getMentorName());
        mentorPhone.setText(course.getMentorPhone());
        mentorEmail.setText(course.getMentorEmail());
        status.setText(course.getStatus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.toolbar_home_term, menu);

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

    public void viewAssessmentsBtnAction(View view) {
        Intent intent = new Intent(this, AssessmentListActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }

    public void viewCourseNotesBtnAction(View view) {
        Intent intent = new Intent(this, CourseNotesListActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }

    //Move to CourseEditorActivity
    public void editCourseBtnAction(View view) {
        Intent intent = new Intent(this, CourseEditorActivity.class);
        intent.putExtra("courseId",courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    //Delete this course from DB
    public void deleteCourseBtnAction(View view) {
        //Display confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this course?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, remove course from DB then return to CourseListActivity
                DBDataManager.deleteCourse(CourseDetailActivity.this,courseId);
                Intent intent = new Intent(CourseDetailActivity.this, CourseListActivity.class);
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
        AlertDialog confirmDelete = builder.create();
        confirmDelete.show();
    }

    public void changeCourseTermBtnAction(View view) {
        Intent intent = new Intent(this, ChangeCourseTermActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }

    public void courseViewCoursesBtnAction(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }
}
