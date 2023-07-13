package com.example.wgutermassistant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AssessmentListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Initialize variables
    private CursorAdapter assessmentCursorAdapter;
    private DBProvider db = new DBProvider();
    long termId;
    long courseId;
    TextView courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);

        //Get Term ID from intent
        Intent intent = getIntent();
        termId = intent.getLongExtra("termId",-1);
        courseId = intent.getLongExtra("courseId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cursor cursor = this.getContentResolver().query(DBProvider.ASSESSMENTS_URI,DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENT_COURSE_ID + " = " + courseId,null,null);

        String[] from = {DBOpenHelper.ASSESSMENT_NAME, DBOpenHelper.ASSESSMENT_TIME, DBOpenHelper.ASSESSMENT_STATUS};
        int[] to = {R.id.lvAssessmentName, R.id.lvAssessmentDueDate, R.id.lvAssessmentStatus};

        assessmentCursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_list_item, cursor, from, to);

        ListView lv = findViewById(R.id.assessmentListView);
        lv.setAdapter(assessmentCursorAdapter);

        //Populate course name
        courseName = findViewById(R.id.assessmentCourseName);
        Course course = DBDataManager.getCourse(this,courseId);
        courseName.setText(course.getName() + " Assessments");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long assessmentId) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("assessmentId",assessmentId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("termId",termId);
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
        return new CursorLoader(this, DBProvider.ASSESSMENTS_URI, null, DBOpenHelper.ASSESSMENT_COURSE_ID + " = " + courseId, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        assessmentCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        assessmentCursorAdapter.swapCursor(null);
    }

    //Allow user to go to AssessmentEditorActivity on button click
    public void addNewAssessmentBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentEditorActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        startActivity(intent);
    }
}
