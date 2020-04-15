package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.afinal.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class AccountPage extends AppCompatActivity {
    TextView name_tv3;
    TextView email_tv3;
    TextView address_tv;
    Button changePW_btn;
    TextView listings_tv;
    Button logout_btn;
    private ListAdapter postAdapter ;
    private ListView lvAccountPosts ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        name_tv3 = (TextView) findViewById(R.id.name_tv3);
        email_tv3 = (TextView) findViewById(R.id.email_tv3);
        address_tv = (TextView) findViewById(R.id.address_tv);
        changePW_btn = (Button) findViewById(R.id.changePW_btn);
        listings_tv = (TextView) findViewById(R.id.listings_tv);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        postAdapter = new listingAdapter(this.getBaseContext(),HomePage.posts) ;
        lvAccountPosts = (ListView) findViewById(R.id.lvAccountListing) ;
        lvAccountPosts.setAdapter(postAdapter);
        lvAccountPosts.setItemsCanFocus(true);

    }
}
