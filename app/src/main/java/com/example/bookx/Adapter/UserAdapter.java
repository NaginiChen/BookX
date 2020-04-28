package com.example.bookx.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookx.MessageActivity;
import com.example.bookx.Model.Chat;
import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext ;
    private List<User> mUsers ;

    FirebaseUser fUser ;
    DatabaseReference reference  ;

    private String lastMsg ;

    public UserAdapter(Context context, List<User> mUser){
        this.mContext = context ;
        this.mUsers = mUser ;
    }
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false) ;
        return new UserAdapter.ViewHolder(view) ;
    }

    // holder for each single user
    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position) ;

        holder.txtUsername.setText(user.getFullName());

        if(user.getImageurl().equals("default")){
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImageurl()).into(holder.imgProfile) ;
        }
        reference = FirebaseDatabase.getInstance().getReference("users") ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userid = snapshot.getKey() ;
                    User temp = snapshot.getValue(User.class) ;
                    if(temp.getEmail().equals(user.getEmail())){
                        lastMessage(userid,holder.txtLastMsg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

        // onclick for each user list item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(mContext,MessageActivity.class) ;
                reference = FirebaseDatabase.getInstance().getReference("users") ;

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String userid = snapshot.getKey() ;
                            User temp = snapshot.getValue(User.class) ;
                            if(temp.getEmail().equals(user.getEmail())){
                                intent.putExtra("userid",userid) ;
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                                reference.removeEventListener(this);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                }) ;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // UI initialization for each user item
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtUsername ;
        public ImageView imgProfile ;
        public TextView txtLastMsg ;


        public ViewHolder(View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername) ;
            imgProfile = itemView.findViewById(R.id.profileImage) ;
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg) ;
        }
    }

    // retrieve the latest message from chat
    private void lastMessage(final String userid, final TextView txtLastMsg){
        lastMsg = "default" ;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats") ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class) ;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) || chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid)){
                        lastMsg = chat.getMessage() ;
                    }
                }

                switch (lastMsg){
                    case "default":
                        txtLastMsg.setText("");
                        break;
                    default:
                        txtLastMsg.setText(lastMsg);
                        break;
                }

                lastMsg = "default" ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;


    }
}
