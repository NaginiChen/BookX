package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignupPage extends AppCompatActivity {
    private static final String TAG = "***SIGNUP***";
    TextView txtSignUp;
    TextView txtIntro;
    TextView txtName;
    EditText edtName;
    TextView txtEmail;
    EditText edtEmail;
    TextView txtPw;
    EditText edtPw;
    TextView txtAddress;
    EditText edtAddress;
    Button btnSignup;
    Button btnUploadPic;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    static final int TAKE_PHOTO = 9999;  //flag that we will use to track the result of taking photo intent
    static final int GET_PHOTO_GALLERY = 9998; //flag that we will use to track the result of getting photo from gallery intent
    static final int CHOOSE_UPLOAD_OPTION = 9997; //flag used to track the choose photo intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        txtSignUp = (TextView) findViewById(R.id.signup_tv);
        txtIntro = (TextView) findViewById(R.id.intro_tv);
        txtName = (TextView) findViewById(R.id.name_tv);
        edtName = (EditText) findViewById(R.id.name_et);
        txtEmail = (TextView) findViewById(R.id.email_tv2);
        edtEmail = (EditText) findViewById(R.id.email_et);
        txtPw = (TextView) findViewById(R.id.pw_tv);
        edtPw = (EditText) findViewById(R.id.pw_et);
        txtAddress = (TextView) findViewById(R.id.address_tv);
        edtAddress = (EditText) findViewById(R.id.address_et);
        btnSignup = (Button) findViewById(R.id.signup_btn);
        btnUploadPic = (Button) findViewById(R.id.upload_pic_btn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        //when you click signup_btn, it will open up the Home page
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }
        });

        // when you click upload_pic_btn the camera will open up
        btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

    }

    // This method validates that the user entered a valid email and password
    private boolean validateForm(String email, String password) {
        boolean valid = true;

        // checks for a valid email
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Required."); // throw an error if empty
            Log.d(TAG, "Email required.");
            valid = false;
        } else if (!Pattern.matches("(.*)[@]([a-z]*)(.edu)", email)) {
            edtEmail.setError(".edu email is required"); // throw an error if not a .edu email
            Log.d(TAG, (email.substring(email.length() - 4)));
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        // checks for a valid password
        if (TextUtils.isEmpty(password)) {
            edtPw.setError("Required."); // throw an error if empty
            Log.d(TAG, "Password required.");
            valid = false;
        } else {
            edtPw.setError(null);
        }

        return valid;
    }

    private void createAccount() {
        final String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPw.getText().toString();
        final String address = edtAddress.getText().toString();
        LatLng lng = null;
        try {
            lng = getLocationFromAddress(getApplicationContext(), address);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please enter a valid address",
                    Toast.LENGTH_LONG).show();
            return;
        }
        final Double latitude = lng.latitude;
        final Double longitude = lng.longitude;


        Log.d(TAG, "createAccount:" + email);

        // Check if email and password are valid
        if (!validateForm(email, password)) {
            Toast.makeText(getBaseContext(), "Invalid inputs.",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Log.d(TAG, "validated:" + email);

        // Referenced https://firebase.google.com/docs/auth/android/password-auth
        // START create_user_with_email using Firebase auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Send user verification link
                            sendEmailVerification();

                            // get user from firebase auth and store it in the firebase database
                            FirebaseUser user = mAuth.getCurrentUser();
                            User currUser = new User(user.getEmail(), name, address); // TODO: get user name and location when UI is done

                            // address to coordinates
                            LatLng lng = getLocationFromAddress(getApplicationContext(), address);
                            currUser.setLatitude(latitude);
                            currUser.setLongitude(longitude);
                            mDatabase.child("users").child(user.getUid()).setValue(currUser);

                        } else {
                            // If sign in fails, display a message to the user
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Send user an email verification link using Firebase auth
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, user.getEmail());
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "EMAIL SENT");
                            // Verification email sent, let user know to verify
                            Toast.makeText(getBaseContext(),
                                    "Verification email sent to " + user.getEmail() +
                                            ". Please verify and proceed to sign in.",
                                    Toast.LENGTH_LONG).show();

                            // Return back to sign in
                            openSignInPage();
                        } else {
                            // Verification email not sent, let user know to try again
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getBaseContext(),
                                    "Failed to send verification email. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openSignInPage() {
        Intent intent = new Intent(this, SignInPage.class);
        startActivity(intent);

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
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    //    // TODO: Make a check somewhere
    boolean isCameraAvailable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);  //just another friendly neighborhood manager
    }

    // This method opens up the camera and allows user to take or upload a profile picture
    private void getPhoto() {

        // Camera.
        List<Intent> cameraIntents = new ArrayList<Intent>();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        cameraIntents.add(captureIntent);
        cameraIntents.add(galleryIntent);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));


        Log.d(TAG, "HERE");
        startActivityForResult(chooserIntent, CHOOSE_UPLOAD_OPTION);
    }

    // Callback from startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Verify that we got something back that was valid
        if (!(resultCode == RESULT_OK)) {
            Toast.makeText(this, "Start Activity for Result Failed.  Does your device support this feature?", Toast.LENGTH_LONG).show();
            return;
        }

        // check our "dye" then check the data
        switch (requestCode) {
            case CHOOSE_UPLOAD_OPTION:
                Intent actual_intent = (Intent) data.getExtras().get(Intent.EXTRA_INITIAL_INTENTS);

                if (actual_intent.getAction() == android.provider.MediaStore.ACTION_IMAGE_CAPTURE) {
                    startActivityForResult(actual_intent, TAKE_PHOTO);
                } else {
                    startActivityForResult(actual_intent, GET_PHOTO_GALLERY);
                }
                break;

            default:
                Bundle bundleData = data.getExtras();           //images are stored in a bundle wrapped within the intent
                Bitmap photo = (Bitmap) bundleData.get("data");  //the bundle key is "data"

                uploadImgToStorage(photo, "user-photos");
                break;
        }
    }

    private void uploadImgToStorage(Bitmap bitmap, String folder_name) {
        // Get the data as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // upload to our firebase storage reference
        UploadTask uploadTask = mStorage.child(folder_name).child(mAuth.getUid()).putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "uploading to firebase storage failed");
                Toast.makeText(getBaseContext(), "Failed to upload picture. Please try again.", Toast.LENGTH_LONG);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "successfully uploaded picture to firebase storage");
                Toast.makeText(getBaseContext(), "Successfully uploaded picture. Click sign up to finish registering!", Toast.LENGTH_LONG);
            }
        });
    }
}