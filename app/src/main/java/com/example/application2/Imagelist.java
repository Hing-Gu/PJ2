package com.example.application2;

import android.os.Parcel;
import android.os.Parcelable;

public class Imagelist implements Parcelable {

    public int imageId;
    public String imageUri;

    public Imagelist(int imageId, String imageUri){
        this.imageId = imageId;
        this.imageUri = imageUri;
    }

    public Imagelist(Parcel in){
        imageId = in.readInt();
        imageUri = in.readString();
    }

    public static final Creator<Imagelist> CREATOR = new Creator<Imagelist>() {
        @Override
        public Imagelist createFromParcel(Parcel in) {
            return new Imagelist(in);
        }

        @Override
        public Imagelist[] newArray(int size) {
            return new Imagelist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(imageUri);
    }
}