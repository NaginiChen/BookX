package com.example.bookx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "***SIGN_IN***";
    // Firebase instance variables
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase database and auth
//        mDatabase = FirebaseDatabase.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//
//        DatabaseReference myRef1 = mDatabase.getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.

//        if (currentUser == null) {
            // Not signed in, launch the Sign UP activity
            // TODO: CREATE SignInActivity
//            startActivity(new Intent(this, SignUpActivity.class));

//            finish();
//            return;
//        } else {
            // User is signed in, proceed to Listings activity
            // TODO: Proceed to ListingsActivity

            // TODO: Get profile information
//            mUsername = currentUser.getDisplayName();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            }
        }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        // start the sign in with email
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task< AuthResult > task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "Verification email sent. Please verify and procees to " +
                        "log in.");
                FirebaseUser user = mAuth.getCurrentUser();

                // User is signed in, proceed to Listings activity
                // TODO: Proceed to ListingsActivity
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getBaseContext(), "Authentication failed. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    });
}

}
