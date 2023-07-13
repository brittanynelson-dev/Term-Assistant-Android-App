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
import android.widget.TextView;

public class TermDetailActivity extends AppCompatActivity {
    //Initialize variable to hold term ID
    long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId",-1);

        //Call method to pull term using id
        Term term = DBDataManager.getTerm(this,termId);

        //Get TextView objects from activity
        TextView termName = findViewById(R.id.termStartDate);
        TextView startDate = findViewById(R.id.termStart);
        TextView endDate = findViewById(R.id.termEnd);

        //Populate TextViews with term data
        termName.setText(term.getTitle());
        startDate.setText("Start Date:  " + term.getStartDate());
        endDate.setText("End Date:    " + term.getEndDate());
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

    public void viewCoursesBtnAction(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void editTermBtnAction(View view) {
        Intent intent = new Intent(this, TermEditorActivity.class);
        intent.putExtra("termId",termId);
        startActivity(intent);
    }

    public void deleteTermBtnAction(View view) {
        //Display an AlertDialog to confirm the user really wants to delete the term
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this term?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User confirmed delete, verify there are no courses assigned to this term

                System.out.println("TermDetail sending term ID: " + termId);

                boolean hasCourses = DBDataManager.termHasCourses(TermDetailActivity.this,termId);
                if (hasCourses) {
                    //Display an error message stating the course cannot be deleted
                    AlertDialog.Builder builder = new AlertDialog.Builder(TermDetailActivity.this);
                    builder.setMessage("You cannot delete a term with courses assigned to it. Please delete or reassign courses first");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    AlertDialog courseWarning = builder.create();
                    courseWarning.show();
                } else {
                    if (DBDataManager.getActiveTermID(TermDetailActivity.this)==termId) {
                        //Display an error message stating the course cannot be deleted
                        AlertDialog.Builder builder = new AlertDialog.Builder(TermDetailActivity.this);
                        builder.setMessage("You cannot delete the active term. Please assign a different active term first");
                        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                        AlertDialog activeWarning = builder.create();
                        activeWarning.show();
                    } else {
                        //No courses assigned and not active course, proceed with delete and return to Term List screen
                        DBDataManager.deleteTerm(TermDetailActivity.this,termId);
                        Intent intent = new Intent(TermDetailActivity.this,TermListActivity.class);
                        startActivity(intent);
                    }
                }
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

    public void termListBtnAction(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivity(intent);
    }
}
