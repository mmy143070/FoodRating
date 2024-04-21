package com.example.FoodRating.entity;

import java.util.List;

public class Dish {
    private String _id;
    private String name;
    private String image;
    private String avr_score;
    private List<Comment> comments;
    private String desc;
    private String staff_name;

    public String getStaffName() {
        return staff_name;
    }

    public void setStaffName(String staffName) {
        this.staff_name = staffName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAvr_score(String avr_score) {
        this.avr_score = avr_score;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getAvr_score() {
        return avr_score;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Dish( String name, String desc, String image, String avr_score, List<Comment> comments,String staff_name) {
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.avr_score = avr_score;
        this.comments = comments;
        this.staff_name = staff_name;
    }
}
