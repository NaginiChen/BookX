package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AccountPage extends AppCompatActivity {
    private static final String TAG = "***ACCOUNT***";

    TextView name_tv3;
    TextView email_tv3;
    TextView address_tv;
    TextView listings_tv;
    Button logout_btn, btnBack;
    ImageView imgProfile ;
    Button editProfileBtn;

    private List<Post> currUserposts;
    private ListAdapter postAdapter ;
    private ListView lvAccountPosts ;

    private User user ;
    private String userid ;

    // firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser fUser ;

    StorageReference storageReference ;
    private static final int IMAGE_REQUEST = 1 ;
    private Uri imageUrl ;
    private StorageTask uploadTask ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        // get the user from the home page intent to display user information
        Intent intent = getIntent() ;
        Bundle extra = intent.getExtras() ;
        if(extra != null){
            this.user = (User) extra.get("user") ;
            this.userid = extra.getString("userid") ;
        }

        name_tv3 = (TextView) findViewById(R.id.name_tv3);
        email_tv3 = (TextView) findViewById(R.id.email_tv3);
        address_tv = (TextView) findViewById(R.id.address_tv);
        listings_tv = (TextView) findViewById(R.id.listings_tv);
        logout_btn = (Button) findViewById(R.id.logout_btn);
        imgProfile = (ImageView) findViewById(R.id.profileImage) ;
        editProfileBtn = (Button) findViewById(R.id.editProfileBtn);
        btnBack = (Button) findViewById(R.id.btnBack) ;
        currUserposts = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference("uploads") ;
        fUser = FirebaseAuth.getInstance().getCurrentUser() ;

        // define firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        readCurrUserData(); // get user data from the database

        // when image is clicked, user can change profile picture
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage() ;
            }
        });

        // back button brings user back to home page and passes in user information again
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class) ;
                intent.putExtra("user",user) ;
                intent.putExtra("userid",userid) ;
                startActivity(intent);
                finish();
            }
        });

        // when log out button is clicked, user is logged out of firebase auth and is brought back to the home page
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(),SignInPage.class) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP); // close all other activities
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(),"You have successfully logged out.",Toast.LENGTH_LONG).show();
            }
        });

        // when button is clicked, user is brought to the edit profile page
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfile();
            }
        });
    }

    // calls an intent to open up gallery so user can select an image to upload
    private void openImage(){
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT) ;
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    // get file extension for the image uri
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver() ;
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton() ; // detects mime type of file
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    // Upload user image to database
    private void uploadImage(){

        if(imageUrl != null){
            // reference to firebase storage
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUrl));
            uploadTask = fileReference.putFile(imageUrl) ; // put image file into storage at given reference
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){   // successfully uploaded image, return the url
                        throw task.getException() ;
                    }
                    return fileReference.getDownloadUrl() ;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult() ;    // store storage url to the database so user attributes will contain path to image
                        String mUri = downloadUri.toString() ;

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid()) ;

                        // rewrite user information with image url
                        HashMap<String, Object> map = new HashMap<>() ;
                        map.put("imageurl",imageUrl) ;
                        reference.updateChildren(map) ;

                        // Glide is a fast and efficient image loading library for Android
                        // this just displays the image to the imgProfile view
                        Glide.with(getApplicationContext()).load(imageUrl).into(imgProfile) ;


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),"Failed to upload image.",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"No image selected", Toast.LENGTH_LONG).show();
        }
    }

    // callback for startActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        // user uploaded a picture successfully
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUrl = data.getData() ; // get url of image to upload to firebase

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload in progress", Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
        }
    }

    // displays user information
    private void updateUI(String name, String email, String location, String imgurl) {
        name_tv3.setText(name);
        email_tv3.setText(email);
        address_tv.setText(location);
        if(imgurl.equals("default")){
            imgProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(getApplicationContext()).load(imgurl).into(imgProfile) ;
        }

    }

    // displays user listings using listing adapater
    private void updateUIListings() {
        postAdapter = new listingAdapter(this.getBaseContext(), currUserposts) ;
        lvAccountPosts = (ListView) findViewById(R.id.lvAccountListing) ;
        lvAccountPosts.setAdapter(postAdapter);
        lvAccountPosts.setItemsCanFocus(true);
    }

    // function is called to open edit profile activity
    private void openEditProfile(){
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }

    // This method gets user data from the database and listens to changes
    private void readCurrUserData() {
        // listen for changes for user data
        mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() { // attach listener to our user database reference

            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {

                currUserposts.clear() ;
                // Get the current user
                User currUser = dataSnapshot.getValue(User.class);

                // update the UI with the retrieved user profile information
                updateUI(currUser.getFullName(), currUser.getEmail(), currUser.getLocation(), currUser.getImageurl());

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

                    // if listing exists, add the listing to the currUserPosts list
                    if(! (boolean) listing.getValue()) {

                        final String lid = listing.getKey();
                        mDatabase.child("listings").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // check here the datasnapshot
                                Post post = dataSnapshot.child(lid).getValue(Post.class);
                                currUserposts.add(post);

                                updateUIListings(); // display listings information
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

    // forbidding back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
