package com.example.bookx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.bookx.Model.Post;
import com.example.bookx.R;

import java.util.List;
import java.util.Date;

public class listingAdapter extends BaseAdapter {
    private List<Post> posts;

    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
    // Eg, spawning & receiving intents, locating the various managers.

    //STEP 2: Override the Constructor, be sure to:
    // grab the context, we will need it later, the callback gets it as a parm.
    // load the strings and images into object references.
    public listingAdapter(Context aContext, List<Post> posts) {
//initializing our data in the constructor.
        context = aContext;  //saving the context we'll need it again.

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        row = inflater.inflate(R.layout.listview_row, parent, false);  //

// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.listing_row, parent, false);
        }
        else
        {
            row = convertView;
        }

        TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle) ;
        TextView txtSeller = (TextView) row.findViewById(R.id.txtSeller) ;
        TextView txtDate = (TextView) row.findViewById(R.id.txtDate) ;
        TextView txtDesc = (TextView) row.findViewById(R.id.txtDesc) ;
        TextView txtCourse = (TextView) row.findViewById(R.id.txtCourse) ;
        TextView txtPrice = (TextView) row.findViewById(R.id.txtPrice) ;
        TextView txtISBN = (TextView) row.findViewById(R.id.txtISBN) ; // TODO: SHOW ISBN

        txtTitle.setText(posts.get(position).getBookTitle());
        txtSeller.setText(posts.get(position).getSeller());
        String[] date = posts.get(position).getDate().toString().split("\\s"); // get post date and parse for day month year
        txtDate.setText(date[0] + " " + date[1] + " " + date[2]);
        txtDesc.setText(posts.get(position).getDesc());
        txtCourse.setText(posts.get(position).getCourse());
        txtPrice.setText("$" + String.format("%.2f",posts.get(position).getPrice()));

        return row ;
    }
}
