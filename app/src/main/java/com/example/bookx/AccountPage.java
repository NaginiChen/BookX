package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountPage extends AppCompatActivity {
    private static final String TAG = "***ACCOUNT***";

    TextView name_tv3;
    TextView email_tv3;
    TextView address_tv;
    Button changePW_btn;
    TextView listings_tv;
    Button logout_btn;
    private ListAdapter postAdapter ;
    private ListView lvAccountPosts ;

    // firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        name_tv3 = (TextView) findViewById(R.id.name_tv3);
        email_tv3 = (TextView) findViewById(R.id.email_tv3);
        address_tv = (TextView) findViewById(R.id.address_tv);
        changePW_btn = (Button) findViewById(R.id.changePW_btn);
        listings_tv = (TextView) findViewById(R.id.listings_tv);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        // define firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        readCurrUserData(); // gets user data from the database (defined below)

        postAdapter = new listingAdapter(this.getBaseContext(),HomePage.posts) ;
        lvAccountPosts = (ListView) findViewById(R.id.lvAccountListing) ;
        lvAccountPosts.setAdapter(postAdapter);
        lvAccountPosts.setItemsCanFocus(true);

    }

    private void updateUI(String name, String email, String location) {
        name_tv3.setText(name);
        email_tv3.setText(email);
        address_tv.setText(location);
    }

    // This method gets user data from the database and listens to changes
    private void readCurrUserData() {
        // listen for changes for user data
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                User currUser = dataSnapshot.getValue(User.class);

                // TODO: remove this
                if (currUser.getListings() != null) {
                    Log.d(TAG, currUser.getListings().toString());
                }

                updateUI(currUser.getFullName(), currUser.getEmail(), currUser.getLocation());
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
        mDatabase.addValueEventListener(userListener); // attach listener to our user database reference
    }
}
