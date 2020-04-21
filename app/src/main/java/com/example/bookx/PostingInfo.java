package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.Post;
import com.example.bookx.Model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Post currPost ;
    private String location ;

    private TextView txtBookTitle, txtPrice, txtDesc, txtSeller, txtDate , txtCourse, txtAddress, txtISBN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_info);

        Bundle extra = getIntent().getExtras() ;
        if(extra != null){
            currPost = (Post) extra.get("post") ;
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragMap) ;
        mapFragment.getMapAsync(this);

        txtBookTitle = (TextView) findViewById(R.id.txtPostBookTitle) ;
        txtPrice = (TextView) findViewById(R.id.txtPostPrice) ;
        txtDesc = (TextView) findViewById(R.id.txtPostDesc) ;
        txtSeller = (TextView) findViewById(R.id.txtPostSeller) ;
        txtDate = (TextView) findViewById(R.id.txtPostDate) ;
        txtCourse = (TextView) findViewById(R.id.txtPostCourse) ;
        txtISBN = (TextView) findViewById(R.id.txtPostISBN);


        txtSeller.setText(currPost.getSeller());
        txtDate.setText(currPost.getDate().toString());
        txtBookTitle.setText(currPost.getBookTitle());
        txtPrice.setText("$" + currPost.getPrice());
        txtDesc.setText(currPost.getDesc());
        txtCourse.setText(currPost.getCourse());
        txtISBN.setText(currPost.getIsbn());

        Log.d(TAG, "ISBN IS" + currPost.getIsbn());

    }
    // string address -> latlng coordinates
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

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
                Double lat = dataSnapshot.child("latitude").getValue(Double.class) ;
                Double lng = dataSnapshot.child("longitude").getValue(Double.class) ;
                LatLng add = new LatLng(lat,lng) ;
                mapAPI.addMarker(new MarkerOptions().position(add).title(seller.getLocation())) ;
                mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(add,15)) ;
            }

            @Override
            // This method is called when we fail to get user from the database
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUserListing:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load user information.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}