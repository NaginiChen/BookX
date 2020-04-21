package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.bookx.Model.Post;

public class PostingInfo extends AppCompatActivity {

    private Post currPost ;
    private TextView txtBookTitle, txtPrice, txtDesc ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_info);

        Bundle extra = getIntent().getExtras() ;
        if(extra != null){
            currPost = (Post) extra.get("post") ;
        }

        txtBookTitle = (TextView) findViewById(R.id.txtPostBookTitle) ;
        txtPrice = (TextView) findViewById(R.id.txtPostPrice) ;
        txtDesc = (TextView) findViewById(R.id.txtPostDesc) ;

        txtBookTitle.setText(currPost.getBookTitle());
        txtPrice.setText("$" + currPost.getPrice());
        txtDesc.setText(currPost.getDesc());
    }
}
