package com.example.bookx;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "myB";
    EditText name;
    EditText address;
    EditText password;
    EditText passwordRetype;
    Button uploadBtn;
    Button saveBtn;

    String imgUrl;

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
        setContentView(R.layout.activity_edit_profile);

        name = (EditText) findViewById(R.id.nameField);
        address = (EditText) findViewById(R.id.addressField);
        password = (EditText) findViewById(R.id.passwordField);
        passwordRetype = (EditText) findViewById(R.id.passwordFieldRetype);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        updateFields();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFields();
            }
        });

    }

    private void updateFields(){
        mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getFullName());
                address.setText(user.getLocation());
                imgUrl = user.getImageurl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void saveFields(){
        mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                String toastMsg = "";

                if(!name.getText().toString().equals("")){
                    map.put("fullName", name.getText().toString());
                }
                if(!address.getText().toString().equals("")){
                    String add = address.getText().toString() ;
                    LatLng lng = null ;
                    try{
                        lng = getLocationFromAddress(getApplicationContext(),add) ; // check if valid address
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(), "Please enter a valid address",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    final Double latitude = lng.latitude;
                    final Double longitude = lng.longitude;
                    map.put("location", address.getText().toString());
                    map.put("latitude",latitude) ;
                    map.put("longitude",longitude) ;
                }
                if(!map.isEmpty()){
                    reference.updateChildren(map);
                    toastMsg += "Name and/or address is updated! ";
                }
                if(password.getText().toString().equals(passwordRetype.getText().toString()) && !password.getText().toString().equals("")){
                    fUser.updatePassword(password.getText().toString());
                    toastMsg += "Password is updated. ";
                }
                else{
                    if (!TextUtils.isEmpty(password.getText().toString()) && !TextUtils.isEmpty(passwordRetype.getText().toString())) {
                        toastMsg += "Passwords do not match or are invalid. ";
                    }
                }


                Toast.makeText(getBaseContext(), toastMsg, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openImage(){
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT) ;
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver() ;
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton() ;
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    private void uploadImage(){
        if(imageUrl != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUrl));
            uploadTask = fileReference.putFile(imageUrl) ;
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException() ;
                    }
                    return fileReference.getDownloadUrl() ;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult() ;
                        String mUri = downloadUri.toString() ;

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid()) ;
                        HashMap<String, Object> map = new HashMap<>() ;
                        map.put("imageurl",mUri) ;
                        reference.updateChildren(map) ;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"No image selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUrl = data.getData() ;

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload in progress", Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
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

}
