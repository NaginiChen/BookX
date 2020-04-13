package com.example.bookx.data.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class that captures user information for logged in users
 */
@IgnoreExtraProperties
public class User {
    private String email;
    private String fullName;
    private String location;
    private boolean showLocation;

    // this stores the user's listings where the key is the lid and the boolean is whether the listing is active
    private Map<String, Boolean> listings;
    // profile_picture?

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String location) {
        this.email = email;
        this.fullName = name;
        this.location = location;
        this.showLocation = false; //user location off by default
        this.listings = new HashMap<String, Boolean>(); // empty by default
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public String getName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getLocation() {
        return this.location;
    }

    public boolean getShowLocation() {
        return this.showLocation;
    }

    public Map<String, Boolean> getListings() {
        return listings;
    }

    public void setListings(Map<String, Boolean> listings) {
        this.listings = listings;
    }
}