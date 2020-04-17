package com.example.bookx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "***SIGN_IN***";

    // Firebase instance variables
    private FirebaseAuth mAuth;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        // onclick for sign in button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                signIn(email, password);
            }
        });
    }

    // This method calls firebase auth to sign in the user given email and password
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        // start the sign in with email
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task< AuthResult > task) {
            if (task.isSuccessful()) {
                // Sign in success but still must check if user is verified
                FirebaseUser user = mAuth.getCurrentUser();

                // If user is verified, proceed to Listings activity
                if (user != null && user.isEmailVerified()) {
                    // TODO: Proceed to ListingsActivity
                    Log.d(TAG, "Successful sign in.");

                    Intent activity = new Intent(getBaseContext(), AccountsActivity.class);
                    startActivity(activity);

                } else { // User has not verified email
                    Toast.makeText(getBaseContext(), "Email is not verified. Verify and try again.",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                // Sign in has failed, display a message to the user
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getBaseContext(), "Sign in failed. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });
    }
}
