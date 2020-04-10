package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ListingPage extends AppCompatActivity {
    TextView listing_tv;
    Button cancel_btn;
    Button post_btn;
    EditText bookname_et;
    EditText isbn_et;
    Button uploadisbn_btn;
    EditText class_et;
    EditText price_et;
    EditText description_et;
    TextView location_tv;
    Button Ylocation_btn;
    Button Nlocation_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_page);

        listing_tv = (TextView) findViewById(R.id.listing_tv);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        post_btn = (Button) findViewById(R.id.post_btn);
        bookname_et = (EditText) findViewById(R.id.bookname_et);
        isbn_et = (EditText) findViewById(R.id.isbn_et);
        uploadisbn_btn = (Button) findViewById(R.id.uploadisbn_btn);
        class_et = (EditText) findViewById(R.id.class_et);
        price_et = (EditText) findViewById(R.id.price_et);
        description_et = (EditText) findViewById(R.id.description_et);
        location_tv = (TextView) findViewById(R.id.location_tv);
        Ylocation_btn = (Button) findViewById(R.id.Ylocation_btn);
        Nlocation_btn = (Button) findViewById(R.id.Nlocation_btn);

        //when you click cancel_btn, it will go back to the Home page
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostingPage();
            }
        });
    }

    public void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

    }

    public void openPostingPage() {
        Intent intent = new Intent(this, PostingPage.class);
        startActivity(intent);

    }
}
