package com.goldmedal.hrapp.data;

public class CalendarObj {
    // global variables of the class
    int year;
    int month;
    String status;
    int days;


    public CalendarObj() {

    }

    // constructor has type of data that is required
    public CalendarObj(int year,
                       int month,
                       String status,
                       int days
    ) {

        this.year = year;
        this.month = month;
        this.status = status;
        this.days = days;

    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

}