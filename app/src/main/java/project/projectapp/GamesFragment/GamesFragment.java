package project.projectapp.GamesFragment;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamRecyclerViewAdapter;

//TODO: access the games section of the database and load them into the recycler view
//TODO: based on the name of the team, search the teams section for additional data
public class GamesFragment extends Fragment {

    private String team1, team2, gameDate, gameTime, gameType, gameLocation, team1Score, team2Score;

    private ArrayList<String> gameDates, gameTimes, gameTypes, gameLocations,team1Nicknames,
            team2Nicknames, team1Scores, team2Scores, team1Wins, team1Losses, team2Wins, team2Losses;

    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_fragment, container, false);

        gameDates = new ArrayList<>();
        gameTimes = new ArrayList<>();
        gameTypes = new ArrayList<>();
        gameLocations = new ArrayList<>();
        team1Nicknames = new ArrayList<>();
        team2Nicknames = new ArrayList<>();
        team1Scores = new ArrayList<>();
        team2Scores = new ArrayList<>();
        team1Wins = new ArrayList<>();
        team1Losses = new ArrayList<>();
        team2Wins = new ArrayList<>();
        team2Losses = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar_games);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        getGames(view);
        return view;
    }

    private void getGames(final View view){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    getData(data);
                    getTeamNickname(team1, team2, view);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /**
     * Separates the database data and stores them into strings
     * @param dataSnapshot
     */
    private void getData(DataSnapshot dataSnapshot){
        String data = String.valueOf(dataSnapshot.getValue());
        String removeCurlyBrackets = data.replace("{", "")
                .replace("}", "");
        String[] splitData = removeCurlyBrackets.split(",");

        gameTime = removeEquals(splitData[0]);
        gameType = removeEquals(splitData[1]);
        team1Score = removeEquals(splitData[2]);
        team2Score = removeEquals(splitData[3]);
        team1 = removeEquals(splitData[4]);
        team2 = removeEquals(splitData[5]);
        gameDate = removeEquals(splitData[6]);
        gameLocation = formatLocation(removeEquals(splitData[7]));

        gameTimes.add(gameTime);
        gameTypes.add(gameType);
        team1Scores.add(team1Score);
        team2Scores.add(team2Score);
        gameDates.add(gameDate);
        gameLocations.add(gameLocation);
    }

    private void getTeamNickname(final String team1, final String team2, final View view) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams").child(team1);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getKey().equals("Nickname")){
                        team1Nicknames.add(data.getValue().toString());
                    }
                    if(data.getKey().equals("Wins")){
                        team1Wins.add(data.getValue().toString());
                    }
                    if(data.getKey().equals("Loss")){
                        team1Losses.add(data.getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams").child(team2);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getKey().equals("Nickname")){
                        team2Nicknames.add(data.getValue().toString());
                    }
                    if(data.getKey().equals("Wins")){
                        team2Wins.add(data.getValue().toString());
                    }
                    if(data.getKey().equals("Loss")){
                        team2Losses.add(data.getValue().toString());
                    }
                }
                recyclerViewSetup(view);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private String removeEquals(String data){
        String[] noEquals = data.split("=");
        return noEquals[1];
    }

    private String formatLocation(String data){
        String replaceSemiColon = data.replace(";", ",");
        return replaceSemiColon;
    }


    private void recyclerViewSetup(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_games);
        GamesRecyclerViewAdapter adapter = new GamesRecyclerViewAdapter(getContext(), team1Nicknames,
                team2Nicknames, gameDates, gameTimes, gameTypes, gameLocations, team1Scores,
                team2Scores, team1Wins, team1Losses, team2Wins, team2Losses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //The recyclerview is done loading and we can now hide the progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    private int monthNameToValue(String month){
        switch(month){
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
        }
        return 0;
    }

}
