package project.projectapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Date;
import java.util.Calendar;


public class NewNewsArticleActivity extends AppCompatActivity {

    private CircleImageView profileIcon;
    private EditText articleTitle, articleContent;
    private ImageView send;
    private TextView username;

    private Toolbar toolbar;

    private String name, newArticleTitle, newArticleContent, newArticlePosterUsername,
            newArticlePosterProfileImage, callingActivity, dayDate, dayTime;

    private Uri userImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_news_article);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profileIcon = findViewById(R.id.new_news_article_profile_icon);
        username = findViewById(R.id.new_news_article_username);
        articleTitle = findViewById(R.id.new_news_article_title);
        articleContent = findViewById(R.id.new_news_article_content);
        toolbar = findViewById(R.id.new_news_article_toolbar);
        send = findViewById(R.id.new_news_article_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createArticle();
            }
        });

        setSupportActionBar(toolbar);

        // Replace the usual back button with an 'x' such that it is more suitable for this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        getDateTime();
        setUserDetails();
    }

    private void createArticle() {
        newArticleTitle = articleTitle.getText().toString();
        newArticleContent = articleContent.getText().toString();
        newArticlePosterUsername = firebaseAuth.getCurrentUser().getDisplayName();

        userImage = firebaseAuth.getCurrentUser().getPhotoUrl();

        if(userImage != null){
            newArticlePosterProfileImage = userImage.toString();
        }

        // Gets the activity that called this one to place in the corresponding database column
        Intent previousIntent = getIntent();
        callingActivity = previousIntent.getStringExtra("activity");

        // Once all data is captured, gets sent to another method to check before adding to database
        verifyPost();
    }

    /**
     * Validation for the input the user has put in, must contain some data otherwise the post
     * get rejected, finally creates a news post object and stores into the database
     */
    private void verifyPost() {
        if(TextUtils.isEmpty(newArticleTitle)){
            articleTitle.setError("Your post requires a title!");
            articleTitle.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(newArticleContent)){
            articleContent.setError("Your post requires a some content!");
            articleContent.requestFocus();
            return;
        }

        // Pass in the calling activity such that official and team news are separated
        databaseReference = FirebaseDatabase.getInstance().getReference(callingActivity);

        String postId = databaseReference.push().getKey();

        NewsPost newsPost = new NewsPost(postId, newArticleTitle, newArticleContent,
                newArticlePosterUsername, newArticlePosterProfileImage, dayDate, dayTime);

        databaseReference.child(postId).setValue(newsPost);

        // Close the activity
        finish();
    }

    /**
     * Retrieves a long string containing the current date, time and year, then splits this data
     * up into separate strings 
     */
    private void getDateTime() {
        String todayDateTime = String.valueOf(Calendar.getInstance().getTime());

        // Calendar.getInstance().getTime() returns a long string of various data for today, split and access what we need
        String[] splitTime = todayDateTime.split(" ");

        dayDate = splitTime[0] + " " + splitTime[1] + " " + splitTime[2] + splitTime[5];
        dayTime = splitTime[3];
    }

    /**
     * places the username and profile image for the user into areas on the layout
     */
    public void setUserDetails(){
        name = firebaseUser.getDisplayName();
        username.setText(name);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(profileIcon);
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
