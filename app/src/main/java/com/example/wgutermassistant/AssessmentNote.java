package com.example.wgutermassistant;

public class AssessmentNote {

    //Initialize variables for assessment note
    private long assessmentNoteId;
    private long assessmentId;
    private String textStr;

    public AssessmentNote() {
        this.textStr = "";
    }

    //Create getters and setters for variables
    //assessmentNoteId
    public long getAssessmentNoteId() {
        return assessmentNoteId;
    }
    public void setAssessmentNoteId(long id) {
        this.assessmentNoteId = id;
    }

    //assessmentId
    public long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(long id) {
        this.assessmentId = id;
    }

    //text
    public String getTextStr() {
        return textStr;
    }
    public void setTextStr(String str) {
        this.textStr = str;
    }
}
