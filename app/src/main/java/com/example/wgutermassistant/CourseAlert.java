package com.example.wgutermassistant;

public class CourseAlert {
    //Initialize variables for assessment alert
    private long courseAlertId;
    private long courseId;
    private String timeStr;
    private String msgStr;

    public CourseAlert() {
        this.timeStr = "";
    }

    //Create getters and setters for variables
    //courseAlertId
    public long getCourseAlertId() {
        return courseAlertId;
    }
    public void setCourseAlertId(long id) {
        this.courseAlertId = id;
    }

    //courseId
    public long getCourseId() {
        return courseId;
    }
    public void setCourseId(long id) {
        this.courseId = id;
    }

    //timeStr
    public String getTimeStr() {
        return timeStr;
    }
    public void setTimeStr(String str) {
        this.timeStr = str;
    }

    //msgStr
    public String getMsgStr() {
        return msgStr;
    }
    public void setMsgStr(String msgStr) {
        this.msgStr = msgStr;
    }
}
