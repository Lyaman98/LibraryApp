package com.demo.dictionary;

import java.util.Date;

public class Words {

    private String german;
    private String english;
    private Date date;
    private boolean added;

    public Words(Date date, String english, String german) {
        this.german = german;
        this.english = english;
        this.date = date;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public String getGerman() {
        return german;
    }

    public void setGerman(String german) {
        this.german = german;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Words{" +
                "german='" + german + '\'' +
                ", english='" + english + '\'' +
                ", isAdded=" + isAdded() +
                '}';
    }
}
