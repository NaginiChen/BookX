package com.example.bookx.Model;

public class Post {

    private String bookTitle ;
    private String seller ;
    private String course ;
    private double price ;
    private String desc ;
    private boolean isSold ;

    public Post(String bookTitle, String seller, String course, double price, String desc, boolean isSold){
        this.bookTitle = bookTitle ;
        this.seller = seller ;
        this.course = course ;
        this.price = price ;
        this.desc = desc ;
        this.isSold = isSold ;
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
}