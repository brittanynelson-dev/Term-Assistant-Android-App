package com.example.wgutermassistant;

public class CourseNote {

    //Initialize variables for course
    private long courseNoteId;
    private long courseId;
    private String textStr;

    public CourseNote() {
        this.textStr = "";
    }

    //Create getters and setters for variables
    //courseNoteId
    public long getCourseNoteId() {
        return courseNoteId;
    }
    public void setCourseNoteId(long id) {
        this.courseNoteId = id;
    }

    //courseId
    public long getCourseId() {
        return courseId;
    }
    public void setCourseId(long id) {
        this.courseId = id;
    }

    //text
    public String getTextStr() {
        return textStr;
    }
    public void setTextStr(String str) {
        this.textStr = str;
    }
}
