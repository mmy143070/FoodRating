package com.example.FoodRating.entity;

public class UserDocument {
    public User getUser() {
        return document;
    }

    private User document;

    public UserDocument() {
    }

    public UserDocument(User user) {
        this.document = user;
    }
}
