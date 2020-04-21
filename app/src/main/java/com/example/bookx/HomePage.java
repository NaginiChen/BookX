package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "***HOME***";

    TextView title_tv;
    Button account_btn;
    Button preferences_btn;
    Button upload_btn;
    Button chat_btn;
    private List<Post> posts;
    private ListView lvPosts;
    private ListAdapter postAdapter;

    // firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        title_tv = (TextView) findViewById(R.id.title_tv);
        account_btn = (Button) findViewById(R.id.account_btn);
        preferences_btn = (Button) findViewById(R.id.preferences_btn);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        posts = new ArrayList<>();

        // define firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listingData(); // read posts and display


        //when you click chat_btn, it will open up the Chat List Page
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatListPage();
            }
        });

        //when you click account_btn, it will open up account page
        account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountPage();
            }
        });

        //when you click preferences_btn, it will open up preferences page
        preferences_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferencesPage();
            }
        });

        //when you click upload_btn, it will open up listing page
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListingPage();
            }
        });
    }

    public void openAccountPage() {
        Intent intent = new Intent(this, AccountPage.class);
        startActivity(intent);
    }

    public void openPreferencesPage() {
        Intent intent = new Intent(this, PreferencesPage.class);
        startActivity(intent);
    }

    public void openListingPage() {
        Intent intent = new Intent(this, ListingPage.class);
        startActivity(intent);
    }

    public void openChatListPage() {
        Intent intent = new Intent(this, MessageList.class);
        startActivity(intent);

    }


    // send listing data to adapter to display
    private void updateUIListings() {
        postAdapter = new listingAdapter(this.getBaseContext(), posts);
        lvPosts = (ListView) findViewById(R.id.lvListing);
        lvPosts.setAdapter(postAdapter);
        lvPosts.setItemsCanFocus(true);
    }

    // This method gets user data from the database and listens to changes
    private void listingData() {
        // listen for changes for user data
        mDatabase.child("listings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "FOUND OTHER USER LISTING!!!");
                    // handle the post
                    Post post = postSnapshot.getValue(Post.class);

                    // if not current user's post, add to list so adapter can display
                    if (!post.getUid().equals(mAuth.getUid())) {
                        posts.add(post);
                    }
                }

                updateUIListings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User listing failed, log a message
                Log.w(TAG, "loadUserListing:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load your listings. Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

