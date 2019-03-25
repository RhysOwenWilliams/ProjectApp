package project.projectapp.GamesFragment.GameResult;

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

public class Team1ResultFragment extends Fragment {

    private final String GAME_ID = "gameIds";

    private String gameId;

    private ArrayList<String> team1Players, team1Numbers, team1PlayerScores, team1Fouls;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_1_result_fragment, container, false);
        team1Players = new ArrayList<>();
        team1Numbers = new ArrayList<>();
        team1PlayerScores = new ArrayList<>();
        team1Fouls = new ArrayList<>();

        getData(view);
        return view;
    }


    private void getData(final View view) {
        gameId = getActivity().getIntent().getStringExtra(GAME_ID);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team1PlayerScores.clear(); // Cleared otherwise data overlaps
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    for(DataSnapshot teamData : data.getChildren()){
                        if(teamData.getKey().equals("GameId")){
                            if(teamData.getValue().toString().equals(gameId)){
                                getTeamData(data, "Team 1", team1Players, team1Numbers,
                                        team1PlayerScores, team1Fouls);
                            }
                        }
                    }
                }
                recyclerViewSetupTeam1(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTeamData(DataSnapshot data, String team, ArrayList<String> teamNameArray,
                             ArrayList<String> teamPlayerArray, ArrayList<String> teamScoresArray,
                             ArrayList<String> teamFoulsArray) {
        for(DataSnapshot gameData : data.getChildren()){
            if(gameData.getKey().equals(team)){
                for(DataSnapshot teamData : gameData.getChildren()){
                    if(teamData.getKey().equals("Players")){
                        for(DataSnapshot playerData : teamData.getChildren()){
                            getPlayerData(playerData, teamNameArray, teamPlayerArray, teamScoresArray,
                                    teamFoulsArray);
                        }
                    }
                }
            }
        }
    }

    private void getPlayerData(DataSnapshot data, ArrayList<String> teamNameArray,
                               ArrayList<String> teamPlayerArray, ArrayList<String> teamScoresArray,
                               ArrayList<String> teamFoulsArray) {
        for(DataSnapshot perPlayer : data.getChildren()){
            if(perPlayer.getKey().equals("name")){
                teamNameArray.add(perPlayer.getValue().toString());
            } else if(perPlayer.getKey().equals("number")){
                teamPlayerArray.add(perPlayer.getValue().toString());
            } else {
                for(DataSnapshot playerScoreFouls : perPlayer.getChildren()){
                    if(playerScoreFouls.getKey().equals("1-pointers")){
                        teamScoresArray.add(playerScoreFouls.getValue().toString());
                    } else if(playerScoreFouls.getKey().equals("2-pointers")){
                        teamScoresArray.add(String.valueOf(Integer
                                .valueOf(playerScoreFouls.getValue().toString())*2));
                    } else if(playerScoreFouls.getKey().equals("3-pointers")){
                        teamScoresArray.add(String.valueOf(Integer
                                .valueOf(playerScoreFouls.getValue().toString())*3));
                    } else {
                        teamFoulsArray.add(playerScoreFouls.getValue().toString());
                    }
                }
            }
        }
        formatPlayerScores(teamScoresArray);
    }

    private void formatPlayerScores(ArrayList<String> teamScore) {
        ArrayList<String> scores = new ArrayList<>();

        for(int i = 0; i < teamScore.size(); i+=3) {
            int score = Integer.valueOf(teamScore.get(i))
                    + Integer.valueOf(teamScore.get(i + 1))
                    + Integer.valueOf(teamScore.get(i + 2));

            scores.add(String.valueOf(score));
        }

        team1PlayerScores = scores;
    }

    private void recyclerViewSetupTeam1(final View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_team_1_breakdown);
        TeamBreakdownRecyclerViewAdapter adapter = new TeamBreakdownRecyclerViewAdapter(getContext(),
                team1Players, team1Numbers, team1PlayerScores, team1Fouls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
