package project.projectapp.NewsFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.projectapp.NewNewsArticleActivity;
import project.projectapp.R;

public class OfficialNewsTabFragment extends Fragment {

    private FloatingActionButton addNewsArticle;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.official_news_tab_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        addNewsArticle = view.findViewById(R.id.official_news_floating_action_button);
        progressBar = view.findViewById(R.id.official_news_progress_bar);

        authenticateUser();

        return view;
    }

    /**
     * Checks if the current signed in user is an official and if so, allows them further access to
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
                if(signedInId.hasChild("isOfficial")){
                    DataSnapshot data = signedInId.child("isOfficial");
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
        newArticle.putExtra("activity", "Official News");
        startActivity(newArticle);
        ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.slide_out_from_bottom);
    }
}
