package com.csefyp2016.gib3.ustsocialapp;


import android.os.Build;

public class Request {
    private String friendId;
    private String friendName;

    private Request() {}

    public String getFriendId() {
        return friendId;
    }

    public String getFriendName() {return friendName;}

    public static class Builder {
        private String friendId;
        private String friendName;

        public Builder() {}

        public Builder requestId(String id) {
            friendId = id;
            return this;
        }

        public Builder requestName (String name) {
            friendName = name + " has sent a friend request to you!";
            return this;
        }

        public Request build() {
            Request request = new Request();
            request.friendId = friendId;
            request.friendName = friendName;
            return request;
        }
    }
}
