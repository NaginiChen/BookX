package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupPage extends AppCompatActivity {
    TextView signup_tv;
    TextView intro_tv;
    TextView name_tv;
    EditText name_et;
    TextView email_tv2;
    EditText email_et;
    TextView pw_tv;
    EditText pw_et;
    TextView address_tv;
    EditText address_et;
    TextView uploadpic_tv;
    Button signup_btn;
    Button verification_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        signup_tv = (TextView) findViewById(R.id.signup_tv);
        intro_tv = (TextView) findViewById(R.id.intro_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        name_et = (EditText) findViewById(R.id.name_et);
        email_tv2 = (TextView) findViewById(R.id.email_tv2);
        email_et = (EditText) findViewById(R.id.email_et);
        pw_tv = (TextView) findViewById(R.id.pw_tv);
        pw_et = (EditText) findViewById(R.id.pw_et);
        address_tv = (TextView) findViewById(R.id.address_tv);
        address_et = (EditText) findViewById(R.id.address_et);
        uploadpic_tv = (TextView) findViewById(R.id.uploadpic_tv);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        verification_btn = (Button) findViewById(R.id.verification_btn);

        //when you click signup_btn, it will open up the Home page
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });
    }

    public void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

    }
}
