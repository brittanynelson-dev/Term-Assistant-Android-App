package com.example.wgutermassistant;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class DBDataManager {

    //Verify there is an active term and retrieve its ID
    public static long getActiveTermID(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.TERMS_URI, null, DBOpenHelper.TERM_CURRENT
                + " =1", null, null);
        long i = -1;
        if (cursor.getCount()==0) {
            return i;
        } else {
            try {
                cursor.moveToFirst();
                i = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
                cursor.close();
                return i;
            } catch (Exception ex) {
                ex.printStackTrace();
                return i;
            }
        }
    }

    //Insert a new term in the DB
    public static void insertTerm(Context context, String title, String start, String end, int current) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, title);
        values.put(DBOpenHelper.TERM_START, start);
        values.put(DBOpenHelper.TERM_END, end);
        values.put(DBOpenHelper.TERM_CURRENT, current);
        context.getContentResolver().insert(DBProvider.TERMS_URI, values);
    }

    //Update an existing term in the DB
    public static void updateTerm(Context context, String title, String start, String end, int current, long id) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, title);
        values.put(DBOpenHelper.TERM_START, start);
        values.put(DBOpenHelper.TERM_END, end);
        values.put(DBOpenHelper.TERM_CURRENT, current);
        context.getContentResolver().update(DBProvider.TERMS_URI, values, DBOpenHelper.TERM_ID
                + " = " + id, null);
    }

    //Delete a term from the DB
    public static void deleteTerm(Context context, long id) {
        context.getContentResolver().delete(DBProvider.TERMS_URI, DBOpenHelper.TERM_ID + " = " + id, null);
    }

    //Pull info for a specific term from the DB
    public static Term getTerm(Context context, long termId) {
        Cursor cursor = context.getContentResolver().query(DBProvider.TERMS_URI,DBOpenHelper.TERMS_COLUMNS,
                DBOpenHelper.TERM_ID + " = " + termId,null,null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
        String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        int current = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_CURRENT));
        cursor.close();

        //Create a new Term object and populate it from the DB
        Term term = new Term();
        term.setTermId(termId);
        term.setTitle(title);
        term.setStartDate(startDate);
        term.setEndDate(endDate);
        term.setCurrent(current);

        return term;
    }

    //Pull the highest term ID in DB (last added term)
    public static long getLastAddedTermId(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.TERMS_URI,DBOpenHelper.TERMS_COLUMNS,
                null,null,DBOpenHelper.TERM_CREATE_DATE + " DESC");
        cursor.moveToFirst();
        long termId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
        System.out.println("getLastAddedTermId found ID: " + termId);
        cursor.close();
        return termId;
    }

    //Deactivate existing current term
    public static void deactivateTerm(Context context, long id) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_CURRENT, 0);
        context.getContentResolver().update(DBProvider.TERMS_URI, values, DBOpenHelper.TERM_ID
                + " = " + id, null);
    }

    //Check to see if term has any courses assigned to it
    public static boolean termHasCourses(Context context, long termId) {
        //Initialize a boolean to be returned
        boolean hasCourses = false;
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSES_URI,null,
                DBOpenHelper.COURSE_TERM_ID + " = " + termId,null,null);

        if (cursor.getCount()==0) {
            return hasCourses;
        } else {
            hasCourses = true;
        }
        cursor.close();
        return hasCourses;
    }

    //Build and return an ArrayList containing all Terms
    public static ArrayList<Term> getAllTerms(Context context) {
        //Initialize ArrayList
        ArrayList<Term> termsArr = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DBProvider.TERMS_URI,null, null,null,null);
        cursor.moveToFirst();
        //Iterate through all found terms and add them to ArrayList
        while(!cursor.isAfterLast()){
            long termId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
            String title = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
            int current = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_CURRENT));

            //Create a new Term object and populate it from the DB
            Term term = new Term();
            term.setTermId(termId);
            term.setTitle(title);
            term.setStartDate(startDate);
            term.setEndDate(endDate);
            term.setCurrent(current);

            termsArr.add(term);
            cursor.moveToNext();
        }
        cursor.close();
        return termsArr;
    }

    //Insert a new course in the DB
    public static void insertCourse(Context context, long termId, String name, String start, String end, String status,
                                    String mentorName, String mentorPhone, String mentorEmail, int alerts) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        values.put(DBOpenHelper.COURSE_NAME, name);
        values.put(DBOpenHelper.COURSE_START, start);
        values.put(DBOpenHelper.COURSE_END, end);
        values.put(DBOpenHelper.COURSE_STATUS, status);
        values.put(DBOpenHelper.COURSE_MENTOR_NAME, mentorName);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, mentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, mentorEmail);
        values.put(DBOpenHelper.COURSE_ALERTS, alerts);
        context.getContentResolver().insert(DBProvider.COURSES_URI, values);
    }

    //Update an existing course in the DB
    public static void updateCourse(Context context, long courseId, long termId, String name, String start, String end,
                                    String status, String mentorName, String mentorPhone, String mentorEmail, int alerts) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        values.put(DBOpenHelper.COURSE_NAME, name);
        values.put(DBOpenHelper.COURSE_START, start);
        values.put(DBOpenHelper.COURSE_END, end);
        values.put(DBOpenHelper.COURSE_STATUS, status);
        values.put(DBOpenHelper.COURSE_MENTOR_NAME, mentorName);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, mentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, mentorEmail);
        values.put(DBOpenHelper.COURSE_ALERTS, alerts);
        context.getContentResolver().update(DBProvider.COURSES_URI, values, DBOpenHelper.COURSES_ID
                + " = " + courseId, null);
    }

    //Update an existing course in the DB
    public static void updateCourseTerm(Context context, long courseId, long termId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        context.getContentResolver().update(DBProvider.COURSES_URI, values, DBOpenHelper.COURSES_ID
                + " = " + courseId, null);
    }

    //Delete a course from the DB
    public static void deleteCourse(Context context, long id) {
        context.getContentResolver().delete(DBProvider.COURSES_URI, DBOpenHelper.COURSES_ID + " = " + id, null);
    }

    //Pull info for a specific course from the DB
    public static Course getCourse(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSES_URI,DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSES_ID + " = " + id,null,null);
        cursor.moveToFirst();
        long termId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID));
        String name = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
        String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
        String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        String status = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
        String mentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_NAME));
        String mentorPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_PHONE));
        String mentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_EMAIL));
        int courseAlerts = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ALERTS));
        cursor.close();

        //Create a new Course object and populate it from the DB
        Course course = new Course();
        course.setCourseId(id);
        course.setCourseTermId(termId);
        course.setName(name);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setStatus(status);
        course.setMentorName(mentorName);
        course.setMentorPhone(mentorPhone);
        course.setMentorEmail(mentorEmail);
        course.setCourseAlerts(courseAlerts);

        return course;
    }

    //Pull the highest course ID in DB (last added course)
    public static long getLastAddedCourseId(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSES_URI,DBOpenHelper.COURSES_COLUMNS,
                null,null,DBOpenHelper.COURSE_CREATE_DATE + " DESC");
        cursor.moveToFirst();
        long courseId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSES_ID));
        cursor.close();
        return courseId;
    }

    //Get the number of courses with a given status
    public static int getCoursesByStatus(Context context, String status) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSES_URI,DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_STATUS + " = " + status,null,null);
        //cursor.moveToFirst();
        int numCourses = cursor.getCount();
        cursor.close();
        return numCourses;
    }

    //Get all current and future courses based on their end date
    public static ArrayList<Course> getfutureCourses(Context context) {
        //Initialize ArrayList
        ArrayList<Course> coursesArr = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSES_URI,DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_END + " >= date('now')",null,  "date(" +DBOpenHelper.COURSE_END + ") ASC");
        //If nothing is found, return null
        if (cursor.getCount()==0) {
            return null;
        }
        cursor.moveToFirst();
        //Iterate through all found terms and add them to ArrayList
        while (!cursor.isAfterLast()) {
            long courseId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSES_ID));
            long termId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID));
            String name = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
            String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
            String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
            String status = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
            String mentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_NAME));
            String mentorPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_PHONE));
            String mentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_EMAIL));
            int courseAlerts = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ALERTS));

            //Create a new Course object and populate it from the DB
            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseTermId(termId);
            course.setName(name);
            course.setStartDate(startDate);
            course.setEndDate(endDate);
            course.setStatus(status);
            course.setMentorName(mentorName);
            course.setMentorPhone(mentorPhone);
            course.setMentorEmail(mentorEmail);
            course.setCourseAlerts(courseAlerts);

            //Add course to ArrayList and move to next row
            coursesArr.add(course);
            cursor.moveToNext();
        }

        //Close cursor and return
        cursor.close();
        return coursesArr;
    }

    //Automatically create alerts for a course based on its given start or end dates
    public static void insertCourseAlert(Context context, long courseId, String time, String msg) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_ALERT_COURSE_ID, courseId);
        values.put(DBOpenHelper.COURSE_ALERT_TIME, time);
        values.put(DBOpenHelper.COURSE_ALERT_MSG, msg);
        context.getContentResolver().insert(DBProvider.COURSE_ALERTS_URI, values);
    }

    //Delete all alerts associated with a given course
    public static void deleteCourseAlerts(Context context, long courseId) {
        context.getContentResolver().delete(DBProvider.COURSE_ALERTS_URI, DBOpenHelper.COURSE_ALERT_COURSE_ID + " = " + courseId, null);
    }

    //Pull the highest assessment alert ID in DB (last added assessment alert)
    public static long getLastAddedCourseAlertId(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSE_ALERTS_URI, DBOpenHelper.COURSE_ALERTS_COLUMNS,
                null, null, DBOpenHelper.COURSE_ALERT_CREATE_DATE + " DESC");
        cursor.moveToFirst();
        long courseAlertId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ALERTS_ID));
        cursor.close();
        return courseAlertId;
    }

    //Pull info for a specific course alert from the DB
    public static CourseAlert getCourseAlert(Context context, long courseId, String msg) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSE_ALERTS_URI,DBOpenHelper.COURSE_ALERTS_COLUMNS,
                DBOpenHelper.COURSE_ALERT_COURSE_ID + " = " + courseId + " AND " + DBOpenHelper.COURSE_ALERT_MSG +
                        " = " + msg,null,null);
        //Nulls are possible, confirm we have a valid alert first, if not return null
        if (cursor.getCount()==0) {
            return null;
        }
        cursor.moveToFirst();
        long courseAlertId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ALERTS_ID));
        String time = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_ALERT_TIME));
        cursor.close();

        //Create a new CourseAlert object and populate it from the DB
        CourseAlert courseAlert = new CourseAlert();
        courseAlert.setCourseAlertId(courseAlertId);
        courseAlert.setCourseId(courseId);
        courseAlert.setTimeStr(time);
        courseAlert.setMsgStr(msg);

        return courseAlert;
    }

    //Insert a new course note in the DB
    public static void insertCourseNote(Context context, long courseId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, text);
        context.getContentResolver().insert(DBProvider.COURSE_NOTES_URI, values);
    }

    //Update an existing course note in the DB
    public static void updateCourseNote(Context context, long courseNoteId, long courseId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, text);
        context.getContentResolver().update(DBProvider.COURSE_NOTES_URI, values, DBOpenHelper.COURSE_NOTES_ID
                + " = " + courseNoteId, null);
    }

    //Delete a course note from the DB
    public static void deleteCourseNote(Context context, long id) {
        context.getContentResolver().delete(DBProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_ID + " = " + id, null);
    }

    //Pull info for a specific course note from the DB
    public static CourseNote getCourseNote(Context context, long courseNoteId) {
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSE_NOTES_URI,DBOpenHelper.COURSE_NOTES_COLUMNS,
                DBOpenHelper.COURSE_NOTES_ID + " = " + courseNoteId,null,null);
        cursor.moveToFirst();
        long courseId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_COURSE_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT));
        cursor.close();

        //Create a new CourseNote object and populate it from the DB
        CourseNote courseNote = new CourseNote();
        courseNote.setCourseNoteId(courseNoteId);
        courseNote.setCourseId(courseId);
        courseNote.setTextStr(text);

        return courseNote;
    }

    public static ArrayList<String> getAllCourseNotes(Context context, long courseId) {
        //Initialize ArrayList
        ArrayList<String> courseNotes = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DBProvider.COURSE_NOTES_URI,null,
                DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,null,null);
        //Verify notes were retrieved
        if (cursor.getCount()==0) {
            return courseNotes;
        } else {
            cursor.moveToFirst();
            //Iterate through all found notes and add them to ArrayList
            while(!cursor.isAfterLast()){
                String note = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT));

                courseNotes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
            return courseNotes;
        }
    }

    //Insert a new assessment in the DB
    public static void insertAssessment(Context context, long courseId, String name, String dueDate, String status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DBOpenHelper.ASSESSMENT_NAME, name);
        values.put(DBOpenHelper.ASSESSMENT_TIME, dueDate);
        values.put(DBOpenHelper.ASSESSMENT_STATUS, status);
        context.getContentResolver().insert(DBProvider.ASSESSMENTS_URI, values);
    }

    //Update an existing assessment in the DB
    public static void updateAssessment(Context context, long assessmentId, long courseId, String name, String dueDate, String status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DBOpenHelper.ASSESSMENT_NAME, name);
        values.put(DBOpenHelper.ASSESSMENT_TIME, dueDate);
        values.put(DBOpenHelper.ASSESSMENT_STATUS, status);
        context.getContentResolver().update(DBProvider.ASSESSMENTS_URI, values, DBOpenHelper.ASSESSMENTS_ID
                + " = " + assessmentId, null);
    }

    //Delete an assessment from the DB
    public static void deleteAssessment(Context context, long id) {
        context.getContentResolver().delete(DBProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_ID + " = " + id, null);
    }

    //Pull info for a specific term from the DB
    public static Assessment getAssessment(Context context, long assessmentId) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENTS_URI,DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENTS_ID + " = " + assessmentId,null,null);
        cursor.moveToFirst();
        long courseId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE_ID));
        String name = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME));
        String dueDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
        String status = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_STATUS));
        cursor.close();

        //Create a new Term object and populate it from the DB
        Assessment assessment = new Assessment();
        assessment.setAssessmentId(assessmentId);
        assessment.setCourseId(courseId);
        assessment.setAssessmentName(name);
        assessment.setAssessmentDueDate(dueDate);
        assessment.setAssessmentStatus(status);

        return assessment;
    }

    //Pull the highest assessment ID in DB (last added assessment)
    public static long getLastAddedAssessmentId(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
                null, null, DBOpenHelper.ASSESSMENT_CREATE_DATE + " DESC");
        cursor.moveToFirst();
        long assessmentId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_ID));
        cursor.close();
        return assessmentId;
    }

    //Get the number of assessments with a specified status
    public static int getAssessmentsByStatus(Context context, String status) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENTS_URI,DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENT_STATUS + " = " + status,null,null);
        //cursor.moveToFirst();
        int numAssessments = cursor.getCount();
        cursor.close();
        return numAssessments;
    }

    //Insert a new assessment note in the DB
    public static void insertAssessmentNote(Context context, long assessmentId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_NOTE_TEXT, text);
        context.getContentResolver().insert(DBProvider.ASSESSMENT_NOTES_URI, values);
    }

    //Update an existing assessment note in the DB
    public static void updateAssessmentNote(Context context, long assessmentNoteId, long assessmentId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_NOTE_TEXT, text);
        context.getContentResolver().update(DBProvider.ASSESSMENT_NOTES_URI, values, DBOpenHelper.ASSESSMENT_NOTES_ID
                + " = " + assessmentNoteId, null);
    }

    //Delete an assessment note from the DB
    public static void deleteAssessmentNote(Context context, long id) {
        context.getContentResolver().delete(DBProvider.ASSESSMENT_NOTES_URI, DBOpenHelper.ASSESSMENT_NOTES_ID + " = " + id, null);
    }

    //Pull info for a specific assessment note from the DB
    public static AssessmentNote getAssessmentNote(Context context, long assessmentNoteId) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENT_NOTES_URI,DBOpenHelper.ASSESSMENT_NOTES_COLUMNS,
                DBOpenHelper.ASSESSMENT_NOTES_ID + " = " + assessmentNoteId,null,null);
        cursor.moveToFirst();
        long assessmentId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_TEXT));
        cursor.close();

        //Create a new AssessmentNote object and populate it from the DB
        AssessmentNote assessmentNote = new AssessmentNote();
        assessmentNote.setAssessmentNoteId(assessmentNoteId);
        assessmentNote.setAssessmentId(assessmentId);
        assessmentNote.setTextStr(text);

        return assessmentNote;
    }

    public static ArrayList<String> getAllAssessmentNotes(Context context, long assessmentId) {
        //Initialize ArrayList
        ArrayList<String> assessmentNotes = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENT_NOTES_URI,null,
                DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " + assessmentId,null,null);
        //Verify notes were retrieved
        if (cursor.getCount()==0) {
            return assessmentNotes;
        } else {
            cursor.moveToFirst();
            //Iterate through all found notes and add them to ArrayList
            while(!cursor.isAfterLast()){
                String note = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_TEXT));

                assessmentNotes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
            return assessmentNotes;
        }
    }

    //Insert a new assessment alert in the DB
    public static void insertAssessmentAlert(Context context, long assessmentId, String time, String msg) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_ALERT_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_ALERT_TIME, time);
        values.put(DBOpenHelper.ASSESSMENT_ALERT_MSG, msg);
        context.getContentResolver().insert(DBProvider.ASSESSMENT_ALERTS_URI, values);
    }

    //Update an existing assessment alert in the DB
    public static void updateAssessmentAlert(Context context, long assessmentAlertId, long assessmentId, String time, String msg) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_ALERT_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_ALERT_TIME, time);
        values.put(DBOpenHelper.ASSESSMENT_ALERT_MSG, msg);
        context.getContentResolver().update(DBProvider.ASSESSMENT_ALERTS_URI, values, DBOpenHelper.ASSESSMENT_ALERTS_ID
                + " = " + assessmentAlertId, null);
    }

    //Delete an assessment alert from the DB
    public static void deleteAssessmentAlert(Context context, long id) {
        context.getContentResolver().delete(DBProvider.ASSESSMENT_ALERTS_URI, DBOpenHelper.ASSESSMENT_ALERTS_ID + " = " + id, null);
    }

    //Pull info for a specific assessment alert from the DB
    public static AssessmentAlert getAssessmentAlert(Context context, long assessmentAlertId) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENT_ALERTS_URI,DBOpenHelper.ASSESSMENT_ALERT_COLUMNS,
                DBOpenHelper.ASSESSMENT_ALERTS_ID + " = " + assessmentAlertId,null,null);
        cursor.moveToFirst();
        long assessmentId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERT_ASSESSMENT_ID));
        String time = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERT_TIME));
        String msg = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERT_MSG));
        cursor.close();

        //Create a new AssessmentAlert object and populate it from the DB
        AssessmentAlert assessmentAlert = new AssessmentAlert();
        assessmentAlert.setAssessmentAlertId(assessmentAlertId);
        assessmentAlert.setAssessmentId(assessmentId);
        assessmentAlert.setTimeStr(time);
        assessmentAlert.setMsgStr(msg);

        return assessmentAlert;
    }

    //Pull the highest assessment alert ID in DB (last added assessment alert)
    public static long getLastAddedAssessmentAlertId(Context context) {
        Cursor cursor = context.getContentResolver().query(DBProvider.ASSESSMENT_ALERTS_URI, DBOpenHelper.ASSESSMENT_ALERT_COLUMNS,
                null, null, DBOpenHelper.ASSESSMENT_ALERT_CREATE_DATE + " DESC");
        cursor.moveToFirst();
        long assessmentAlertId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERTS_ID));
        cursor.close();
        return assessmentAlertId;
    }
}
