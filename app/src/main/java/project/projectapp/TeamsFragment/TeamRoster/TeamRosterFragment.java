package project.projectapp.TeamsFragment.TeamRoster;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.R;

public class TeamRosterFragment extends Fragment {

    private static final String TEAM_NAME = "teamName";

    private String teamName;

    private ArrayList<String> playerNames, playerNumbers, playerPositions;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_roster_fragment, container, false);

        teamName = getActivity().getIntent().getStringExtra(TEAM_NAME);

        playerNames = new ArrayList<>();
        playerNumbers = new ArrayList<>();
        playerPositions = new ArrayList<>();

        getRoster(view);
        return view;
    }

    private void getRoster(final View view){
        final ArrayList<String> myData = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Roster").child(teamName);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    for(DataSnapshot playerData : data.getChildren()){
                        myData.add(playerData.getValue().toString());
                    }
                }

                for(int i = 0; i < myData.size(); i+=3){
                    playerNames.add(myData.get(i));
                    playerNumbers.add(myData.get(i+1));
                    playerPositions.add(myData.get(i+2));
                }

                recyclerViewSetup(view);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_roster);
        RosterRecyclerViewAdapter adapter = new RosterRecyclerViewAdapter(getContext(), playerNames,
                playerNumbers, playerPositions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
}