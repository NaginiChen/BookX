package com.example.bookx;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

class ChatListPage extends AppCompatActivity {

    TextView chat_tv;
    private ListView lvChats ;
    private ListAdapter listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        chat_tv = (TextView) findViewById(R.id.chat_tv);

        //listAdapter = new listingAdapter(this.getBaseContext(),HomePage.chats) ;
        lvChats = (ListView) findViewById(R.id.lvAccountListing) ;
        lvChats.setAdapter(listAdapter);
        lvChats.setItemsCanFocus(true);

    }
}


