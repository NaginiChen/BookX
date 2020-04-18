package com.example.bookx.Model;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Date;

/**
 * Data class that captures user information for logged in users
 */
@IgnoreExtraProperties
public class Post {

    private String bookTitle ;
    private String seller ;
    private String course ;
    private double price ;
    private String desc ;
    private boolean isSold ;
    private Date date;

    public Post(String bookTitle, String seller, String course, double price, String desc, boolean isSold){
        this.bookTitle = bookTitle ;
        this.seller = seller ;
        this.course = course ;
        this.price = price ;
        this.desc = desc ;
        this.isSold = isSold ;
        this.date = new Date();

        // TODO: ADD ISBN
    }

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
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
}