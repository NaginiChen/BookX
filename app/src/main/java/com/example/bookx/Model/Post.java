package com.example.bookx.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Date;

/**
 * Data class that captures user information for logged in users
 */
@IgnoreExtraProperties
public class Post implements Parcelable {

    private String uid;
    private String bookTitle ;
    private String seller ;
    private String course ;
    private double price ;
    private String desc ;
    private boolean isSold ;
    private Date date;
    private String isbn;
    private String imageurl ;
    private double latitude, longitude ;

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Post(String uid, String bookTitle, String seller, String course, double price, String desc, boolean isSold, String isbn, String imageurl, double latitude, double longitude){
        this.uid = uid;
        this.bookTitle = bookTitle ;
        this.seller = seller ;
        this.course = course ;
        this.price = price ;
        this.desc = desc ;
        this.isSold = isSold ;
        this.date = new Date();
        this.isbn = isbn;
        this.imageurl = imageurl ;
        this.latitude = latitude ;
        this.longitude = longitude ;
    }

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(Parcel in){

        this.uid = in.readString() ;
        this.bookTitle = in.readString() ;
        this.seller = in.readString() ;
        this.course = in.readString() ;
        this.price = in.readDouble() ;
        this.desc = in.readString() ;
        this.isSold = Boolean.parseBoolean(in.readString()) ;
        this.date = new Date(in.readLong());
        this.isbn = in.readString();
        this.imageurl = in.readString();
        this.latitude = in.readDouble() ;
        this.longitude = in.readDouble() ;
    }

    // setter & getter



    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(bookTitle);
        dest.writeString(seller);
        dest.writeString(course);
        dest.writeDouble(price);
        dest.writeString(desc);
        dest.writeString(Boolean.toString(isSold));
        dest.writeLong(date.getTime());
        dest.writeString(isbn);
        dest.writeString(imageurl);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);

    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}