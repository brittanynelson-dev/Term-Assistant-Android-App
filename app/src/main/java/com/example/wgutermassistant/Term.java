package com.example.wgutermassistant;

public class Term {
    //Initialize variables for term
    private long termId;
    private String title;
    private String startDate;
    private String endDate;
    private int current;

    //Create constructor and initialize variables
    public Term() {
        this.title = "";
        this.startDate = "";
        this.endDate = "";
        this.current = 0;
    }

    //Create getters and setters for variables
    //termId
    public long getTermId() {
        return termId;
    }
    public void setTermId(long id) {
        this.termId = id;
    }

    //title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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

    //current
    public int getCurrent() {
        return current;
    }
    public void setCurrent(int current) {
        this.current = current;
    }
}
