package com.example.wgutermassistant;

public class AssessmentAlert {
    //Initialize variables for assessment alert
    private long assessmentAlertId;
    private long assessmentId;
    private String timeStr;
    private String msgStr;

    public AssessmentAlert() {
        this.timeStr = "";
    }

    //Create getters and setters for variables
    //assessmentAlertId
    public long getAssessmentAlertId() {
        return assessmentAlertId;
    }
    public void setAssessmentAlertId(long id) {
        this.assessmentAlertId = id;
    }

    //assessmentId
    public long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(long id) {
        this.assessmentId = id;
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
