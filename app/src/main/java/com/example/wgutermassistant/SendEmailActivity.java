package com.example.wgutermassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendEmailActivity extends AppCompatActivity {
    //Initialize variables
    long assessmentId;
    long courseId;
    long termId;
    int flag;
    EditText emailAddress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        Intent intent = getIntent();
        assessmentId = intent.getLongExtra("assessmentId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);
        flag = intent.getIntExtra("flag",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get EditText object from activity
        emailAddress = findViewById(R.id.emailAddress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu based on the flag
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

    public void sendEmailBtnAction(View view) {
        //Verify email address is not blank
        if(emailAddress.getText().toString().matches("")) {
            //If email is blank, display a quick message informing user they must enter an email address
            Toast mentorBlankEmailAlert = Toast.makeText(this,"You must enter a valid email address to send notes to.",Toast.LENGTH_LONG);
            mentorBlankEmailAlert.show();
            return;
        }
        //Verify email address is in a valid format
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern p = Pattern.compile(emailRegex);
        Matcher m = p.matcher(emailAddress.getText().toString());
        if (!m.matches()) {
            //If email doesn't match validation regex, display a quick message informing user they must enter a valid email
            Toast mentorInvalidEmailAlert = Toast.makeText(this,"You must enter a valid email address (Ex. name@email.com).",Toast.LENGTH_LONG);
            mentorInvalidEmailAlert.show();
            return;
        }

        //Email is verified, send either course or assessment notes (course=0, assessment=1)
        //Initialize strings to hold email, subject, and body text
        String email = emailAddress.getText().toString();
        String subject = "Notes for ";
        String bodyText = "";

        if (flag==0) {
            //Get course name for subject line
            Course course = DBDataManager.getCourse(this, courseId);
            subject = subject + "course " + course.getName();

            //Pull all course notes for this course
            ArrayList<String> courseNotes = DBDataManager.getAllCourseNotes(this,courseId);

            System.out.println("courseNotes size = " + courseNotes.size());
            System.out.println("courseNotes.isEmpty() = " +courseNotes.isEmpty());

            //Verify notes were pulled, if not display an error and return to CourseNotesListActivity
            if (courseNotes.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No notes for this course were found. There must be at least one note before an email can be sent.");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SendEmailActivity.this, CourseNotesListActivity.class);
                        intent.putExtra("termId",termId);
                        intent.putExtra("courseId",courseId);
                        startActivity(intent);
                    }
                });
                AlertDialog error = builder.create();
                error.show();
                return;
            }
            //Add each note in the ArrayList to the bodyText String
            for (int i = 0; i < courseNotes.size(); i++) {
                bodyText = bodyText + courseNotes.get(i) + "\n";
            }
            //Produce email intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
            startActivity(Intent.createChooser(emailIntent,"Email Course Notes"));
        } else {
            //Get assessment name for subject line
            Assessment assessment = DBDataManager.getAssessment(this, assessmentId);
            subject = subject + "assessment " + assessment.getAssessmentName();

            //Pull all assessment notes for this assessment
            ArrayList<String> assessmentNotes = DBDataManager.getAllAssessmentNotes(this,assessmentId);

            //Verify notes were pulled, if not display an error and return to CourseNotesListActivity
            if (assessmentNotes.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No notes for this assessment were found. There must be at least one note before an email can be sent.");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SendEmailActivity.this, AssessmentDetailActivity.class);
                        intent.putExtra("termId",termId);
                        intent.putExtra("courseId",courseId);
                        intent.putExtra("assessmentId",assessmentId);
                        startActivity(intent);
                    }
                });
                AlertDialog error = builder.create();
                error.show();
                return;
            }
            //Add each note in the ArrayList to the bodyText String
            for (int i = 0; i < assessmentNotes.size(); i++) {
                bodyText = bodyText + assessmentNotes.get(i) + "\n";
            }
            //Produce email intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
            startActivity(Intent.createChooser(emailIntent,"Email Course Notes"));
        }
    }

    public void emailCancelBtnAction(View view) {
        if (flag==0) {
            Intent intent = new Intent(this,CourseNotesListActivity.class);
            intent.putExtra("courseId", courseId);
            intent.putExtra("termId",termId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this,AssessmentDetailActivity.class);
            intent.putExtra("assessmentId",assessmentId);
            intent.putExtra("courseId", courseId);
            intent.putExtra("termId",termId);
            startActivity(intent);
        }
    }
}
