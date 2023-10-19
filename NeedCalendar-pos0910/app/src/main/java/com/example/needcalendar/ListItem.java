package com.example.needcalendar;

public class ListItem {
    private int id;
    private String title;
    private String place;
    private String memo;



    public ListItem(int id, String title, String place, String memo) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.memo = memo;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public String getMemo() {
        return memo;
    }
}
