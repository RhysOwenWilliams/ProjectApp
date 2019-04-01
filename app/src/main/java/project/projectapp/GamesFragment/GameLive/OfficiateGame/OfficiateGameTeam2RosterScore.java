package project.projectapp.GamesFragment.GameLive.OfficiateGame;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.GamesFragment.GameLive.OfficiateGame.OfficiateGameRecyclerViewAdapter;
import project.projectapp.R;

public class OfficiateGameTeam2RosterScore extends Fragment {


    private final String GAME_ID = "gameIds";

    private String gameId, quarter;

    private ArrayList<String> playerNames, playerNumbers, onePointers, twoPointers, threePointers,
            fouls;

    private DatabaseReference databaseReference, databaseReferenceTotalScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.officiate_game_team_2_fragment, container, false);

        playerNames = new ArrayList<>();
        playerNumbers = new ArrayList<>();
        onePointers = new ArrayList<>();
        twoPointers = new ArrayList<>();
        threePointers = new ArrayList<>();
        fouls = new ArrayList<>();

        getPlayerData(view);
        return view;
    }

    private void getPlayerData(final View view) {
        gameId = getActivity().getIntent().getStringExtra(GAME_ID);
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearScoresFoulsArrays();
                for(DataSnapshot games : dataSnapshot.getChildren()){
                    for(DataSnapshot perGame : games.getChildren()){
                        if(perGame.getKey().equals("GameId")){
                            if(perGame.getValue().toString().equals(gameId)){
                                getPlayers(games, "Team 2");
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

    private void clearScoresFoulsArrays() {
        onePointers.clear();
        twoPointers.clear();
        threePointers.clear();
        fouls.clear();
    }

    private void getPlayers(DataSnapshot games, String whichTeam) {
        for(DataSnapshot getTeamData : games.getChildren()){
            if(getTeamData.getKey().equals(whichTeam)){
                for(DataSnapshot teamData : getTeamData.getChildren()){
                    if(teamData.getKey().equals("Players")){
                        if(whichTeam == "Team 2") {
                            addPlayersToArray(teamData);
                        }
                    }
                }
            }
        }
    }

    private void addPlayersToArray(DataSnapshot teamData) {
        int score = 0;
        for(DataSnapshot perPlayer : teamData.getChildren()){
            for(DataSnapshot players : perPlayer.getChildren()){
                if(players.getKey().equals("name")){
                    playerNames.add(players.getValue().toString());
                }
                if(players.getKey().equals("number")) {
                    playerNumbers.add(players.getValue().toString());
                }
                if(players.getKey().equals("data")){
                    for(DataSnapshot scoresFouls : players.getChildren()){
                        if(scoresFouls.getKey().equals("1-pointers")){
                            onePointers.add(scoresFouls.getValue().toString());
                            score += Integer.valueOf(scoresFouls.getValue().toString());
                        }
                        if(scoresFouls.getKey().equals("2-pointers")){
                            twoPointers.add(scoresFouls.getValue().toString());
                            score += Integer.valueOf(scoresFouls.getValue().toString())*2;
                        }
                        if(scoresFouls.getKey().equals("3-pointers")){
                            threePointers.add(scoresFouls.getValue().toString());
                            score += Integer.valueOf(scoresFouls.getValue().toString())*3;
                        }
                        if(scoresFouls.getKey().equals("fouls")){
                            fouls.add(scoresFouls.getValue().toString());
                        }
                    }
                }
            }
        }
        if(quarter != "Total"){
            getPreviousQuarterScore(score);
        }
    }

    private void getPreviousQuarterScore(final int score){
        databaseReferenceTotalScore = FirebaseDatabase.getInstance()
                .getReference("Live").child("Game "+gameId).child("Team2QuarterScore");

        databaseReferenceTotalScore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int quarterScore = 0;
                if(dataSnapshot.getValue() != null){
                    quarterScore = Integer.valueOf(dataSnapshot.getValue().toString());
                }
                setQuarterScore(score, quarterScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setQuarterScore(final int score, final int prevQuarterScore){
        int finalScore = score;
        int currentQuarterScore = score - prevQuarterScore;

        getCurrentQuarter();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");
        if(quarter != null){
            if(quarter == "Not Started" || quarter != "Total"){
                databaseReference.child("Game "+gameId)
                        .child("Team 2")
                        .child("Score")
                        .child(quarter)
                        .setValue(currentQuarterScore);
                databaseReference.child("Game "+gameId)
                        .child("Team 2")
                        .child("Score")
                        .child("Total")
                        .setValue(finalScore);
            }
        }
    }

    private void getCurrentQuarter(){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child("Game "+gameId)
                .child("CurrentQuarter");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quarter = dataSnapshot.getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.officiate_game_team_2_recycler_view);
        OfficiateGameRecyclerViewAdapter adapter =
                new OfficiateGameRecyclerViewAdapter(getContext(), playerNames, playerNumbers,
                        onePointers, twoPointers, threePointers, fouls, gameId, "Team 2");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}