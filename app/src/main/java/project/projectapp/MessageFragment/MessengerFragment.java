package project.projectapp.MessageFragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import project.projectapp.NewsFragment.Comments.CommentsRecyclerViewAdapter;
import project.projectapp.R;

public class MessengerFragment extends Fragment {

    private Button checkCode;
    private EditText enteredCode, messageContent;
    private ImageButton sendMessage, leaveChat;
    private LinearLayout isInRoom, notInRoom, customToolbar, addMessage;
    private ProgressBar progressBar;
    private TextView roomName;

    private Boolean inRoom, roomFound, checkRoom;
    private String roomCode, dayTime, dayDate;

    private ArrayList<String> messageBody, messageDate, messageTime, messageSender, messageSenderId;

    private DatabaseReference databaseReference, databaseReferenceCode;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.messenger_fragment, container, false);

        checkCode = view.findViewById(R.id.messenger_check_code);
        enteredCode = view.findViewById(R.id.messenger_user_entered_code);
        messageContent = view.findViewById(R.id.messenger_message);
        sendMessage = view.findViewById(R.id.messenger_send);
        leaveChat = view.findViewById(R.id.messenger_leave_chat);
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

        firebaseAuth = FirebaseAuth.getInstance();
        authenticateUser(view);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMessage();
            }
        });

        leaveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUserFromChat();
            }
        });

        return view;
    }

    private void removeUserFromChat() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger")
                .child(roomCode)
                .child("userList")
                .child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.removeValue();

        refreshFragment();
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
                if(signedInId.hasChild("isOfficial") || signedInId.hasChild("isTeam")){
                    leaveChat.setVisibility(View.INVISIBLE);
                }
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
                                        roomCode = chatRooms.getKey();
                                    }
                                }
                            }
                        }
                    }
                }
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
        customToolbar.setVisibility(View.INVISIBLE);
        addMessage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        checkCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = enteredCode.getText().toString();
                checkIfValid(code);
            }
        });
    }

    private void checkIfValid(final String code) {
        databaseReferenceCode = FirebaseDatabase.getInstance()
                .getReference("Messenger");

        databaseReferenceCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chatRooms : dataSnapshot.getChildren()){
                    if(!roomFound){
                        if(chatRooms.getKey().equals(code)){
                            roomFound = true;
                            getThisChatDetails(chatRooms.getKey());
                        }
                    }
                }

                // If no room with matching code is found after checking all codes
                if(!roomFound){
                    enteredCode.setError("Invalid code");
                    enteredCode.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getThisChatDetails(String chatRooms) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger")
                .child(chatRooms)
                .child("userList");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .setValue(firebaseAuth.getCurrentUser().getDisplayName());

        refreshFragment();
    }

    private void clearMessageArrays(){
        messageBody.clear();
        messageTime.clear();
        messageDate.clear();
        messageSender.clear();
        messageSenderId.clear();
    }

    private void userIsInAChatRoom(DataSnapshot chatRoom, final View view){
        isInRoom.setVisibility(View.VISIBLE);
        notInRoom.setVisibility(View.INVISIBLE);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger").child(chatRoom.getKey());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearMessageArrays();
                for(DataSnapshot chatRoomDetails : dataSnapshot.getChildren()){
                    if(chatRoomDetails.getKey().equals("roomName")){
                        roomName.setText(chatRoomDetails.getValue().toString());
                    }
                    if(chatRoomDetails.getKey().equals("messages")){
                        for(DataSnapshot message : chatRoomDetails.getChildren()){
                            for(DataSnapshot messageDetails : message.getChildren()){
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
                    }
                }
                recyclerViewSetup(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewMessage() {
        getDateTime();

        String messageBody = messageContent.getText().toString();
        String messageSender = firebaseAuth.getCurrentUser().getDisplayName();
        String messageSenderId = firebaseAuth.getCurrentUser().getUid();

        if(TextUtils.isEmpty(messageBody)){
            messageContent.setError("Your message can not be empty");
            messageContent.requestFocus();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Messenger")
                .child(roomCode)
                .child("messages");

        String messageId = databaseReference.push().getKey();

        Message message = new Message(messageBody, dayTime, dayDate, messageSender, messageSenderId);

        databaseReference.child(messageId).setValue(message);

        messageContent.onEditorAction(EditorInfo.IME_ACTION_DONE);
        messageContent.setText("");
    }

    /**
     * Retrieves a long string containing the current date, time and year, then splits this data
     * up into separate strings
     */
    private void getDateTime() {
        String todayDateTime = String.valueOf(Calendar.getInstance().getTime());

        // Calendar.getInstance().getTime() returns a long string of various data for today, split and access what we need
        String[] splitTime = todayDateTime.split(" ");

        dayDate = splitTime[1] + " " + splitTime[2] + " " + splitTime[5]; // Month, Day, Year
        dayTime = splitTime[3];
    }

    private void refreshFragment(){
        inRoom = false;
        roomFound = false;
        checkRoom = false;

        getFragmentManager().beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_messages);
        MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter(getContext(), messageBody, messageDate, messageTime, messageSender, messageSenderId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.scrollToPosition(adapter.getItemCount()-1); //Start at bottom

        customToolbar.setVisibility(View.VISIBLE);
        addMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
