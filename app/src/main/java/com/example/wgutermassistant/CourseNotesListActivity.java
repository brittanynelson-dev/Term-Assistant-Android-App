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

public class CourseNotesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Initialize variables
    private CursorAdapter courseNotesCursorAdapter;
    private DBProvider db = new DBProvider();
    long courseId;
    long termId;
    TextView courseNotesCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_notes_list);

        //Get Course and Term IDs from intent
        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId",-1);
        termId = intent.getLongExtra("termId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cursor cursor = this.getContentResolver().query(DBProvider.COURSE_NOTES_URI,DBOpenHelper.COURSE_NOTES_COLUMNS,
                DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,null,null);

        String[] from = {DBOpenHelper.COURSE_NOTE_TEXT};
        int[] to = {R.id.lvNoteText};

        courseNotesCursorAdapter = new SimpleCursorAdapter(this, R.layout.single_list_item, cursor, from, to);

        ListView lv = findViewById(R.id.courseNotesListView);
        lv.setAdapter(courseNotesCursorAdapter);

        //Populate course name
        courseNotesCourseName = findViewById(R.id.courseNotesCourseName);
        Course course = DBDataManager.getCourse(this,courseId);
        courseNotesCourseName.setText(course.getName() + " Notes");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long courseNoteId) {
                Intent intent = new Intent(CourseNotesListActivity.this, CourseNotesEditorActivity.class);
                intent.putExtra("termId",termId);
                intent.putExtra("courseId",courseId);
                intent.putExtra("courseNoteId",courseNoteId);
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
        return new CursorLoader(this, DBProvider.COURSE_NOTES_URI, null, DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        courseNotesCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        courseNotesCursorAdapter.swapCursor(null);
    }

    //Allow user to go to CourseNotesEditorActivity on button click
    public void addCourseNoteBtnAction(View view) {
        Intent intent = new Intent(this,CourseNotesEditorActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void sendCourseNotesBtnAction(View view) {
        Intent intent = new Intent(this,SendEmailActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("termId",termId);
        intent.putExtra("flag",0);
        startActivity(intent);
    }
}