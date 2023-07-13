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

public class AssessmentAlertsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Initialize variables
    private CursorAdapter assessmentAlertCursorAdapter;
    private DBProvider db = new DBProvider();
    long termId;
    long courseId;
    long assessmentId;
    TextView assessmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_alerts_list);

        //Get Term ID from intent
        Intent intent = getIntent();
        termId = intent.getLongExtra("termId",-1);
        courseId = intent.getLongExtra("courseId",-1);
        assessmentId = intent.getLongExtra("assessmentId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cursor cursor = this.getContentResolver().query(DBProvider.ASSESSMENT_ALERTS_URI,DBOpenHelper.ASSESSMENT_ALERT_COLUMNS,
                DBOpenHelper.ASSESSMENT_ALERT_ASSESSMENT_ID + " = " + assessmentId,null,null);

        String[] from = {DBOpenHelper.ASSESSMENT_ALERT_TIME, DBOpenHelper.ASSESSMENT_ALERT_MSG};
        int[] to = {R.id.lvTime,R.id.lvMsg};

        assessmentAlertCursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_alert_list_item, cursor, from, to);

        ListView lv = findViewById(R.id.assessmentAlertsListView);
        lv.setAdapter(assessmentAlertCursorAdapter);

        //Populate assessment name
        assessmentName = findViewById(R.id.aaAssessmentName);
        Assessment assessment = DBDataManager.getAssessment(this, assessmentId);
        assessmentName.setText(assessment.getAssessmentName() + " Alerts");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long assessmentAlertId) {
                Intent intent = new Intent(AssessmentAlertsListActivity.this, AssessmentAlertEditorActivity.class);
                intent.putExtra("assessmentAlertId",assessmentAlertId);
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
        return new CursorLoader(this, DBProvider.ASSESSMENT_ALERTS_URI, null,
                DBOpenHelper.ASSESSMENT_ALERT_ASSESSMENT_ID + " = " + assessmentId, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        assessmentAlertCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        assessmentAlertCursorAdapter.swapCursor(null);
    }

    //Allow user to go to AssessmentEditorActivity on button click
    public void addAssessmentAlertBtnAction(View view) {
        Intent intent = new Intent(this,AssessmentAlertEditorActivity.class);
        intent.putExtra("termId",termId);
        intent.putExtra("courseId",courseId);
        intent.putExtra("assessmentId",assessmentId);
        startActivity(intent);
    }
}
