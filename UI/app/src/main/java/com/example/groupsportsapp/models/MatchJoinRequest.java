package com.example.groupsportsapp.models;

public class MatchJoinRequest {
    private int user_id;
    private int match_id;

    public MatchJoinRequest(int user_id, int match_id) {
        this.user_id = user_id;
        this.match_id = match_id;
    }
}
