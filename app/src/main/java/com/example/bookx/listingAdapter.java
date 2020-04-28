package com.example.bookx;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookx.Model.Post;
import com.example.bookx.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class listingAdapter extends BaseAdapter {
    private static final String TAG = "***LISTING ADAPTER***";
    private List<Post> posts;
    private Button btnSeePost ;
    private ImageView imgListing;

    Context context;
    public listingAdapter(Context aContext, List<Post> posts) {
        //initializing our data in the constructor.
        context = aContext;

        this.posts = posts ;

    }
    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get certain view according to item's position in list
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View row;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listing_row, parent, false);
        }
        else
        {
            row = convertView;
        }


        TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle) ;
        TextView txtPrice = (TextView) row.findViewById(R.id.txtPrice) ;
        btnSeePost = (Button) row.findViewById(R.id.btnSeePost) ;
        imgListing = (ImageView) row.findViewById(R.id.imgBook);

        if (posts.get(position) != null) {
            txtTitle.setText(posts.get(position).getBookTitle());
            txtPrice.setText(String.format("$%s", String.format("%.2f", posts.get(position).getPrice())));

            loadPicture(position);
        }

        btnSeePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked see post");
                Post tempPost = posts.get(position) ;
                Intent intent = new Intent(parent.getContext(),PostingInfo.class) ;
                intent.putExtra("post",tempPost) ;
                parent.getContext().startActivity(intent);
            }
        });

        return row ;
    }

    // loads picture into the image view for current listing
    private void loadPicture(int position) {
        Log.d(TAG, "TRYING TO LOAD PICTURE iN ROW");
        try {
            Glide.with(context).load(posts.get(position).getImageurl()).into(imgListing); // get the url and load into view with Glide
        } catch (Exception e) {
            Log.d(TAG, "FAILED TO LOAD LISTING PICTURE");
        }
    }
}
