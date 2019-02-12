package project.projectapp.NewsFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import project.projectapp.R;

public class TeamNewsTabFragment extends Fragment {

    private FloatingActionButton addNewsArticle;
    private ProgressBar progressBar;

    private String profileImage, profileUsername, postDate, postTime, postTitle, postContent;

    private ArrayList<String> images, usernames, dates, times, titles, contents;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_news_tab_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        images = new ArrayList<>();
        usernames = new ArrayList<>();
        dates = new ArrayList<>();
        times = new ArrayList<>();
        titles = new ArrayList<>();
        contents = new ArrayList<>();

        addNewsArticle = view.findViewById(R.id.team_news_floating_action_button);
        progressBar = view.findViewById(R.id.team_news_progress_bar);

        authenticateUser();
        retrieveOfficialArticles(view);

        return view;
    }

    /**
     * Accesses the database for the official news posts and stores the data into arrays, ready
     * to be shown in the recycler view
     */
    private void retrieveOfficialArticles(final View view) {
        final View VIEW = view;

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Team News");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Used because otherwise, already existing database articles will duplicate
                dates.clear();
                images.clear();
                times.clear();
                titles.clear();
                contents.clear();
                usernames.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()){

                    String postDetails = data.getValue().toString();
                    getPostDetails(postDetails);

                    dates.add(postDate);
                    images.add(profileImage);
                    times.add(postTime);
                    titles.add(postTitle);
                    contents.add(postContent);
                    usernames.add(profileUsername);
                }

                reverseOrderOfArrays();

                recyclerViewSetup(VIEW);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getPostDetails(String postDetails){
        String[] splitPost = postDetails.replace("{", "")
                .replace("}","")
                .split(",");

        postDate = structureContent(splitPost[0]);
        profileImage = structureContent(splitPost[1]);
        postTime = structureContent(splitPost[2]);
        postTitle = structureContent(splitPost[3]);
        postContent = structureContent(splitPost[4]);
        profileUsername = structureContent(splitPost[5]);
    }

    /**
     * Used to remove all unwanted data from the database since we only need to get the data and
     * not the column
     * @param removeUnwanted a string we want to format
     * @return formatted string
     */
    private String structureContent(String removeUnwanted){
        String[] structure = removeUnwanted.split("=", 2);
        return structure[1];
    }

    /**
     * This is used such that the articles appear in order of newest at the top and oldest at the
     * bottom
     */
    private void reverseOrderOfArrays() {
        Collections.reverse(dates);
        Collections.reverse(images);
        Collections.reverse(times);
        Collections.reverse(titles);
        Collections.reverse(contents);
        Collections.reverse(usernames);
    }

    /**
     * Checks if the current signed in user is a team and if so, allows them further access to
     * create a new post
     */
    private void authenticateUser() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Roles");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Hide the progress bar once the database data can be accessed
                progressBar.setVisibility(View.INVISIBLE);

                DataSnapshot signedInId = dataSnapshot.child(firebaseAuth.getUid());

                if(signedInId.hasChild("isTeam")){
                    DataSnapshot data = signedInId.child("isTeam");
                    Boolean roleValue = Boolean.valueOf(data.getValue().toString());

                    if(roleValue){
                        addNewsArticle.setVisibility(View.VISIBLE);
                        addNewsArticle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openNewNewsArticleActivity();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void openNewNewsArticleActivity() {
        Intent newArticle = new Intent(getActivity(), NewNewsArticleActivity.class);
        newArticle.putExtra("activity", "Team News");
        startActivity(newArticle);
        ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.slide_out_from_bottom);
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_news_teams);
        NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(getContext(), images,
                usernames, dates, times, titles, contents);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
}
