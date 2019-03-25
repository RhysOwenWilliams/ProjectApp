package project.projectapp.GamesFragment.GameLive;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.GamesFragment.GameResult.TeamBreakdownRecyclerViewAdapter;
import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamDetailsMapsLocation;

public class Team1RosterScore extends Fragment {

    private final String GAME_ID = "gameIds";
    private final String TEAM_1_PLAYERS = "team1Players";

    private String gameId;

    private ArrayList<String> teamPlayers, teamNumbers, teamPlayerScores, teamFouls;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_1_live_fragment, container, false);

        teamPlayers = new ArrayList<>();
        teamNumbers = new ArrayList<>();
        teamPlayerScores = new ArrayList<>();
        teamFouls = new ArrayList<>();

        setTeamData(view);
        return view;
    }

    private void setTeamData(final View view) {
        gameId = getActivity().getIntent().getStringExtra(GAME_ID);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamPlayerScores.clear();
                teamFouls.clear();
                for(DataSnapshot games : dataSnapshot.getChildren()){
                    for(DataSnapshot perGame : games.getChildren()){
                        if(perGame.getKey().equals("GameId")){
                            if(perGame.getValue().toString().equals(gameId)){
                                getTeamData(games, "Team 1", teamPlayers, teamNumbers,
                                        teamPlayerScores, teamFouls);
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

    private void getTeamData(DataSnapshot games, String team, ArrayList<String> playerArray,
                             ArrayList<String> numberArray, ArrayList<String> playerScoreArray,
                             ArrayList<String> foulsArray) {
        for(DataSnapshot getTeamData : games.getChildren()){
            if(getTeamData.getKey().equals(team)){
                for(DataSnapshot teamData : getTeamData.getChildren()){
                    if(teamData.getKey().equals("Players")){
                        for(DataSnapshot players : teamData.getChildren()){
                            getPlayerData(players, playerArray, numberArray, playerScoreArray,
                                    foulsArray);
                        }
                    }
                }
            }
        }
    }

    private void getPlayerData(DataSnapshot players, ArrayList<String> playerArray,
                               ArrayList<String> numberArray, ArrayList<String> playerScoreArray,
                               ArrayList<String> foulsArray) {
        for(DataSnapshot playerData : players.getChildren()){
            if(playerData.getKey().equals("name")){
                playerArray.add(playerData.getValue().toString());
            } else if(playerData.getKey().equals("number")){
                numberArray.add(playerData.getValue().toString());
            } else {
                for(DataSnapshot playerScoreFouls : playerData.getChildren()){
                    if(playerScoreFouls.getKey().equals("1-pointers")){
                        playerScoreArray.add(playerScoreFouls.getValue().toString());
                    }
                    if(playerScoreFouls.getKey().equals("2-pointers")){
                        playerScoreArray.add(String.valueOf(Integer
                                .valueOf(playerScoreFouls.getValue().toString())*2));
                    }
                    if(playerScoreFouls.getKey().equals("3-pointers")){
                        playerScoreArray.add(String.valueOf(Integer
                                .valueOf(playerScoreFouls.getValue().toString())*3));
                    }
                    if(playerScoreFouls.getKey().equals("fouls")){
                        foulsArray.add(playerScoreFouls.getValue().toString());
                        Log.d("scores", playerScoreFouls.getValue().toString());
                    }
                }
            }
        }

        formatPlayerScores(playerScoreArray);
    }

    private void formatPlayerScores(ArrayList<String> teamScore) {
        ArrayList<String> scores = new ArrayList<>();

        for(int i = 0; i < teamScore.size(); i+=3){
            int score = Integer.valueOf(teamScore.get(i))
                        + Integer.valueOf(teamScore.get(i+1))
                        + Integer.valueOf(teamScore.get(i+2));

            scores.add(String.valueOf(score));
        }

        teamPlayerScores = scores;
    }

    /**
     * Away team
     */
    private void recyclerViewSetup(View view) {
        //Log.d("scores", teamPlayerScores.toString());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_live_game_team_1);
        TeamBreakdownRecyclerViewAdapter adapter = new TeamBreakdownRecyclerViewAdapter(getActivity(),
                teamPlayers, teamNumbers, teamPlayerScores, teamFouls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}