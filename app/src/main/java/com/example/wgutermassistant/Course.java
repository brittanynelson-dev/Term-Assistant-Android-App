package com.example.wgutermassistant;

public class Course {

    //Initialize variables for course
    private long courseId;
    private long courseTermId;
    private String name;
    private String startDate;
    private String endDate;
    private String status;
    private String mentorName;
    private String mentorPhone;
    private String mentorEmail;
    private int courseAlerts;

    //Create constructor and initialize variables
    public Course() {
        this.name = "";
        this.startDate = "";
        this.endDate = "";
        this.status = "";
        this.mentorName = "";
        this.mentorPhone = "";
        this.mentorEmail = "";
        this.courseAlerts = 0;
    }

    //Create getters and setters for variables
    //courseId
    public long getCourseId() {
        return courseId;
    }
    public void setCourseId(long id) {
        this.courseId = id;
    }

    //courseTermId
    public long getCourseTermId() {
        return courseTermId;
    }
    public void setCourseTermId(long id) {
        this.courseTermId = id;
    }

    //name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //startDate
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    //endDate
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    //status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    //mentorName
    public String getMentorName() {
        return mentorName;
    }
    public void setMentorName(String name) {
        this.mentorName = name;
    }

    //mentorPhone
    public String getMentorPhone() {
        return mentorPhone;
    }
    public void setMentorPhone(String phone) {
        this.mentorPhone = phone;
    }

    //mentorEmail
    public String getMentorEmail() {
        return mentorEmail;
    }
    public void setMentorEmail(String email) {
        this.mentorEmail = email;
    }

    //courseAlerts
    public int getCourseAlerts() {
        return courseAlerts;
    }
    public void setCourseAlerts(int alerts) {
        this.courseAlerts = alerts;
    }
}
