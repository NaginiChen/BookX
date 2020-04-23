package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
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
    private ImageView imgListing;
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

        btnMessage = (Button) findViewById(R.id.btnInterested) ;
        txtISBN = (TextView) findViewById(R.id.txtPostISBN);
        imgListing = (ImageView) findViewById(R.id.ivListing);


        loadPicture();
        txtSeller.setText(currPost.getSeller());
        txtDate.setText(currPost.getDate().toString());
        txtBookTitle.setText(currPost.getBookTitle());
        txtPrice.setText("$" + currPost.getPrice());
        txtDesc.setText(currPost.getDesc());
        txtCourse.setText(currPost.getCourse());
        txtISBN.setText(currPost.getIsbn());

        Log.d(TAG, "ISBN IS" + currPost.getIsbn());

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(),MessageActivity.class) ;
                intent.putExtra("userid",currPost.getUid()) ;
                startActivity(intent);
            }
        });

    }

    // loads picture into the image view for current listing
    private void loadPicture() {
        Log.d(TAG, "TRYING TO LOAD PICTURE iN POSTING INFO");
        try {
            if (currPost.getImageurl() == null) {
                imgListing.setVisibility(View.GONE); // hide image view if no picture for this listing
            } else {
                Glide.with(getBaseContext()).load(currPost.getImageurl()).into(imgListing); // get the url and load into view with Glide
            }
        } catch (Exception e) {
            Log.d(TAG, "FAILED TO LOAD LISTING PICTURE");
        }
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
        LatLng add = new LatLng(currPost.getLatitude(),currPost.getLongitude()) ;
        mapAPI.addMarker(new MarkerOptions().position(add).title("ADDRESS")) ;
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(add,15)) ;

    }
}