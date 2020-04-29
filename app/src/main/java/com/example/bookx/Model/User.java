package com.example.bookx.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// class model for users
@IgnoreExtraProperties
public class User implements Parcelable {
    private String email;
    private String fullName;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imageurl ;
    private boolean showLocation;

    // this stores the user's listings where the key is the lid and the boolean is whether the listing is active
    private Map<String, Object> listings;

    // Parcelable methods
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String email, String name, String location, String imageurl) {
        this.email = email;
        this.fullName = name;
        this.location = location;
        this.showLocation = true; //user location off by default
        this.imageurl = imageurl ;
        this.listings = new HashMap<>(); // empty by default
    }

    // read from Parcel
    protected User(Parcel in) {
        email = in.readString();
        fullName = in.readString();
        location = in.readString();
        latitude = in.readDouble() ;
        longitude = in.readDouble() ;
        imageurl = in.readString() ;
        showLocation = in.readByte() != 0;
    }

    // write model class to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(fullName);
        dest.writeString(location);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imageurl);
        dest.writeByte((byte) (showLocation ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // setters & getters
    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public String getFullName() {
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

    public Map<String, Object> getListings() {
        return listings;
    }

    public void setListings(Map<String, Object> listings) {
        this.listings = listings;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}