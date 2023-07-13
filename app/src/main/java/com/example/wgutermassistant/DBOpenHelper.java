package com.example.wgutermassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    //Constants for db name and version
    private static final String DATABASE_NAME = "term_assistant.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for Terms table
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_TITLE = "termTitle";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_CURRENT = "termCurrent";
    public static final String TERM_CREATE_DATE = "termCreateDate";
    public static final String[] TERMS_COLUMNS = {TERM_ID, TERM_TITLE, TERM_START, TERM_END, TERM_CURRENT,TERM_CREATE_DATE};

    //Constants for Courses table
    public static final String TABLE_COURSES = "courses";
    public static final String COURSES_ID = "_id";
    public static final String COURSE_TERM_ID = "courseTermId";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_MENTOR_NAME = "courseMentorName";
    public static final String COURSE_MENTOR_PHONE = "courseMentorPhone";
    public static final String COURSE_MENTOR_EMAIL = "courseMentorEmail";
    public static final String COURSE_ALERTS = "courseAlerts";
    public static final String COURSE_CREATE_DATE = "courseCreateDate";
    public static final String[] COURSES_COLUMNS = {COURSES_ID, COURSE_TERM_ID, COURSE_NAME,
            COURSE_START, COURSE_END, COURSE_STATUS, COURSE_MENTOR_NAME, COURSE_MENTOR_PHONE, COURSE_MENTOR_EMAIL,
            COURSE_ALERTS, COURSE_CREATE_DATE};

    //Constants for Course Alerts table
    public static final String TABLE_COURSE_ALERTS = "courseAlerts";
    public static final String COURSE_ALERTS_ID = "_id";
    public static final String COURSE_ALERT_COURSE_ID = "courseAlertCourseId";
    public static final String COURSE_ALERT_TIME = "courseAlertTime";
    public static final String COURSE_ALERT_MSG = "courseAlertMsg";
    public static final String COURSE_ALERT_CREATE_DATE = "courseAlertCreateDate";
    public static final String[] COURSE_ALERTS_COLUMNS = {COURSE_ALERTS_ID, COURSE_ALERT_COURSE_ID,
            COURSE_ALERT_TIME, COURSE_ALERT_MSG, COURSE_ALERT_CREATE_DATE};

    //Constants for Course Notes table
    public static final String TABLE_COURSE_NOTES = "courseNotes";
    public static final String COURSE_NOTES_ID = "_id";
    public static final String COURSE_NOTE_COURSE_ID = "courseNoteCourseId";
    public static final String COURSE_NOTE_TEXT = "courseNoteText";
    public static final String COURSE_NOTE_CREATE_DATE = "courseNoteCreateDate";
    public static final String[] COURSE_NOTES_COLUMNS = {COURSE_NOTES_ID, COURSE_NOTE_COURSE_ID, COURSE_NOTE_TEXT,
            COURSE_NOTE_CREATE_DATE};

    //Constants for Assessments table
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENTS_ID = "_id";
    public static final String ASSESSMENT_COURSE_ID = "assessmentCourseId";
    public static final String ASSESSMENT_STATUS = "assessmentStatus";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String ASSESSMENT_TIME = "assessmentTime";
    public static final String ASSESSMENT_CREATE_DATE = "assessmentCreateDate";
    public static final String[] ASSESSMENTS_COLUMNS = {ASSESSMENTS_ID, ASSESSMENT_COURSE_ID, ASSESSMENT_STATUS,
            ASSESSMENT_NAME, ASSESSMENT_TIME, ASSESSMENT_CREATE_DATE};

    //Constants for Assessment Alerts table
    public static final String TABLE_ASSESSMENT_ALERTS = "assessmentAlerts";
    public static final String ASSESSMENT_ALERTS_ID = "_id";
    public static final String ASSESSMENT_ALERT_ASSESSMENT_ID = "assessmentAlertAssessmentId";
    public static final String ASSESSMENT_ALERT_TIME = "assessmentAlertTime";
    public static final String ASSESSMENT_ALERT_MSG = "assessmentAlertMsg";
    public static final String ASSESSMENT_ALERT_CREATE_DATE = "assessmentAlertCreateDate";
    public static final String[] ASSESSMENT_ALERT_COLUMNS = {ASSESSMENT_ALERTS_ID, ASSESSMENT_ALERT_ASSESSMENT_ID,
            ASSESSMENT_ALERT_TIME, ASSESSMENT_ALERT_MSG, ASSESSMENT_ALERT_CREATE_DATE};

    //Constants for Assessment Notes table
    public static final String TABLE_ASSESSMENT_NOTES = "assessmentNotes";
    public static final String ASSESSMENT_NOTES_ID = "_id";
    public static final String ASSESSMENT_NOTE_ASSESSMENT_ID = "assessmentNoteAssessmentId";
    public static final String ASSESSMENT_NOTE_TEXT = "assessmentNoteText";
    public static final String ASSESSMENT_NOTE_CREATE_DATE = "assessmentNoteCreateDate";
    public static final String[] ASSESSMENT_NOTES_COLUMNS = {ASSESSMENT_NOTES_ID, ASSESSMENT_NOTE_ASSESSMENT_ID,
            ASSESSMENT_NOTE_TEXT, ASSESSMENT_NOTE_CREATE_DATE};


    //Create Terms table String
    private static final String CREATE_TERMS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START + " DATE, " +
                    TERM_END + " DATE, " +
                    TERM_CURRENT + " INTEGER, " +
                    TERM_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Courses table String
    private static final String CREATE_COURSES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " (" +
                    COURSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_TERM_ID + " INTEGER, " +
                    COURSE_NAME + " TEXT, " +
                    COURSE_START + " DATE, " +
                    COURSE_END + " DATE, " +
                    COURSE_STATUS + " TEXT, " +
                    COURSE_MENTOR_NAME + " TEXT, " +
                    COURSE_MENTOR_PHONE + " TEXT, " +
                    COURSE_MENTOR_EMAIL + " TEXT, " +
                    COURSE_ALERTS + " INTEGER, " +
                    COURSE_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Course Alerts table String
    private static final String CREATE_COURSE_ALERTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE_ALERTS + " (" +
                    COURSE_ALERTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_ALERT_COURSE_ID + " INTEGER, " +
                    COURSE_ALERT_TIME + " DATETIME, " +
                    COURSE_ALERT_MSG + " TEXT, " +
                    COURSE_ALERT_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Course Notes table String
    private static final String CREATE_COURSE_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE_NOTES + " (" +
                    COURSE_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_NOTE_COURSE_ID + " INTEGER, " +
                    COURSE_NOTE_TEXT + " TEXT, " +
                    COURSE_NOTE_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Assessments table String
    private static final String CREATE_ASSESSMENTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_COURSE_ID + " INTEGER, " +
                    ASSESSMENT_STATUS + " TEXT, " +
                    ASSESSMENT_NAME + " TEXT, " +
                    ASSESSMENT_TIME + " DATETIME, " +
                    ASSESSMENT_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Assessment Alerts table String
    private static final String CREATE_ASSESSMENT_ALERTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENT_ALERTS + " (" +
                    ASSESSMENT_ALERTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_ALERT_ASSESSMENT_ID + " INTEGER, " +
                    ASSESSMENT_ALERT_TIME + " DATETIME, " +
                    ASSESSMENT_ALERT_MSG + " TEXT, " +
                    ASSESSMENT_ALERT_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";

    //Create Assessment Notes table String
    private static final String CREATE_ASSESSMENT_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENT_NOTES + " (" +
                    ASSESSMENT_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_NOTE_ASSESSMENT_ID + " INTEGER, " +
                    ASSESSMENT_NOTE_TEXT + " TEXT, " +
                    ASSESSMENT_NOTE_CREATE_DATE + " DATETIME default CURRENT_TIMESTAMP" +
                    " );";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create all tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TERMS_TABLE);
        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_COURSE_ALERTS_TABLE);
        db.execSQL(CREATE_COURSE_NOTES_TABLE);
        db.execSQL(CREATE_ASSESSMENTS_TABLE);
        db.execSQL(CREATE_ASSESSMENT_ALERTS_TABLE);
        db.execSQL(CREATE_ASSESSMENT_NOTES_TABLE);
    }

    //On DB upgrade, delete all tables and re-create them
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_NOTES);
        onCreate(db);
    }
}
