package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AccountPage extends AppCompatActivity {
    private static final String TAG = "***ACCOUNT***";

    TextView name_tv3;
    TextView email_tv3;
    TextView address_tv;
    Button changePW_btn;
    TextView listings_tv;
    Button logout_btn;
    ImageView userImg;
    private List<Post> currUserposts;
    private ListAdapter postAdapter ;
    private ListView lvAccountPosts ;

    // firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

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
        userImg = (ImageView) findViewById(R.id.userImg);
        currUserposts = new ArrayList<>();

        // define firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference("user-photos/" + mAuth.getUid());

        readCurrUserData(); // get user data from the database
        loadUserPic(); // load user pic from storage
    }

    private void updateUI(String name, String email, String location) {
        name_tv3.setText(name);
        email_tv3.setText(email);
        address_tv.setText(location);
    }

    private void updateUIListings() {
        postAdapter = new listingAdapter(this.getBaseContext(), currUserposts) ;
        lvAccountPosts = (ListView) findViewById(R.id.lvAccountListing) ;
        lvAccountPosts.setAdapter(postAdapter);
        lvAccountPosts.setItemsCanFocus(true);
    }

    // This method gets user data from the database and listens to changes
    private void readCurrUserData() {
        // listen for changes for user data
        mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() { // attach listener to our user database reference

            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                User currUser = dataSnapshot.getValue(User.class);

                // update the UI with the retrieved user profile information
                updateUI(currUser.getFullName(), currUser.getEmail(), currUser.getLocation());

                // Retrieve all of the listings that belong to the user given listing ids
                Map<String,Object> listings = currUser.getListings();

                if (listings == null) {
                    return;
                }

                Log.d(TAG, "NUMBER OF USER LISTINGS WE SHOULD GET" + listings.size());
                Iterator<Map.Entry<String, Object>> iter = listings.entrySet().iterator();

                // For each lid, grab the corresponding listing and add it to the currUserPosts
                while(iter.hasNext())
                {
                    Map.Entry<String, Object> listing = iter.next();

                    Log.d(TAG, "ISSOLD?" + listing.getValue());
                    // if not sold, retrieve the corresponding listing
                    if(! (boolean) listing.getValue()) {

                        String lid = listing.getKey();

                        mDatabase.child("listings").child(lid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // check here the datasnapshot
                                Post post = dataSnapshot.getValue(Post.class);
                                currUserposts.add(post);

                                updateUIListings();
                                Log.d(TAG, "FOUND A USER LISTING!!!");
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
            }

            @Override
            // This method is called when we fail to get user from the database
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load user information.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUserPic() {

        final long ONE_MEGABYTE = 1024 * 1024;

        mStorage.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        userImg.setMinimumHeight(dm.heightPixels);
                        userImg.setMinimumWidth(dm.widthPixels);
                        userImg.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                userImg.setBackground(getDrawable(R.drawable.defaultuserpic));
            }
        });
    }
}
