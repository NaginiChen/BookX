package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
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

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


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

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //when you click signup_btn, it will open up the Home page
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

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
        } else if (!Pattern.matches("(.*)[@]([a-z]*)(.edu)",email)) {
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
        final String name = edtName.getText().toString() ;
        String email = edtEmail.getText().toString();
        String password = edtPw.getText().toString();
        final String address = edtAddress.getText().toString() ;
        LatLng lng = null ;
        try{
            lng = getLocationFromAddress(getApplicationContext(),address) ;
        }catch (Exception e){
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
                            LatLng lng = getLocationFromAddress(getApplicationContext(),address) ;
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
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}