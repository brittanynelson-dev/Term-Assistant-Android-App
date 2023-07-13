package com.example.wgutermassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AssessmentDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Initialize variables
    long assessmentId;
    long courseId;
    long termId;
    Assessment assessment;
    private CursorAdapter assessmentNotesCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        Intent intent = getIntent();
        assessmentId = intent.getLongExtra("assessmentId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Call method to pull assessment info using id
        assessment = DBDataManager.getAssessment(this,assessmentId);

        //Get TextView objects from activity
        TextView assessmentName = findViewById(R.id.adAssessmentName);
        TextView dueDate = findViewById(R.id.adDueDate);
        TextView status = findViewById(R.id.adStatusTxt);

        //Populate TextViews with assessment data
        assessmentName.setText(assessment.getAssessmentName());
        dueDate.setText("Due Date:  " + assessment.getAssessmentDueDate());
        status.setText(assessment.getAssessmentStatus());

        //Setup assessment notes ListView
        Cursor cursor = this.getContentResolver().query(DBProvider.ASSESSMENT_NOTES_URI,DBOpenHelper.ASSESSMENT_NOTES_COLUMNS,
                DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " + assessmentId,null,null);

        String[] from = {DBOpenHelper.ASSESSMENT_NOTE_TEXT};
        int[] to = {R.id.lvNoteText};

        assessmentNotesCursorAdapter = new SimpleCursorAdapter(this, R.layout.single_list_item, cursor, from, to);

        ListView lv = findViewById(R.id.assessmentNotesListView);
        lv.setAdapter(assessmentNotesCursorAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long assessmentNoteId) {
                Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentNotesEditorActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("assessmentId",assessmentId);
                intent.putExtra("assessmentNoteId",assessmentNoteId);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(0, null,this);

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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, DBProvider.ASSESSMENT_NOTES_URI, null,
                DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " + assessmentId, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        assessmentNotesCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        assessmentNotesCursorAdapter.swapCursor(null);
    }

    //Move to AssessmentEditorActivity
    public void editAssessmentBtnAction(View view) {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        intent.putExtra("assessmentId",assessmentId);
        intent.putExtra("courseId",courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    //Delete this course from DB
    public void deleteAssessmentBtnAction(View view) {
        //Display confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this assessment?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, remove assessment from DB then return to AssessmentListActivity
                DBDataManager.deleteAssessment(AssessmentDetailActivity.this,assessmentId);
                Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentListActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
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

    public void adAddAssessmentNoteBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentNotesEditorActivity.class);
        intent.putExtra("assessmentId",assessmentId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void adViewAssessmentAlertsBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentAlertsListActivity.class);
        intent.putExtra("assessmentId",assessmentId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void adAssessmentListBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentListActivity.class);
        intent.putExtra("assessmentId",assessmentId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void adSendAssessmentNotesBtnAction(View view) {
        Intent intent = new Intent(this,SendEmailActivity.class);
        intent.putExtra("assessmentId",assessmentId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        intent.putExtra("flag",1);
        startActivity(intent);
    }
}
