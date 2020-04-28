package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookx.Adapter.MessageAdapter;
import com.example.bookx.Model.Chat;
import com.example.bookx.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    // local variables
    ImageView imgProfile ;
    TextView txtUsername ;
    ImageButton ibtSend ;
    EditText edtMessage ;
    MessageAdapter messageAdapter ;
    List<Chat> mChats ;

    RecyclerView recyclerView ;

    FirebaseUser mUser ;
    DatabaseReference mReference ;

    Intent intent ;
    ValueEventListener readListener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // UI initialization
        Toolbar toolbar = findViewById(R.id.toolBar) ;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.rvMessage) ;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imgProfile = findViewById(R.id.profileImage) ;
        txtUsername = findViewById(R.id.txtUsername) ;
        ibtSend = findViewById(R.id.ibtSendMessage) ;
        edtMessage = findViewById(R.id.edtSendMessage) ;

        intent = getIntent() ;
        final String userid = intent.getStringExtra("userid") ;

        mUser = FirebaseAuth.getInstance().getCurrentUser() ;

        ibtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edtMessage.getText().toString() ;
                if(!msg.equals("")){
                    sendMessage(mUser.getUid(), userid, msg);
                }else {
                    Toast.makeText(getApplicationContext(), "You can't send empty message", Toast.LENGTH_LONG).show();
                }
                 edtMessage.setText("");
            }
        });


        mReference = FirebaseDatabase.getInstance().getReference("users").child(userid) ;

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class) ;
                txtUsername.setText(user.getFullName());
                if(user.getImageurl().equals("default")){
                    imgProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(imgProfile) ;
                }

                readMessage(mUser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

        readMessage(userid);
    }

    // read messages from database
    private void readMessage(final String userid){
        mReference = FirebaseDatabase.getInstance().getReference("chats") ;
        readListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class) ;
                    if(chat.getReceiver().equals(mUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String , Object> map = new HashMap<>() ;
                        map.put("read",true) ;
                        snapshot.getRef().updateChildren(map) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        mChats = new ArrayList<>() ;

        mReference = FirebaseDatabase.getInstance().getReference("chats") ;
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class) ;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChats.add(chat) ;
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageurl) ;
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }

    // send message to database
    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference() ;

        HashMap<String, Object> messageMap = new HashMap<>() ;
        messageMap.put("sender",sender) ;
        messageMap.put("receiver", receiver) ;
        messageMap.put("message",message) ;
        messageMap.put("read",false) ;
        messageMap.put("sent",false) ;

        reference.child("chats").push().setValue(messageMap) ;
    }

    // remove listener on pause
    @Override
    protected void onPause() {
        super.onPause();
        mReference.removeEventListener(readListener);
    }
}
