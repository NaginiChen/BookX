package com.example.bookx.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookx.Model.Chat;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0 ;
    public static final int MSG_TYPE_RIGHT = 1 ;

    private Context mCcontext ;
    private List<Chat> mChats ;
    private String imageurl ;

    FirebaseUser mUser ;

    public MessageAdapter(Context context, List<Chat> mChats, String imageurl){
        this.mCcontext = context ;
        this.mChats = mChats ;
        this.imageurl = imageurl ;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if(viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(mCcontext).inflate(R.layout.chat_item_right,parent,false) ;
        }else
            view = LayoutInflater.from(mCcontext).inflate(R.layout.chat_item_left,parent,false) ;
        return new MessageAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position) ;

        holder.txtShowMessage.setText(chat.getMessage());

        if(imageurl.equals("default")){
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mCcontext).load(imageurl).into(holder.imgProfile) ;
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtShowMessage ;
        public ImageView imgProfile ;


        public ViewHolder(View itemView) {
            super(itemView);

            txtShowMessage = itemView.findViewById(R.id.txtShowMessage) ;
            imgProfile = itemView.findViewById(R.id.profileImage) ;
        }
    }

    @Override
    public int getItemViewType(int postion){
        mUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(mChats.get(postion).getSender().equals(mUser.getUid())){
            return MSG_TYPE_RIGHT ;
        }else{
            return MSG_TYPE_LEFT ;
        }
    }
}
