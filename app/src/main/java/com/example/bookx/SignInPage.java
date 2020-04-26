package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignInPage extends AppCompatActivity {
    private static final String TAG = "***SIGNIN***";
    TextView txtWelcome;
    TextView txtLogin;
    TextView txtEmail;
    EditText edtEmail;
    TextView txtPassword;
    EditText edtPassword;
    Button btnLogin;
    TextView txtSignUp;
    Button btnSignUp;
    private FirebaseAuth mAuth;

    private void logIn(String email, String password) {

        // start the sign in with email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task< AuthResult > task) {
                        if (task.isSuccessful()) {
                            // Sign in success but still must check if user is verified
                            FirebaseUser fuser = mAuth.getCurrentUser();

                            // If user is verified, proceed to Listings activity
                            if (fuser.isEmailVerified()) {

                                // get the user id from firebase auth and add user to firebase database
                                final String userid = fuser.getUid() ;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference() ; // path of where user is stored in database
                                reference.child("users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // get the user data and pass it to next homepage activity in bundle
                                        User user = dataSnapshot.getValue(User.class) ;
                                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                        intent.putExtra("user",user) ;
                                        intent.putExtra("userid",userid) ;
                                        startActivity(intent);
                                    }

                                    // failed to get user data after sign in, throw message
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getBaseContext(), "Failed to get user information. Please try again.", Toast.LENGTH_LONG);
                                        Log.d(TAG, "cannot get user data in sign in");
                                    }
                                }) ;


                            } else { // User has not verified email
                                Toast.makeText(getBaseContext(), "Email is not verified. Verify and try again.",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            // Sign in has failed, display a message to the user
                            Toast.makeText(getBaseContext(), "Sign in failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWelcome = (TextView) findViewById(R.id.welcome_tv);
        txtLogin = (TextView) findViewById(R.id.login_tv);
        txtEmail = (TextView) findViewById(R.id.email_tv);
        edtEmail = (EditText) findViewById(R.id.email);
        txtPassword = (TextView) findViewById(R.id.password_tv);
        edtPassword= (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login_btn);
        txtSignUp = (TextView) findViewById(R.id.signhere_tv);
        btnSignUp = (Button) findViewById(R.id.signhere_btn);



        mAuth = FirebaseAuth.getInstance();
        //when you click login_btn, it will open up the Home page
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If non empty email and non empty password, try login
                if (!TextUtils.isEmpty(edtEmail.getText().toString()) && !TextUtils.isEmpty(edtPassword.getText().toString())) {
                    String username = edtEmail.getText().toString() ;
                    String pw = edtPassword.getText().toString() ;
                    logIn(username,pw);
                }

                // If empty email throw an error
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    edtEmail.setError("Please enter your email.");
                }

                // If empty password, throw an error
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    edtPassword.setError("Please enter your password.");
                }

                return;
            }
        });
        //when you click on singup_btn, it will open up the signup page
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupPage();
            }
        });



    }

    public void openSignupPage() {
        Intent intent = new Intent(this, SignupPage.class);
        startActivity(intent);
    }

}
