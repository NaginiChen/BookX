package com.example.bookx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookx.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountsActivity extends AppCompatActivity {
    private static final String TAG = "***ACCOUNTS***";
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvLocation;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User currUser; // User object that stores all the user information

    private Button btnGoToListings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        // initialize views
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnGoToListings = (Button) findViewById(R.id.btnGoToListings);

        // get firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        getUserData(); // gets user data from the database (defined below)

        btnGoToListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), HomeActivity.class));
            }
        });
    }

    // This method is used to display user information
    private void updateUI(){
        tvName.setText(currUser.getName());
        tvEmail.setText(currUser.getEmail());
        tvLocation.setText(currUser.getLocation());
    }

    // This method gets user data from the database and listens to changes
    private void getUserData() {
        // listen for changes for user data
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                currUser = dataSnapshot.getValue(User.class);

                // TODO: remove this
                if (currUser.getListings() != null) {
                    Log.d(TAG, currUser.getListings().toString());
                }

                updateUI();
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
