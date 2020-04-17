package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
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
                            FirebaseUser user = mAuth.getCurrentUser();

                            // If user is verified, proceed to Listings activity
                            if (user.isEmailVerified()) {
                                // TODO: Proceed to ListingsActivity

                                openHomePage();

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
                String username = edtEmail.getText().toString() ;
                String pw = edtPassword.getText().toString() ;
                logIn(username,pw);

            }
        });
        //when you click on signhere_btn, it will open up the signup page
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupPage();
            }
        });



    }
    public void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

    }

    public void openSignupPage() {
        Intent intent = new Intent(this, SignupPage.class);
        startActivity(intent);
    }

}
