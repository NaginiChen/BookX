package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ListingPage extends AppCompatActivity {
    private static final String TAG = "***LISTINGS***";

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User currUser; // current logged on user

    TextView listing_tv;
    Button post_btn;
    EditText bookname_et;
    EditText isbn_et;
    Button uploadisbn_btn;
    EditText class_et;
    EditText price_et;
    EditText description_et;
    TextView location_tv;
    Button Ylocation_btn;
    Button Nlocation_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_page);

        // Initialize firebase auth and datebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // read user data in order to create new listing
        getUserData();

        listing_tv = (TextView) findViewById(R.id.listing_tv);
        post_btn = (Button) findViewById(R.id.post_btn);
        bookname_et = (EditText) findViewById(R.id.bookname_et);
        isbn_et = (EditText) findViewById(R.id.isbn_et);
        uploadisbn_btn = (Button) findViewById(R.id.uploadisbn_btn);
        class_et = (EditText) findViewById(R.id.class_et);
        price_et = (EditText) findViewById(R.id.price_et);
        description_et = (EditText) findViewById(R.id.description_et);
        location_tv = (TextView) findViewById(R.id.location_tv);
        Ylocation_btn = (Button) findViewById(R.id.Ylocation_btn); // TODO: CHANGE TO A SWITCH INSTEAD
        Nlocation_btn = (Button) findViewById(R.id.Nlocation_btn);



        //when you click post_btn, it will go to Posting page? Not sure what that is
        // for now go to home page so you can view on listings
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createListing()) {
                    openHomePage();
                } else {
                    Toast.makeText(getBaseContext(), "Failed to create new listing. " +
                            "Try again.", Toast.LENGTH_LONG).show();
                }
            }
        }); // TODO: CHANGE TO ANOTHER PAGE
    }

    // Returns true if listing is successfully created
    private boolean createListing() {
        String name = bookname_et.getText().toString();
        String isbn = isbn_et.getText().toString(); // TODO: INTEGRATE ISBN API
        String className = class_et.getText().toString();
        String strPrice = price_et.getText().toString();
        String description = description_et.getText().toString();

        double price;

        try {
            price = Double.parseDouble(strPrice);

            // write listing to database
            if (!writeListing(name, isbn, className, price, description)) {
                return false;
            }

            Toast.makeText(getBaseContext(), "Successfully created " +
                    "listing.", Toast.LENGTH_LONG).show();
            return true;

        } catch (NumberFormatException e) {
            price_et.setError("Invalid price entered.");

            return false;
        }
    }

    // this method creates a new listing and writes it to the database
    private boolean writeListing(String name, String isbn, String className, double price, String description) {
        String lid = mDatabase.child("listings").push().getKey();   // generates a new unique id to be the listing id

        // create listing object and write to database
        try {
            String uid = mAuth.getUid(); // user id of the current user who is creaitng the listing
            String seller = currUser.getFullName();

            Post newListing = new Post(uid, name, seller, className, price, description, false);

            // add listing to the listings table
            mDatabase.child("listings").child(lid).setValue(newListing);

            // update the user listings to include the new listing
            Map<String, Object> map = new HashMap<>();
            map.put(lid, newListing.isSold());
            Log.d(TAG, uid);
            mDatabase.child("users").child(uid).child("listings").updateChildren(map);

            Log.d(TAG, "created listing");
            return true;

        } catch (Exception e) {
            Log.d(TAG, "Failed to create listing: " + e.getMessage());
            return false;
        }
    }

    public void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

    }

    public void openPostingPage() {
        Intent intent = new Intent(this, PostingPage.class);
        startActivity(intent);

    }


    // This method returns the current user's name
    private void getUserData() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                currUser = dataSnapshot.getValue(User.class);
            }

            @Override
            // This method is called when we fail to get user from the database
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load user information.",
                        Toast.LENGTH_LONG).show();
            }
        };
        mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(userListener); // attach listener to our user database reference
    }
}