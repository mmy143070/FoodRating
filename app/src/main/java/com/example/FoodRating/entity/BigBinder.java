package com.example.FoodRating.entity;

import android.os.Binder;

public class BigBinder extends Binder {
    private String data;

    public BigBinder(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
