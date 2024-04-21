package com.example.FoodRating.entity;

public class User {
    private String username;
    private String password;
    private boolean isStudent;

    public User() {
    }

    public User(String username, String password, boolean isStudent) {
        this.username = username;
        this.password = password;
        this.isStudent = isStudent;
    }

    public User(String username, boolean isStudent) {
        this.username = username;
        this.isStudent = isStudent;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStudent() {
        return isStudent;
    }
}
