package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostingInfo extends FragmentActivity implements OnMapReadyCallback{
    private static final String TAG = "***POSTPAGE***";
    private GoogleMap mapAPI ;
    private SupportMapFragment mapFragment ;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private Post currPost ;
    private String location ;
    private Button btnMessage ;
    private ImageView imgListing, imgProfile;
    private TextView txtBookTitle, txtPrice, txtDesc, txtSeller, txtDate , txtCourse, txtAddress, txtISBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_info);

        // get the post object from previous activity
        Bundle extra = getIntent().getExtras() ;
        if(extra != null){
            currPost = (Post) extra.get("post") ;
        }

        Log.d(TAG, "GOT THE POST SUCCESSFULLY");

        // get reference to firebase database and auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // load map fragment to display user location for this listing
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);

        txtBookTitle = (TextView) findViewById(R.id.txtPostBookTitle) ;
        txtPrice = (TextView) findViewById(R.id.txtPostPrice) ;
        txtDesc = (TextView) findViewById(R.id.txtPostDesc) ;
        txtSeller = (TextView) findViewById(R.id.txtPostSeller) ;
        txtDate = (TextView) findViewById(R.id.txtPostDate) ;
        txtCourse = (TextView) findViewById(R.id.txtPostCourse) ;

        btnMessage = (Button) findViewById(R.id.btnInterested) ;
        txtISBN = (TextView) findViewById(R.id.txtPostISBN);
        imgListing = (ImageView) findViewById(R.id.ivListing);
        imgProfile = findViewById(R.id.profileImage) ;

        // get the current user from firebase auth
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser() ;
        
        // if the post is the user's post, we change the button to a delete post button
        if(currPost.getUid().equals(fUser.getUid())){
            toDeletePost() ;
        }else{ 
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(getApplicationContext(),MessageActivity.class) ; // messageActivity is started when message button is clicked
                    intent.putExtra("userid",currPost.getUid()) ; // pass the user id in the intent
                    startActivity(intent);
                }
            });
        }

        // load user picure and listing info
        loadPicture();
        loadPostInfo() ;

        Log.d(TAG, "ISBN IS" + currPost.getIsbn());
    }

    // loads user picture
    private void loadPicture() {
        Log.d(TAG, "TRYING TO LOAD PICTURE iN POSTING INFO");
        try {
            if (currPost.getImageurl() == null) {
                imgListing.setVisibility(View.GONE); // hide image view if no picture for this listing
            } else {
                // Glide is a fast and efficient image loading library for Android
                Glide.with(getBaseContext()).load(currPost.getImageurl()).into(imgListing); // this loads the image from the url into the imgListing image view
            }
        } catch (Exception e) {
            Log.d(TAG, "FAILED TO LOAD LISTING PICTURE");
        }
    }

    // display listing info to the ui
    private void loadPostInfo(){
        txtSeller.setText(currPost.getSeller());
        txtDate.setText(currPost.getDate().toString());
        txtBookTitle.setText(currPost.getBookTitle());
        txtPrice.setText("$" + currPost.getPrice());
        txtDesc.setText(currPost.getDesc());
        txtCourse.setText(currPost.getCourse());
        txtISBN.setText(currPost.getIsbn());

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currPost.getUid()) ; // get the user id of the listing
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User seller = dataSnapshot.getValue(User.class) ;
                if(seller.getImageurl().equals("default")){ // get the image of the user; use default image
                    imgProfile.setImageResource(R.mipmap.ic_launcher); // display image to the imgProfile image view 
                }else{
                    // Glide is a fast and efficient image loading library for Android
                    Glide.with(getApplicationContext()).load(seller.getImageurl()).into(imgProfile) ; // this loads the image from the url into the imgProfile image view
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPostInfo:onCancelled", databaseError.toException());
            }
        }) ;
    }

    // Converts string address to latlng coordinates using Geocoder
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context); // class for handling geocoding (transforming addresses to coordinates)
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5); // returns an array of Addresses that are known to describe the named location
            if (address == null) {
                return null;
            }

            Address location = address.get(0); // get the first address
            p1 = new LatLng(location.getLatitude(), location.getLongitude() ); // create coordinate object

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }


    // This function is called when the map is ready to be used; it will display the user location on the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap ;
        mDatabase.child("users").child(currPost.getUid()).addValueEventListener(new ValueEventListener() { // attach listener to our user database reference

            @Override
            // This method is called when user data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current user
                User seller = dataSnapshot.getValue(User.class);
                Log.d(TAG,seller.getLocation()) ; 

                // display location only if user's show location is turned on
                if(seller.getShowLocation()){
                    // get the location coordinates 
                    Double lat = dataSnapshot.child("latitude").getValue(Double.class) ;
                    Double lng = dataSnapshot.child("longitude").getValue(Double.class) ;
                    LatLng add = new LatLng(lat,lng) ;

                    // add a market to the map to display the user location
                    mapAPI.addMarker(new MarkerOptions().position(add).title(seller.getLocation())) ; // shows the location when marker is clicked
                    mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(add,14)) ; // repositions map when user zoomz in or out
                }else{

                    // user's show location is turned off so we display location at coordinates 0,0
                    LatLng add = new LatLng(0,0) ;
                    mapAPI.addMarker(new MarkerOptions().position(add).title("Seller doesn't allow location sharing")) ; // shows "seller doesn't allow location sharing" when marker is clicked
                    mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(add,14)) ;
                }

            }

            @Override
            // This method is called when we fail to load user listing
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUserListing:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load user information.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // change message button to delete post button if the viewer own this post
    private void toDeletePost(){
        btnMessage.setBackground(getResources().getDrawable(R.drawable.button_delete));
        btnMessage.setTextColor(Color.WHITE);
        btnMessage.setText(R.string.delete_post) ;
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost() ; // when delete button is called, the post is removed from the database
                finish();
            }
        });
    }


    // remove the current user post from the database
    private void deletePost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("listings") ; // get reference to all the listings in database
        reference.addValueEventListener(new ValueEventListener() { // listens for changes to the listings in database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lid = "";

                // iterate through all of the user's listings, to find the current one we want to delete
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class) ;

                    // check if this is the listing we want to delete by comparing uid and date
                    if(post.getUid().equals(currPost.getUid()) && post.getDate().getTime() == currPost.getDate().getTime()){
                        lid = snapshot.getKey() ; // we found the listing, get the listing id
                        break;
                    }
                }
                if(lid.equals("")) // if we don't find the listing to delete, just return
                    return ;

                // now we have a reference to the specific listing (given its id) we want to delete 
                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("listings").child(lid) ;
                mReference.removeValue() ; // remove listing from the listings table in database 
                mReference = FirebaseDatabase.getInstance().getReference("users").child(currPost.getUid()).child("listings").child(lid) ; // also remove listing from the listings attribute in the user table
                mReference.setValue(null) ;
                return ;
            }

            // This method is called when we fail to delete post
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "deletePost:onCancelled", databaseError.toException());
            }
        }) ;

    }
}