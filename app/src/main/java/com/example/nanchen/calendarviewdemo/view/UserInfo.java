package com.example.nanchen.calendarviewdemo.view;

public class UserInfo {
    private long rowid;
    private String text;
    private String year;
    private int month;
    private int day;
    private String name;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "rowid=" + rowid +
                ", text='" + text + '\'' +
                ", year='" + year + '\'' +
                ", month=" + month +
                ", day=" + day +
                ", name='" + name + '\'' +
                '}';
    }
}
