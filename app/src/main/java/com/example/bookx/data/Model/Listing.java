package com.example.bookx.data.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Data class that captures user information for listings
 * not sure what im doing with this yet
 */
@IgnoreExtraProperties
public class Listing {
    private String uid;
    private String name;
    private String isbn; // empty string if notes and not textbook
    private String className;
    private int price;
    private String description;

    public Listing() {
        // Default constructor required for calls to DataSnapshot.getValue(Listing.class)
    }

    public Listing(String uid, String name, String isbn, String className, int price, String description) {
        this.uid = uid;
        this.name = name;
        this.isbn = isbn;
        this.className = className;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


