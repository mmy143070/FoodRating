package com.example.FoodRating.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Comment implements Parcelable {
    private String username;
    private String score;
    private String text;
    private String image;

    protected Comment(Parcel in) {
        username = in.readString();
        score = in.readString();
        text = in.readString();
        image = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            Comment comment = new Comment();
            comment.setUsername(in.readString());
            comment.setScore(in.readString());
            comment.setText(in.readString());
            comment.setImage(in.readString());
            return comment;
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getScore() {
        return score;
    }

    public String getText() {
        return text;
    }
    public String getImage() {
        return image;
    }

    public Comment() {
    }

    public Comment(String username, String score, String text, String image) {
        this.username = username;
        this.score = score;
        this.text = text;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(score);
        parcel.writeString(text);
        parcel.writeString(image);
    }
}
