package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bookx.Adapter.UserAdapter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageList extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private UserAdapter userAdapter ;
    private Set<User> mUsers ;

    FirebaseUser fUser ;
    DatabaseReference reference ;

    private List<String> userList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        recyclerView = findViewById(R.id.rvMessageList) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser() ;
        userList = new ArrayList<>() ;
        reference = FirebaseDatabase.getInstance().getReference("chats") ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class) ;

                    if(chat.getSender().equals(fUser.getUid())){
                        userList.add(chat.getReceiver()) ;
                    }

                    if(chat.getReceiver().equals(fUser.getUid())){
                        userList.add(chat.getSender()) ;
                    }
                }

                readChats() ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }

    private void readChats(){
        mUsers = new HashSet<>() ;

        reference = FirebaseDatabase.getInstance().getReference("users") ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userid = snapshot.getKey() ;
                    User user = snapshot.getValue(User.class) ;

                    for(String id : userList){
                        if(id.equals(userid)){
                            mUsers.add(user) ;

                        }
                    }
                }
                List<User> mUsersList = new ArrayList<>(mUsers) ;
                userAdapter = new UserAdapter(getApplicationContext(),mUsersList) ;
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }
}
