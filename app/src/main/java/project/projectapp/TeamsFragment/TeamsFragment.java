package project.projectapp.TeamsFragment;

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
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.R;

public class TeamsFragment extends Fragment {

    private ProgressBar progressBar;

    private ArrayList<String> names, imageJpg;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.teams_fragment, container, false);

        progressBar = view.findViewById(R.id.progress_bar_teams);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        names = new ArrayList<>();
        imageJpg = new ArrayList<>();

        addTeamAndLogo(view);
        return view;
    }

    private void addTeamAndLogo(View view){
        final View VIEW = view;
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    names.add(data.getKey());
                    recyclerViewSetup(VIEW);
                    for(DataSnapshot getLogo : data.getChildren()){
                        if(getLogo.getKey().equals("Logo")){
                            imageJpg.add(getLogo.getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_teams);
        TeamRecyclerViewAdapter adapter = new TeamRecyclerViewAdapter(getContext(), names,
                imageJpg);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //The recyclerview is done loading and we can now hide the progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }
}
