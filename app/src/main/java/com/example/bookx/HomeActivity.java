package com.example.bookx;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookx.Model.Listing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "***HOME***";

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    List<Listing> listings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Log.d(TAG, "HEYYYYYYYY");

        // Initialize firebase auth and database refernce
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("listings");

        // Initialize views

        getListingsData();
    }

    // This method gets listings data from the database and listens to changes
    private void getListingsData() {
        // listen for changes for user data
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Listing listing = postSnapshot.getValue(Listing.class);

                    if (listing != null) {
                        Log.d(TAG, listing.getName());
                        listings.add(listing);
                    }
                }

                // TODO: UPDATE UI HERE
            }

            @Override
            // This method is called when we fail to get user from the database
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadListings:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load listings.",
                        Toast.LENGTH_LONG).show();
            }
        };
        mDatabase.addValueEventListener(userListener); // attach listener to our user database reference
    }
}