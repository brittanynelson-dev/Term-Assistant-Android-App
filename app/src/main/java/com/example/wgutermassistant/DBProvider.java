package com.example.wgutermassistant;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DBProvider extends ContentProvider{

    // Authority and path strings
    private static final String AUTHORITY = "com.example.wgutermassistant.dbprovider";
    private static final String TERMS_PATH = "terms";
    private static final String COURSES_PATH = "courses";
    private static final String COURSE_ALERTS_PATH = "courseAlerts";
    private static final String COURSE_NOTES_PATH = "courseNotes";
    private static final String ASSESSMENTS_PATH = "assessments";
    private static final String ASSESSMENT_ALERTS_PATH = "assessmentNotifications";
    private static final String ASSESSMENT_NOTES_PATH = "assessmentNotes";

    //URIs
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH);
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH);
    public static final Uri COURSE_ALERTS_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_ALERTS_PATH);
    public static final Uri COURSE_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_NOTES_PATH);
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH);
    public static final Uri ASSESSMENT_ALERTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_ALERTS_PATH);
    public static final Uri ASSESSMENT_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_NOTES_PATH);

    //Constant to identify requested operation
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int COURSE_ALERTS = 5;
    private static final int COURSE_ALERTS_ID = 6;
    private static final int COURSE_NOTES = 7;
    private static final int COURSE_NOTES_ID = 8;
    private static final int ASSESSMENTS = 9;
    private static final int ASSESSMENTS_ID = 10;
    private static final int ASSESSMENT_ALERTS = 11;
    private static final int ASSESSMENT_ALERTS_ID = 12;
    private static final int ASSESSMENT_NOTES = 13;
    private static final int ASSESSMENT_NOTES_ID = 14;

    //URI Matcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, TERMS_PATH + "/#", TERMS_ID);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH + "/#", COURSES_ID);
        uriMatcher.addURI(AUTHORITY, COURSE_ALERTS_PATH, COURSE_ALERTS);
        uriMatcher.addURI(AUTHORITY, COURSE_ALERTS_PATH + "/#", COURSE_ALERTS_ID);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH, COURSE_NOTES);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH + "/#", COURSE_NOTES_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH + "/#", ASSESSMENTS_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_ALERTS_PATH, ASSESSMENT_ALERTS);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_ALERTS_PATH + "/#", ASSESSMENT_ALERTS_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_NOTES_PATH, ASSESSMENT_NOTES);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_NOTES_PATH + "/#", ASSESSMENT_NOTES_ID);
    }

    /*
    //Content type constants
    public static final String TERM_CONTENT_TYPE = "term";
    public static final String COURSE_CONTENT_TYPE = "course";
    public static final String COURSE_ALERT_CONTENT_TYPE = "courseAlert";
    public static final String COURSE_NOTE_CONTENT_TYPE = "courseNote";
    public static final String ASSESSMENT_CONTENT_TYPE = "assessment";
    public static final String ASSESSMENT_NOTIFICATION_CONTENT_TYPE = "assessmentNotification";
    public static final String ASSESSMENT_NOTE_CONTENT_TYPE = "assessmentNote";
    */

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        //Create database for app
        DBOpenHelper helper = new DBOpenHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case TERMS:
                return db.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_COLUMNS, selection, null,
                        null, null, DBOpenHelper.TERM_ID + " DESC");
            case COURSES:
                return db.query(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSES_COLUMNS, selection, null,
                        null, null, DBOpenHelper.COURSES_ID + " DESC");
            case COURSE_ALERTS:
                return db.query(DBOpenHelper.TABLE_COURSE_ALERTS, DBOpenHelper.COURSE_ALERTS_COLUMNS, selection, null,
                        null, null, DBOpenHelper.COURSE_ALERTS_ID + " DESC");
            case COURSE_NOTES:
                return db.query(DBOpenHelper.TABLE_COURSE_NOTES, DBOpenHelper.COURSE_NOTES_COLUMNS, selection, null,
                        null, null, DBOpenHelper.COURSE_NOTES_ID + " DESC");
            case ASSESSMENTS:
                return db.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENTS_COLUMNS, selection, null,
                        null, null, DBOpenHelper.ASSESSMENTS_ID + " DESC");
            case ASSESSMENT_ALERTS:
                return db.query(DBOpenHelper.TABLE_ASSESSMENT_ALERTS, DBOpenHelper.ASSESSMENT_ALERT_COLUMNS, selection, null,
                        null, null, DBOpenHelper.ASSESSMENT_ALERTS_ID + " DESC");
            case ASSESSMENT_NOTES:
                return db.query(DBOpenHelper.TABLE_ASSESSMENT_NOTES, DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, selection, null,
                        null, null, DBOpenHelper.ASSESSMENT_NOTES_ID + " DESC");
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        switch (uriMatcher.match(uri)) {
            case TERMS:
                id = db.insert(DBOpenHelper.TABLE_TERMS, null, values);
                return uri.parse(TERMS_PATH + "/" + id);
            case COURSES:
                id = db.insert(DBOpenHelper.TABLE_COURSES, null, values);
                return uri.parse(COURSES_PATH + "/" + id);
            case COURSE_ALERTS:
                id = db.insert(DBOpenHelper.TABLE_COURSE_ALERTS, null, values);
                return uri.parse(COURSE_ALERTS_PATH + "/" + id);
            case COURSE_NOTES:
                id = db.insert(DBOpenHelper.TABLE_COURSE_NOTES, null, values);
                return uri.parse(COURSE_NOTES_PATH + "/" + id);
            case ASSESSMENTS:
                id = db.insert(DBOpenHelper.TABLE_ASSESSMENTS, null, values);
                return uri.parse(ASSESSMENTS_PATH + "/" + id);
            case ASSESSMENT_ALERTS:
                id = db.insert(DBOpenHelper.TABLE_ASSESSMENT_ALERTS, null, values);
                return uri.parse(ASSESSMENT_ALERTS_PATH + "/" + id);
            case ASSESSMENT_NOTES:
                id = db.insert(DBOpenHelper.TABLE_ASSESSMENT_NOTES, null, values);
                return uri.parse(ASSESSMENT_NOTES_PATH + "/" + id);
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TERMS:
                return db.delete(DBOpenHelper.TABLE_TERMS, selection, selectionArgs);
            case COURSES:
                return db.delete(DBOpenHelper.TABLE_COURSES, selection, selectionArgs);
            case COURSE_ALERTS:
                return db.delete(DBOpenHelper.TABLE_COURSE_ALERTS, selection, selectionArgs);
            case COURSE_NOTES:
                return db.delete(DBOpenHelper.TABLE_COURSE_NOTES, selection, selectionArgs);
            case ASSESSMENTS:
                return db.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
            case ASSESSMENT_ALERTS:
                return db.delete(DBOpenHelper.TABLE_ASSESSMENT_ALERTS, selection, selectionArgs);
            case ASSESSMENT_NOTES:
                return db.delete(DBOpenHelper.TABLE_ASSESSMENT_NOTES, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TERMS:
                return db.update(DBOpenHelper.TABLE_TERMS, values, selection, selectionArgs);
            case COURSES:
                return db.update(DBOpenHelper.TABLE_COURSES, values, selection, selectionArgs);
            case COURSE_ALERTS:
                return db.update(DBOpenHelper.TABLE_COURSE_ALERTS, values, selection, selectionArgs);
            case COURSE_NOTES:
                return db.update(DBOpenHelper.TABLE_COURSE_NOTES, values, selection, selectionArgs);
            case ASSESSMENTS:
                return db.update(DBOpenHelper.TABLE_ASSESSMENTS, values, selection, selectionArgs);
            case ASSESSMENT_ALERTS:
                return db.update(DBOpenHelper.TABLE_ASSESSMENT_ALERTS, values, selection, selectionArgs);
            case ASSESSMENT_NOTES:
                return db.update(DBOpenHelper.TABLE_ASSESSMENT_NOTES, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }
}
