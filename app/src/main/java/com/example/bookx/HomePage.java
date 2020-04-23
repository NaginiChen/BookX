package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.Chat;
import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "myB";

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

    Boolean notificationToggle;
    Boolean firstBootUp;

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

        notificationToggle = true;
        firstBootUp = true;
        listenForNewMessages();
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
        Collections.reverse(posts);
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

    // listens for incoming messages
    private void listenForNewMessages(){
        mDatabase.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                final Chat chatVal = dataSnapshot.getValue(Chat.class);

                mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class) ;
                            if( snapshot.getKey().equals(chatVal.getSender()) && chatVal.getReceiver().equals(mAuth.getUid()) ){
                                if(notificationToggle){
                                    sendNotification(chatVal.getMessage(), user.getFullName() );
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void sendNotification(String message, String sender){
        createNotificationChannel();
        Intent intent = new Intent(this, MessageList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Message from " + sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            String description = "Messages from other users";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
