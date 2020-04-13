package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    TextView welcome_tv;
    TextView login_tv;
    TextView email_tv;
    EditText email;
    TextView password_tv;
    EditText password;
    Button login_btn;
    TextView signhere_tv;
    Button signhere_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome_tv = (TextView) findViewById(R.id.welcome_tv);
        login_tv = (TextView) findViewById(R.id.login_tv);
        email_tv = (TextView) findViewById(R.id.email_tv);
        email = (EditText) findViewById(R.id.email);
        password_tv = (TextView) findViewById(R.id.password_tv);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);
        signhere_tv = (TextView) findViewById(R.id.signhere_tv);
        signhere_btn = (Button) findViewById(R.id.signhere_btn);

        //when you click login_btn, it will open up the Home page
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });
        //when you click on signhere_btn, it will open up the signup page
        signhere_btn.setOnClickListener(new View.OnClickListener() {
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
