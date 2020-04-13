package com.example.bookx;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookx.data.Model.Listing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ListingsActivity extends AppCompatActivity {
    private static final String TAG = "***LISTINGS***";

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize firebase auth and datebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // TODO: CREATE LISTINGS UI
    }

    // dummy method to create a listing
    // we will have to pull information from the UI
    private void createListing() {
        // for now just have random information
        String uid = mAuth.getUid(); // user id of the current user who is creaitng the listing
        String name = "Android App Development";
        String isbn = "9781284092134";
        String className = "CS591";
        int price = 50;
        String description = "Textbook for CS591 mobile app development class. Perfect condition. " +
                "Accepting exchanges for other cs class textbooks!";

        createNewListing(uid, name, isbn, className, price, description);
    }

    // this method creates a new listing and adds it to the database
    private void createNewListing(String uid, String name, String isbn, String className, int price, String description) {
        String lid = mDatabase.child("listings").push().getKey();   // generates a new unique id to be the listing id
        Listing listing = new Listing(uid, name, isbn, className, price, description);

        // add listing to the listings table
        mDatabase.child("listings").child(lid).setValue(listing);

        // update the user listings to include the new listing
        Map<String, Object> map = new HashMap<>();
        map.put(lid, true);
        mDatabase.child("users").child(uid).child("listings").updateChildren(map);

        Log.d(TAG, "created listing");
    }
}
