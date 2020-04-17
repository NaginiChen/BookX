package com.example.bookx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookx.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "***SIGNUP***";

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        // Initialize Firebase database auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // onclick for the signup button which creates the account
        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
            etEmail.setError("Required."); // throw an error if empty
            Log.d(TAG, "Email required.");
            valid = false;
        } else if (!Pattern.matches("(.*)[@]([a-z]*)(.edu)",email)) {
            etEmail.setError(".edu email is required"); // throw an error if not a .edu email
            Log.d(TAG, (email.substring(email.length() - 4)));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        // checks for a valid password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required."); // throw an error if empty
            Log.d(TAG, "Password required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    // Create an account for the new user given a valid email and password
    private void createAccount() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        Log.d(TAG, "createAccount:" + email);

        // Check if email and password are valid
        if (!validateForm(email, password)) {
            Toast.makeText(getBaseContext(), "Invalid inputs.",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Log.d(TAG, "validated:" + email);

        // START create_user_with_email using Firebase auth
        // Referenced https://firebase.google.com/docs/auth/android/manage-users
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Send user verification link
                            sendEmailVerification();

                            // get user from firebase auth and store it in the firebase database
                            FirebaseUser user = mAuth.getCurrentUser();
                            User currUser = new User(user.getEmail(), "Kelly", "100 Bay State Road, Boston, MA 02215"); // TODO: get user name and location when UI is done
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
    // Referenced https://firebase.google.com/docs/auth/android/manage-users
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Verification email sent, let user know to verify
                            Toast.makeText(getBaseContext(),
                                    "Verification email sent to " + user.getEmail() +
                                            ". Please verify and proceed to sign in.",
                                    Toast.LENGTH_LONG).show();

                            // Return back to sign in
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
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
}
