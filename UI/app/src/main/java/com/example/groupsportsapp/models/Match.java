package com.example.groupsportsapp.models;

public class Match {
    private int id;
    private String title;
    private String location;
    private String match_time;
    private int created_by;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getMatchTime() { return match_time; }
    public int getCreatedBy() { return created_by; }
}
