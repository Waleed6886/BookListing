package com.example.waleed.booklistingapplecation;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

/**
 * Created by Waleed on 05/05/17.
 */

public class Books implements Parcelable {

    public JSONArray author;
    public String title;
    public Bitmap image;

    public Books(JSONArray author, String title,Bitmap img) {
        this.author = author;
        this.title = title;
        this.image = img;
    }

    public JSONArray getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(JSONArray author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBitman(Bitmap bitmap){
        this.image = bitmap;
    }

    public Bitmap getBitmap(){
        return this.image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
