package project.projectapp.MessageFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import project.projectapp.NewsFragment.Comments.CommentsRecyclerViewAdapter;
import project.projectapp.R;

public class MessengerFragment extends Fragment {

    private Button checkCode;
    private EditText enteredCode;
    private LinearLayout isInRoom, notInRoom, customToolbar, addMessage;
    private ProgressBar progressBar;
    private TextView roomName;

    private Boolean inRoom, roomFound, checkRoom;

    private ArrayList<String> messageBody, messageDate, messageTime, messageSender, messageSenderId;
    private Set<String> mBody;

    private DataSnapshot whichRoom;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.messenger_fragment, container, false);

        checkCode = view.findViewById(R.id.messenger_check_code);
        enteredCode = view.findViewById(R.id.messenger_user_entered_code);
        isInRoom = view.findViewById(R.id.messenger_is_in_room);
        notInRoom = view.findViewById(R.id.messenger_not_in_room);
        customToolbar = view.findViewById(R.id.messenger_toolbar_custom);
        addMessage = view.findViewById(R.id.messenger_add_message);
        progressBar = view.findViewById(R.id.messenger_progress_bar);
        roomName = view.findViewById(R.id.messenger_room_name);

        inRoom = false;
        roomFound = false;
        checkRoom = false;

        messageBody = new ArrayList<>();
        messageDate = new ArrayList<>();
        messageTime = new ArrayList<>();
        messageSender = new ArrayList<>();
        messageSenderId = new ArrayList<>();

        mBody = new HashSet<>();

        firebaseAuth = FirebaseAuth.getInstance();
        authenticateUser(view);

        return view;
    }

    private void authenticateUser(final View view) {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Roles");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot signedInId = dataSnapshot.child(firebaseAuth.getUid());
                getChatRoom(signedInId.getKey().toString(), view);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getChatRoom(final String signedInUser, final View view) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chatRooms : dataSnapshot.getChildren()){
                    for(DataSnapshot perRoom : chatRooms.getChildren()){
                        if(perRoom.getKey().equals("userList")){
                            for(DataSnapshot users : perRoom.getChildren()){
                                checkIfUserIsInRoom(users, signedInUser);
                                if(checkRoom){
                                    if(users.getKey().equals(signedInUser)){
                                        userIsInAChatRoom(chatRooms, view);
                                        whichRoom = chatRooms;
                                    }
                                }
                            }
                        }
                    }
                }
                getMessages(whichRoom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfUserIsInRoom(DataSnapshot users, final String signedInUser) {
        if(!inRoom){
            if(users.getKey().equals(signedInUser)){
                inRoom = true;
                checkRoom = true;
            } else {
                userNotInAChatRoom();
            }
        }
    }

    private void userNotInAChatRoom(){
        isInRoom.setVisibility(View.INVISIBLE);
        notInRoom.setVisibility(View.VISIBLE);

        checkCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = enteredCode.getText().toString();
                checkIfValid(code);
            }
        });
    }

    private void checkIfValid(final String code) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chatRooms : dataSnapshot.getChildren()){
                    if(!roomFound){
                        if(chatRooms.getKey().equals(code)){
                            roomFound = true;
                            getThisChatDetails(chatRooms);
                        } else {
                            enteredCode.setError("Invalid code");
                            enteredCode.requestFocus();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getThisChatDetails(DataSnapshot chatRooms) {
        for(DataSnapshot perChat : chatRooms.getChildren()){
            if(perChat.getKey().equals("roomName")){
                Toast.makeText(getActivity(), "Welcome to chat room for: " + perChat.getValue().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void userIsInAChatRoom(DataSnapshot chatRoom, final View view){
        isInRoom.setVisibility(View.VISIBLE);
        notInRoom.setVisibility(View.INVISIBLE);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger").child(chatRoom.getKey());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chatRoomDetails : dataSnapshot.getChildren()){
                    if(chatRoomDetails.getKey().equals("roomName")){
                        roomName.setText(chatRoomDetails.getValue().toString());
                    }
                }
                recyclerViewSetup(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO: bug here, when messenger fragment is reloaded, data is lost
    private void getMessages(DataSnapshot chatRoom){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger")
                .child(chatRoom.getKey())
                .child("messages");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot messageDetails : dataSnapshot.getChildren()){
                    if(messageDetails.getKey().equals("body")){
                        messageBody.add(messageDetails.getValue().toString());
                    }
                    if(messageDetails.getKey().equals("date")){
                        messageDate.add(messageDetails.getValue().toString());
                    }
                    if(messageDetails.getKey().equals("time")){
                        messageTime.add(messageDetails.getValue().toString());
                    }
                    if(messageDetails.getKey().equals("user")){
                        messageSender.add(messageDetails.getValue().toString());
                    }
                    if(messageDetails.getKey().equals("userId")){
                        messageSenderId.add(messageDetails.getValue().toString());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_messages);
        MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter(getContext(), messageBody, messageDate, messageTime, messageSender, messageSenderId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        customToolbar.setVisibility(View.VISIBLE);
        addMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
