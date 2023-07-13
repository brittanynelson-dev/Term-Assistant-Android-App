package com.example.wgutermassistant;

public class Assessment {
    //Initialize variables for assessment
    private long assessmentId;
    private long courseId;
    private String assessmentName;
    private String assessmentDueDate;
    private String assessmentStatus;

    //Create constructor and initialize variables
    public Assessment() {
        this.assessmentName = "";
        this.assessmentDueDate = "";
        this.assessmentStatus = "";
    }

    //Create getters and setters for variables
    //assessmentId
    public long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    //courseId
    public long getCourseId() {
        return courseId;
    }
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    //assessmentName
    public String getAssessmentName() {
        return assessmentName;
    }
    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    //assessmentDueDate
    public String getAssessmentDueDate() {
        return assessmentDueDate;
    }
    public void setAssessmentDueDate(String assessmentDueDate) {
        this.assessmentDueDate = assessmentDueDate;
    }

    //assessmentStatus
    public String getAssessmentStatus() {
        return assessmentStatus;
    }
    public void setAssessmentStatus(String assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }
}
