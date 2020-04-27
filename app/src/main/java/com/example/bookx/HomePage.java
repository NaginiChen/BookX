package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.Chat;
import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "myB";

    private User user ;
    private String userid ;
    TextView title_tv, txtUnread;
    Button account_btn;
    Button preferences_btn;
    Button upload_btn;
    Button chat_btn;
    ImageButton ibtSearch, ibtFilter;
    EditText edtSearch ;
//    ImageButton btSearchOption ;
    private List<Post> posts, allPosts;
    private ListView lvPosts;
    private ListAdapter postAdapter;

    // firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    SharedPreferences pref;

    Boolean firstBootUp;
    Comparator<Post> price_l2h = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            if(o1.getPrice() == o2.getPrice())
                return (int) (o1.getDate().getTime() - o1.getDate().getTime());
            return (int)(o1.getPrice() - o2.getPrice()) ;
        }
    } ;
    Comparator<Post> price_h2l = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            if(o1.getPrice() == o2.getPrice())
                return (int) (o1.getDate().getTime() - o1.getDate().getTime());
            return (int)(o2.getPrice() - o1.getPrice()) ;
        }
    } ;
    Comparator<Post> time_l2h = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            if(o1.getDate().getTime() == o2.getDate().getTime())
                return (int) (o1.getPrice() - o2.getPrice());
            return (int)(o1.getDate().getTime() - o2.getDate().getTime()) ;
        }
    } ;

    Comparator<Post> time_h2l = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            if(o1.getDate().getTime() == o2.getDate().getTime())
                return (int) (o1.getPrice() - o2.getPrice());
            return (int)(o2.getDate().getTime() - o1.getDate().getTime()) ;
        }
    } ;
    Comparator<Post> distance_l2h = new Comparator<Post>() {
        @Override
        public int compare(final Post o1, final Post o2) {
            double distance1 = distance(user.getLatitude(),user.getLongitude(),o1.getLatitude(),o1.getLongitude()) ;
            double distance2 = distance(user.getLatitude(),user.getLongitude(),o2.getLatitude(),o2.getLongitude()) ;
            if(distance1 == distance2) return (int)(o1.getDate().getTime() - o2.getDate().getTime()) ;
            return (int) (distance1 - distance2) ;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent() ;
        Bundle extra = intent.getExtras() ;
        if(extra != null){
            this.user = (User) extra.get("user") ;
            this.userid = extra.getString("userid") ;
        }

        title_tv = (TextView) findViewById(R.id.title_tv);
        account_btn = (Button) findViewById(R.id.account_btn);
        preferences_btn = (Button) findViewById(R.id.preferences_btn);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        ibtSearch = findViewById(R.id.ibtSearch) ;
        ibtFilter = findViewById(R.id.ibtFilter) ;
        edtSearch = findViewById(R.id.edtSearch) ;
        txtUnread = findViewById(R.id.txtUnread) ;

        getUnreadNum() ;

        pref = getSharedPreferences("com.example.bookx.notification", Context.MODE_PRIVATE);

//        ibtSearchOption = findViewById(R.id.ibtSearchOption) ;
        posts = new ArrayList<>();
        allPosts = new ArrayList<>() ;

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

        firstBootUp = true;
        listenForNewMessages();

        ibtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posts.clear();
                String keyword = edtSearch.getText().toString().toLowerCase() ;
                if(Pattern.matches("\\s+",keyword)){
                    posts.addAll(allPosts) ;
                }else{
                    for(Post post : allPosts){
                        String postInfo = post.toString() ;
                        if(postInfo.contains(keyword)) posts.add(post) ;
                    }
                }
                updateUIListings();
            }
        });

//        ibtSearchOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(HomePage.this,ibtSearchOption) ;
//                popupMenu.getMenuInflater().inflate(R.menu.menu_search,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return true;
//                    }
//                });
//                popupMenu.show();
//            }
//        });

        ibtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(HomePage.this,ibtFilter) ;
                popupMenu.getMenuInflater().inflate(R.menu.menu_filter,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_price_l2h:
                                Collections.sort(posts,price_l2h);
                                break ;
                            case R.id.menu_price_h2l:
                                Collections.sort(posts,price_h2l);
                                break ;
                            case R.id.menu_time_h2l:
                                Collections.sort(posts,time_h2l);
                                break ;
                            case R.id.menu_time_l2h:
                                Collections.sort(posts,time_l2h);
                                break ;
                            case R.id.menu_distance:
                                Collections.sort(posts,distance_l2h);
                                break ;
                        }
                        updateUIListings();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void openAccountPage() {
        Intent intent = new Intent(this, AccountPage.class);
        intent.putExtra("userid", this.userid) ;
        intent.putExtra("user", this.user) ;
        startActivity(intent);
        finish();
    }

    public void openPreferencesPage() {
        Intent intent = new Intent(this, PreferencesPage.class);
        intent.putExtra("userid", this.userid) ;
        intent.putExtra("user", this.user) ;
        startActivity(intent);
        finish();
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
                    // handle the post
                    Post post = postSnapshot.getValue(Post.class);

                    // if not current user's post, add to list so adapter can display
                    if (!post.getUid().equals(mAuth.getUid())) {
                        posts.add(post);

                    }
                }
                Collections.reverse(posts);
                allPosts.addAll(posts) ;
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;

            return dist;
        }
    }

    void getUnreadNum(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats") ;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0 ;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class) ;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isRead())
                        count++ ;
                }
                txtUnread.setText("( " + count + " )");
                if(count == 0) txtUnread.setTextColor(Color.WHITE);
                else txtUnread.setTextColor(Color.RED);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }

    // listens for incoming messages
    private void listenForNewMessages(){
        mDatabase.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, String prevChildKey) {
                final Chat chatVal = dataSnapshot.getValue(Chat.class);

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
                        for(DataSnapshot snapshot : dataSnapshotUser.getChildren()){
                            User user = snapshot.getValue(User.class) ;
                            if( snapshot.getKey().equals(chatVal.getSender()) && chatVal.getReceiver().equals(mAuth.getUid()) && !chatVal.isSent() ){
                                HashMap<String , Object> map = new HashMap<>();
                                map.put("sent", true);
                                dataSnapshot.getRef().updateChildren(map);

                                if(pref.getBoolean("notificationIsOn", false)){
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_1")
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

    // Source: https://developer.android.com/training/notify-user/channels
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            String description = "Messages from other users";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // forbidding back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
