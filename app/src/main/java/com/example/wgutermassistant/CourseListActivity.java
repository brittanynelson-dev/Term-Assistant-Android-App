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

public class CourseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Initialize variables
    private CursorAdapter courseCursorAdapter;
    private DBProvider db = new DBProvider();
    long termId;
    TextView termName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        //Get Term ID from intent
        Intent intent = getIntent();
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cursor cursor = this.getContentResolver().query(DBProvider.COURSES_URI,DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_TERM_ID + " = " + termId,null,null);

        String[] from = {DBOpenHelper.COURSE_NAME, DBOpenHelper.COURSE_START, DBOpenHelper.COURSE_END, DBOpenHelper.COURSE_STATUS};
        int[] to = {R.id.lvCourseName, R.id.lvCourseStart, R.id.lvCourseEnd, R.id.lvCourseStatus};

        courseCursorAdapter = new SimpleCursorAdapter(this, R.layout.course_list_item, cursor, from, to);

        ListView lv = findViewById(R.id.courseListView);
        lv.setAdapter(courseCursorAdapter);

        //Populate term name
        termName = findViewById(R.id.courseTermName);
        final Term term = DBDataManager.getTerm(this,termId);
        termName.setText(term.getTitle() + " Courses");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long courseId) {
                Intent intent = new Intent(CourseListActivity.this, CourseDetailActivity.class);
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, DBProvider.COURSES_URI, null, DBOpenHelper.COURSE_TERM_ID + " = " + termId, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        courseCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        courseCursorAdapter.swapCursor(null);
    }

    //Allow user to go to CourseEditorActivity on button click
    public void addNewCourseBtnAction(View view) {
        Intent intent = new Intent(this,CourseEditorActivity.class);
        intent.putExtra("termId", termId);
        startActivity(intent);
    }
}
