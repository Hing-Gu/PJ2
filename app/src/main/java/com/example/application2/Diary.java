package com.example.application2;

import android.os.Parcel;
import android.os.Parcelable;

public class Diary implements Parcelable {
    private String day;
    private String text;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Diary(String day, String text){
        this.day = day;
        this.text = text;
    }

    public Diary(Parcel in){
        day = in.readString();
        text = in.readString();
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(day);
        dest.writeString(text);
    }
}
