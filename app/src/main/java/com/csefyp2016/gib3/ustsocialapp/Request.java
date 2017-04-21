package com.csefyp2016.gib3.ustsocialapp;



public class Request {
    private String friendId;

    private Request() {}

    public String getFriendId() {
        return friendId;
    }

    public static class Builder {
        private String friendId;

        public Builder() {}

        public Builder requestId(String id) {
            friendId = id + "has sent a friend request to you!";
            return this;
        }

        public Request build() {
            Request request = new Request();
            request.friendId = friendId;
            return request;
        }
    }
}
