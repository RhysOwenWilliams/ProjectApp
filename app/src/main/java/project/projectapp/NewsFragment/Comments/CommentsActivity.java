package project.projectapp.NewsFragment.Comments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.NewsFragment.NewsRecyclerViewAdapter;
import project.projectapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;


public class CommentsActivity extends AppCompatActivity {

    private final String[] DATA_NAMES
            = {"username", "userProfileImage", "commentText", "commentDate", "commentTime"};

    private ImageButton addComment;
    private EditText commentText;
    private Toolbar toolbar;

    private String username, userComment, userProfileImage, commentTime, commentDate, postId;

    private ArrayList<ArrayList> myArrays; // Will be used to add the comment data to the below arrays
    private ArrayList<String> usernames, userComments, userProfileImages, commentTimes, commentDates,
        postIds;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        myArrays = new ArrayList<>();
        usernames = new ArrayList<>();
        userComments = new ArrayList<>();
        userProfileImages = new ArrayList<>();
        commentTimes = new ArrayList<>();
        commentDates = new ArrayList<>();
        postIds = new ArrayList<>();

        addComment = findViewById(R.id.comments_send);
        commentText = findViewById(R.id.comments_content);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserCommentAndId();

                commentText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                commentText.setText("");
            }
        });


        toolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(toolbar);

        // Replace the usual back button with an 'x' such that it is more suitable for this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        retrieveCommentsForThisPost();
    }


    private void getUserCommentAndId() {
        postId = getIntent().getStringExtra("callingPost");
        userComment = commentText.getText().toString();
        username = firebaseAuth.getCurrentUser().getDisplayName();
        Uri image = firebaseAuth.getCurrentUser().getPhotoUrl();

        if(image != null){
            userProfileImage = image.toString();
        }

        getDateTime();
        verifyComment();
    }

    private void verifyComment() {
        if(TextUtils.isEmpty(userComment)){
            commentText.setError("Your comment can not be empty");
            commentText.requestFocus();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Comments")
                .child(postId);

        String commentId = databaseReference.push().getKey();

        Comment comment = new Comment(username, userProfileImage, userComment, commentDate,
                commentTime);

        databaseReference.child(commentId).setValue(comment);
    }

    /**
     * Retrieves a long string containing the current date, time and year, then splits this data
     * up into separate strings
     */
    private void getDateTime() {
        String todayDateTime = String.valueOf(Calendar.getInstance().getTime());

        // Calendar.getInstance().getTime() returns a long string of various data for today, split and access what we need
        String[] splitTime = todayDateTime.split(" ");

        commentDate = splitTime[1] + " " + splitTime[2] + " " + splitTime[5]; // Month, Day, Year
        commentTime = splitTime[3];
    }

    private void retrieveCommentsForThisPost(){
        postId = getIntent().getStringExtra("callingPost");

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernames.clear();
                userProfileImages.clear();
                userComments.clear();
                commentDates.clear();
                commentTimes.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getKey().equals(postId)){
                        getCommentData(data);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getCommentData(DataSnapshot data) {
        myArrays.add(usernames);
        myArrays.add(userProfileImages);
        myArrays.add(userComments);
        myArrays.add(commentDates);
        myArrays.add(commentTimes);

        for(DataSnapshot comments : data.getChildren()){
            for(DataSnapshot commentData : comments.getChildren()){
                int i = 0;
                for(String s : DATA_NAMES){
                    if(commentData.getKey().equals(s)){
                        myArrays.get(i).add(commentData.getValue().toString());
                    }
                    i++;
                }
            }
        }

        recyclerViewSetup();
    }

    private void recyclerViewSetup(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_comments);
        CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(this, usernames,
                userProfileImages, userComments, commentDates, commentTimes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }


    /**
     * Called when the user clicks the 'x', only used for a returning animation
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_from_top);
        return false;
    }

    /**
     * Called when the user clicks the back button on their device, used for returning animation
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_from_top);
        super.onBackPressed();
    }
}
