package project.projectapp.GamesFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamRecyclerViewAdapter;

//TODO: access the games section of the database and load them into the recycler view
//TODO: based on the name of the team, search the teams section for additional data
public class GamesFragment extends Fragment {

    private final String ALLOW_COMMA = "@%$%@$#%";

    private boolean firstOpened;
    private int position;

    private ArrayList<String> team1Logos, team2Logos, gameDates, gameTimes, gameTypes, gameLocations,
            team1Nicknames, team2Nicknames, team1Scores, team2Scores, team1Wins, team1Losses,
            team2Wins, team2Losses, team1Abbreviations, team2Abbreviations, nonTeamSpecificDatas,
            gameBreakdowns, allTeams, team1, team2, allScores, team1ScoresQ1, team1ScoresQ2,
            team1ScoresQ3, team1ScoresQ4, team2ScoresQ1, team2ScoresQ2, team2ScoresQ3, team2ScoresQ4,
            gameIds;

    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_fragment, container, false);

        firstOpened = true;

        team1Logos = new ArrayList<>();
        team2Logos = new ArrayList<>();
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
        team1Abbreviations = new ArrayList<>();
        team2Abbreviations = new ArrayList<>();
        nonTeamSpecificDatas = new ArrayList<>();
        gameBreakdowns = new ArrayList<>();
        allTeams = new ArrayList<>();
        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        allScores = new ArrayList<>();
        team1ScoresQ1 = new ArrayList<>();
        team1ScoresQ2 = new ArrayList<>();
        team1ScoresQ3 = new ArrayList<>();
        team1ScoresQ4 = new ArrayList<>();
        team2ScoresQ1 = new ArrayList<>();
        team2ScoresQ2 = new ArrayList<>();
        team2ScoresQ3 = new ArrayList<>();
        team2ScoresQ4 = new ArrayList<>();
        gameIds = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar_games);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        getGames(view);

        return view;
    }

    private void clear(){
        team1Logos.clear();
        team2Logos.clear();
        gameDates.clear();
        gameTimes.clear();
        gameTypes.clear();
        gameLocations.clear();
        team1Nicknames.clear();
        team2Nicknames.clear();
        team1Scores.clear();
        team2Scores.clear();
        team1Wins.clear();
        team1Losses.clear();
        team2Wins.clear();
        team2Losses.clear();
        team1Abbreviations.clear();
        team2Abbreviations.clear();
        nonTeamSpecificDatas.clear();
        gameBreakdowns.clear();
        allTeams.clear();
        team1.clear();
        team2.clear();
        allScores.clear();
        team1ScoresQ1.clear();
        team1ScoresQ2.clear();
        team1ScoresQ3.clear();
        team1ScoresQ4.clear();
        team2ScoresQ1.clear();
        team2ScoresQ2.clear();
        team2ScoresQ3.clear();
        team2ScoresQ4.clear();
    }

    private void getGames(final View view){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(firstOpened){
                        scrollToNextFixture(data);
                    }
                    for(DataSnapshot keys : data.getChildren()){
                        if(!keys.getKey().equals("Team 1") && !keys.getKey().equals("Team 2")){
                            getNonTeamData(keys);
                        } else {
                            getRemainingData(keys);
                        }
                    }
                }
                setNonTeamData();
                setRemainingData();
                getTeamNickname(team1, team2, view);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setRemainingData() {
        for(int i = 0; i < allTeams.size(); i+=2){
            team1.add(allTeams.get(i));
            team2.add(allTeams.get(i+1));
        }

        for(int i = 0; i < allScores.size(); i+=10){
            team1ScoresQ1.add(allScores.get(i));
            team1ScoresQ2.add(allScores.get(i+1));
            team1ScoresQ3.add(allScores.get(i+2));
            team1ScoresQ4.add(allScores.get(i+3));
            team1Scores.add(allScores.get(i+4));
            team2ScoresQ1.add(allScores.get(i+5));
            team2ScoresQ2.add(allScores.get(i+6));
            team2ScoresQ3.add(allScores.get(i+7));
            team2ScoresQ4.add(allScores.get(i+8));
            team2Scores.add(allScores.get(i+9));
        }
    }

    private void getRemainingData(DataSnapshot data){
        for(DataSnapshot teamData : data.getChildren()){
            if(teamData.getKey().equals("Name")){
                allTeams.add(teamData.getValue().toString());
            } else if (teamData.getKey().equals("Score")){
                for(DataSnapshot scores : teamData.getChildren()){
                    allScores.add(scores.getValue().toString());
                }
            }
        }
    }

    private void setNonTeamData() {
        for(int i = 0; i < nonTeamSpecificDatas.size(); i+=6){
            gameBreakdowns.add(nonTeamSpecificDatas.get(i).replace(ALLOW_COMMA, ","));
            gameDates.add(nonTeamSpecificDatas.get(i+1));
            gameIds.add(nonTeamSpecificDatas.get(i+2));
            gameLocations.add(formatLocation(nonTeamSpecificDatas.get(i+3)));
            gameTimes.add(nonTeamSpecificDatas.get(i+4));
            gameTypes.add(nonTeamSpecificDatas.get(i+5));
        }
    }

    private void scrollToNextFixture(DataSnapshot data){
        for(DataSnapshot type : data.getChildren()){
            if(type.getKey().equals("Type")){
                if(type.getValue().toString().equals("Result")){
                    position++;
                }
            }
        }
    }

    /**
     * Separates the database data and stores them into strings
     * @param dataSnapshot
     */
    private void getNonTeamData(DataSnapshot dataSnapshot){
        String data = String.valueOf(dataSnapshot.getValue());
        String removeCurlyBrackets = data.replace("{", "")
                .replace("}", "");

        nonTeamSpecificDatas.add(removeCurlyBrackets);
    }

    private void getTeamNickname(final ArrayList<String> team1, final ArrayList<String> team2, final View view) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearTeamData();
                for(String teamName : team1){
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getKey().equals(teamName)) {
                            for (DataSnapshot teamData : data.getChildren()) {
                                if (teamData.getKey().equals("Nickname")) {
                                    team1Nicknames.add(teamData.getValue().toString());
                                }
                                if (teamData.getKey().equals("Wins")) {
                                    team1Wins.add(teamData.getValue().toString());
                                }
                                if (teamData.getKey().equals("Loss")) {
                                    team1Losses.add(teamData.getValue().toString());
                                }
                                if (teamData.getKey().equals("Logo")) {
                                    team1Logos.add(teamData.getValue().toString());
                                }
                                if (teamData.getKey().equals("Abbreviation")) {
                                    team1Abbreviations.add(teamData.getValue().toString());
                                }
                            }
                        }
                    }
                }
                for(String teamName : team2){
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(data.getKey().equals(teamName)){
                            for(DataSnapshot teamData : data.getChildren()){
                                if(teamData.getKey().equals("Nickname")){
                                    team2Nicknames.add(teamData.getValue().toString());
                                }
                                if(teamData.getKey().equals("Wins")){
                                    team2Wins.add(teamData.getValue().toString());
                                }
                                if(teamData.getKey().equals("Loss")){
                                    team2Losses.add(teamData.getValue().toString());
                                }
                                if(teamData.getKey().equals("Logo")){
                                    team2Logos.add(teamData.getValue().toString());
                                }
                                if(teamData.getKey().equals("Abbreviation")){
                                    team2Abbreviations.add(teamData.getValue().toString());
                                }
                            }
                        }
                    }
                }

                recyclerViewSetup(view);
                firstOpened = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        gameDateToValue();
    }

    private void clearTeamData() {
        team1Wins.clear();
        team1Losses.clear();
        team2Wins.clear();
        team2Losses.clear();
    }

    private String formatLocation(String data){
        String replaceSemiColon = data.replace(";", ",");
        return replaceSemiColon;
    }


    private void recyclerViewSetup(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_games);
        GamesRecyclerViewAdapter adapter = new GamesRecyclerViewAdapter(getContext(), team1, team2,
                team1Logos, team2Logos, team1Nicknames, team2Nicknames, gameDates, gameTimes,
                gameTypes, gameLocations, team1Scores, team2Scores, team1Wins, team1Losses,
                team2Wins, team2Losses, team1Abbreviations, team2Abbreviations, team1ScoresQ1,
                team1ScoresQ2, team1ScoresQ3, team1ScoresQ4, team2ScoresQ1, team2ScoresQ2,
                team2ScoresQ3, team2ScoresQ4, gameBreakdowns, gameIds);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        recyclerView.getLayoutManager().scrollToPosition(position);

        // Used so when the database is updated, it keeps the users last position
        if(!firstOpened){
            getUserLastScrolledPosition(recyclerView);
        }

        //The recyclerview is done loading and we can now hide the progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getUserLastScrolledPosition(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int offset = recyclerView.computeVerticalScrollOffset();
                int extent = recyclerView.computeVerticalScrollExtent();
                int range = recyclerView.computeVerticalScrollRange();

                int percentage = (int)(100.0 * offset / (float)(range - extent));


                position = percentage/22; //20 represents each cell, and a further 2 for the gap
            }
        });
    }


    /**
     * Tests the game date against todays date to change games from fixtures to results
     */
    //TODO: this can be improved by instead waiting for the 'official' to finish inserting the game score and on completion, change it to a result
    //TODO: extending on this, once the official has chosen to start inserting scores, it changes to a live game
    private void gameDateToValue() {
        for(String date : gameDates){
            String[] splitDate = date.split(" ");
            String month = splitDate[0];
            String day = splitDate[1];
            String year = splitDate[2];

            String formatted = year + formatNumber(monthNameToValue(month)) + day;

            if(Integer.valueOf(formatted) > Integer.valueOf(getDateTimeAndFormat())){
                //Log.d("teamdata", "future date");
                //Log.d("teamdata", formatted);
                //Log.d("teamdata", getDateTimeAndFormat());
            } else {
                //Log.d("teamdata", "past date");
                //Log.d("teamdata", formatted);
                //Log.d("teamdata", getDateTimeAndFormat());
            }

        }
    }

    private String getDateTimeAndFormat() {
        String todayDateTime = String.valueOf(Calendar.getInstance().getTime());

        // Calendar.getInstance().getTime() returns a long string of various data for today, split and access what we need
        String[] splitTime = todayDateTime.split(" ");
        String formattedDate = splitTime[5] + formatNumber(monthNameToValue(splitTime[1])) + splitTime[2];
        return formattedDate;
    }

    private String formatNumber(int number){
        if(String.valueOf(number).length() == 1) {
            String value = "0" + String.valueOf(number);
            return value;
        }
        return null;
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
