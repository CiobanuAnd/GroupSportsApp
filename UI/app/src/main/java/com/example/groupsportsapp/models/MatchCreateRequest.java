package com.example.groupsportsapp.models;

public class MatchCreateRequest {
    private String title;
    private String location;
    private String match_time;
    private int created_by;

    public MatchCreateRequest(String title, String location, String match_time, int created_by) {
        this.title = title;
        this.location = location;
        this.match_time = match_time;
        this.created_by = created_by;
    }
}
