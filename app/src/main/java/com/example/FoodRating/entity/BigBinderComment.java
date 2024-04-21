package com.example.FoodRating.entity;

import android.os.Binder;

import java.util.ArrayList;

public class BigBinderComment extends Binder {
    private ArrayList<Comment> data;

    public BigBinderComment(ArrayList<Comment> data) {
        this.data = data;
    }

    public ArrayList<Comment> getData() {
        return data;
    }
}
