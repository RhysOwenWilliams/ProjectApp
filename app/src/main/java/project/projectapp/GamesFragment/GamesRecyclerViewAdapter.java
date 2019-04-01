package project.projectapp.GamesFragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import project.projectapp.GamesFragment.GameLive.LiveActivity;
import project.projectapp.GamesFragment.GameLive.OfficiateGame.OfficiateGameActivity;
import project.projectapp.GamesFragment.GameResult.ResultsActivity;
import project.projectapp.R;


public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>{

    private final String TEAM_1 = "team1Name";
    private final String TEAM_2 = "team2Name";
    private final String TEAM_1_SCORE = "team1Score";
    private final String TEAM_2_SCORE = "team2Score";
    private final String TEAM_1_NICKNAME = "team1Nickname";
    private final String TEAM_2_NICKNAME = "team2Nickname";
    private final String TEAM_1_LOGO = "team1Logo";
    private final String TEAM_2_LOGO = "team2Logo";
    private final String TEAM_1_ABBREVIATION = "team1Abbreviation";
    private final String TEAM_2_ABBREVIATION = "team2Abbreviation";
    private final String TEAM_1_SCORE_Q1 = "team1ScoreQ1";
    private final String TEAM_1_SCORE_Q2 = "team1ScoreQ2";
    private final String TEAM_1_SCORE_Q3 = "team1ScoreQ3";
    private final String TEAM_1_SCORE_Q4 = "team1ScoreQ4";
    private final String TEAM_2_SCORE_Q1 = "team2ScoreQ1";
    private final String TEAM_2_SCORE_Q2 = "team2ScoreQ2";
    private final String TEAM_2_SCORE_Q3 = "team2ScoreQ3";
    private final String TEAM_2_SCORE_Q4 = "team2ScoreQ4";
    private final String GAME_BREAKDOWN = "gameBreakdown";
    private final String GAME_ID = "gameIds";
    private final String TEAM_1_COLOURS = "team1Colours";
    private final String TEAM_2_COLOURS = "team2Colours";


    private ArrayList<String> team1Names, team2Names, team1Logos, team2Logos, team1Nicknames,
            team2Nicknames, gameDates, gameTimes, gameTypes, gameCurrentQuarters, team1Scores,
            team2Scores, team1Wins, team1Losses, team2Wins, team2Losses, team1Abbreviations,
            team2Abbreviations, team1ScoresQ1, team1ScoresQ2, team1ScoresQ3, team1ScoresQ4,
            team2ScoresQ1, team2ScoresQ2, team2ScoresQ3, team2ScoresQ4, gameBreakdowns, gameIds,
            team1Colours, team2Colours;

    private Context context;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public GamesRecyclerViewAdapter(Context context, ArrayList<String> team1Names,
                                    ArrayList<String> team2Names, ArrayList<String> team1Logos,
                                    ArrayList<String> team2Logos, ArrayList<String> team1Nicknames,
                                    ArrayList<String> team2Nicknames, ArrayList<String> gameDates,
                                    ArrayList<String> gameTimes, ArrayList<String> gameTypes,
                                    ArrayList<String> gameCurrentQuarters, ArrayList<String> team1Scores,
                                    ArrayList<String> team2Scores, ArrayList<String> team1Wins,
                                    ArrayList<String> team1Losses, ArrayList<String> team2Wins,
                                    ArrayList<String> team2Losses, ArrayList<String> team1Abbreviations,
                                    ArrayList<String> team2Abbreviations, ArrayList<String> team1ScoresQ1,
                                    ArrayList<String> team1ScoresQ2, ArrayList<String> team1ScoresQ3,
                                    ArrayList<String> team1ScoresQ4, ArrayList<String> team2ScoresQ1,
                                    ArrayList<String> team2ScoresQ2, ArrayList<String> team2ScoresQ3,
                                    ArrayList<String> team2ScoresQ4, ArrayList<String> gameBreakdowns,
                                    ArrayList<String> gameIds, ArrayList<String> team1Colours,
                                    ArrayList<String> team2Colours){
        this.context = context;
        this.team1Names = team1Names;
        this.team2Names = team2Names;
        this.team1Logos = team1Logos;
        this.team2Logos = team2Logos;
        this.team1Nicknames = team1Nicknames;
        this.team2Nicknames = team2Nicknames;
        this.gameDates = gameDates;
        this.gameTimes = gameTimes;
        this.gameTypes = gameTypes;
        this.gameCurrentQuarters = gameCurrentQuarters;
        this.team1Scores = team1Scores;
        this.team2Scores = team2Scores;
        this.team1Wins = team1Wins;
        this.team1Losses = team1Losses;
        this.team2Wins = team2Wins;
        this.team2Losses = team2Losses;
        this.team1Abbreviations = team1Abbreviations;
        this.team2Abbreviations = team2Abbreviations;
        this.team1ScoresQ1 = team1ScoresQ1;
        this.team1ScoresQ2 = team1ScoresQ2;
        this.team1ScoresQ3 = team1ScoresQ3;
        this.team1ScoresQ4 = team1ScoresQ4;
        this.team2ScoresQ1 = team2ScoresQ1;
        this.team2ScoresQ2 = team2ScoresQ2;
        this.team2ScoresQ3 = team2ScoresQ3;
        this.team2ScoresQ4 = team2ScoresQ4;
        this.gameBreakdowns = gameBreakdowns;
        this.gameIds = gameIds;
        this.team1Colours =  team1Colours;
        this.team2Colours = team2Colours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_fixtures_results_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        firebaseAuth = FirebaseAuth.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.team1Nickname.setText(team1Nicknames.get(position));
        holder.team2Nickname.setText(team2Nicknames.get(position));
        holder.team1Record.setText(team1Wins.get(position) + " - " + team1Losses.get(position));
        holder.team2Record.setText(team2Wins.get(position) + " - " + team2Losses.get(position));

        Glide.with(context)
                .load(team1Logos.get(position))
                .apply(new RequestOptions().override(200, 200))
                .into(holder.team1);
        Glide.with(context)
                .load(team2Logos.get(position))
                .apply(new RequestOptions().override(200, 200))
                .into(holder.team2);

        if(gameTypes.get(position).equals("Result")){
            holder.resultsView.setVisibility(View.VISIBLE);
            holder.fixtureView.setVisibility(View.INVISIBLE);
            holder.team1Score.setText(team1Scores.get(position));
            holder.team2Score.setText(team2Scores.get(position));
            holder.finished.setVisibility(View.VISIBLE);
        } else if (gameTypes.get(position).equals("Live")){
            holder.resultsView.setVisibility(View.VISIBLE);
            holder.liveView.setVisibility(View.VISIBLE);
            holder.team1Score.setText(team1Scores.get(position));
            holder.team2Score.setText(team2Scores.get(position));
            if(gameCurrentQuarters.get(position).equals("Total")){
                holder.liveCurrentQuarter.setText("Ending...");
            } else {
                holder.liveCurrentQuarter.setText("LIVE - " + gameCurrentQuarters.get(position));
            }
        } else {
            holder.fixtureView.setVisibility(View.VISIBLE);
            holder.time.setText(gameTimes.get(position));
            holder.date.setText(gameDates.get(position));
            checkIfGameReadyEveryMinute(holder, position);
        }

        // Result has multiple parameters since we only need to access the database data once since
        // this data doesn't change, Live games data however does, so we only pass the id
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameTypes.get(position).equals("Result")){
                    gameResult(team1Names.get(position), team2Names.get(position),
                            team1Scores.get(position), team2Scores.get(position),
                            team1Nicknames.get(position), team2Nicknames.get(position),
                            team1Logos.get(position), team2Logos.get(position),
                            team1Abbreviations.get(position), team2Abbreviations.get(position),
                            team1ScoresQ1.get(position), team1ScoresQ2.get(position),
                            team1ScoresQ3.get(position), team1ScoresQ4.get(position),
                            team2ScoresQ1.get(position),team2ScoresQ2.get(position),
                            team2ScoresQ3.get(position),team2ScoresQ4.get(position),
                            gameBreakdowns.get(position), gameIds.get(position),
                            team1Colours.get(position), team2Colours.get(position));
                } else if (gameTypes.get(position).equals("Live")){
                    gameLive(gameIds.get(position));
                }
            }
        });
    }
    /**
     * Checks if the current signed in user is an official and if so, allows them further access to
     * create a new post
     */
    private void authenticateUser(final ViewHolder holder, final int position) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Roles");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot signedInId = dataSnapshot.child(firebaseAuth.getUid());
                if(signedInId.hasChild("isOfficial")){
                    DataSnapshot data = signedInId.child("isOfficial");
                    Boolean roleValue = Boolean.valueOf(data.getValue().toString());

                    if(roleValue){
                        holder.officiateGame.setVisibility(View.VISIBLE);
                        holder.officiateGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                officiateGame(position);
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void checkIfGameReadyEveryMinute(final ViewHolder holder, final int position) {
        if(gameDateToValue(gameDates.get(position), gameTimes.get(position))){
            authenticateUser(holder, position);
        } else {
            holder.officiateGame.setVisibility(View.INVISIBLE);
        }
    }

    private void officiateGame(int position) {
        Intent officiate = new Intent(context, OfficiateGameActivity.class);
        officiate.putExtra(GAME_ID, gameIds.get(position));
        officiate.putExtra(TEAM_1, team1Names.get(position));
        officiate.putExtra(TEAM_2, team2Names.get(position));
        officiate.putExtra(TEAM_1_ABBREVIATION, team1Abbreviations.get(position));
        officiate.putExtra(TEAM_2_ABBREVIATION, team2Abbreviations.get(position));
        officiate.putExtra(TEAM_1_LOGO, team1Logos.get(position));
        officiate.putExtra(TEAM_2_LOGO, team2Logos.get(position));
        officiate.putExtra(TEAM_1_NICKNAME, team1Nicknames.get(position));
        officiate.putExtra(TEAM_2_NICKNAME, team2Nicknames.get(position));
        context.startActivity(officiate);
    }

    private void gameLive(String gameId) {
        Intent live = new Intent(context, LiveActivity.class);
        live.putExtra(GAME_ID, gameId);
        context.startActivity(live);
    }

    private void gameResult(String team1, String team2, String team1Score, String team2Score,
                            String team1Nickname, String team2Nickname, String team1Logo,
                            String team2Logo, String team1Abbreviation, String team2Abbreviation,
                            String team1ScoreQ1, String team1ScoreQ2, String team1ScoreQ3,
                            String team1ScoreQ4, String team2ScoreQ1, String team2ScoreQ2,
                            String team2ScoreQ3, String team2ScoreQ4, String gameBreakdown,
                            String gameId, String team1Colour, String team2Colour){
        Intent result = new Intent(context, ResultsActivity.class);
        result.putExtra(TEAM_1, team1);
        result.putExtra(TEAM_2, team2);
        result.putExtra(TEAM_1_SCORE, team1Score);
        result.putExtra(TEAM_2_SCORE, team2Score);
        result.putExtra(TEAM_1_NICKNAME, team1Nickname);
        result.putExtra(TEAM_2_NICKNAME, team2Nickname);
        result.putExtra(TEAM_1_LOGO, team1Logo);
        result.putExtra(TEAM_2_LOGO, team2Logo);
        result.putExtra(TEAM_1_ABBREVIATION, team1Abbreviation);
        result.putExtra(TEAM_2_ABBREVIATION, team2Abbreviation);
        result.putExtra(TEAM_1_SCORE_Q1, team1ScoreQ1);
        result.putExtra(TEAM_1_SCORE_Q2, team1ScoreQ2);
        result.putExtra(TEAM_1_SCORE_Q3, team1ScoreQ3);
        result.putExtra(TEAM_1_SCORE_Q4, team1ScoreQ4);
        result.putExtra(TEAM_2_SCORE_Q1, team2ScoreQ1);
        result.putExtra(TEAM_2_SCORE_Q2, team2ScoreQ2);
        result.putExtra(TEAM_2_SCORE_Q3, team2ScoreQ3);
        result.putExtra(TEAM_2_SCORE_Q4, team2ScoreQ4);
        result.putExtra(GAME_BREAKDOWN, gameBreakdown);
        result.putExtra(GAME_ID, gameId);
        result.putExtra(TEAM_1_COLOURS, team1Colour);
        result.putExtra(TEAM_2_COLOURS, team2Colour);
        context.startActivity(result);
    }

    @Override
    public int getItemCount() {
        return gameBreakdowns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
        RelativeLayout fixtureView;
        RelativeLayout liveView;
        RelativeLayout officiateGame;
        LinearLayout resultsView;
        ImageView team1;
        ImageView team2;
        TextView team1Nickname;
        TextView team2Nickname;
        TextView team1Score;
        TextView team2Score;
        TextView team1Record;
        TextView team2Record;
        TextView time;
        TextView date;
        TextView finished;
        TextView liveCurrentQuarter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fixture_results_list_parent);
            fixtureView = itemView.findViewById(R.id.fixtures_display);
            resultsView = itemView.findViewById(R.id.results_display);
            officiateGame = itemView.findViewById(R.id.fixture_results_officiate);
            liveView = itemView.findViewById(R.id.live_display);
            team1 = itemView.findViewById(R.id.fixture_results_list_team_1);
            team2 = itemView.findViewById(R.id.fixture_results_list_team_2);
            team1Nickname = itemView.findViewById(R.id.fixture_results_list_team_1_nickname);
            team2Nickname = itemView.findViewById(R.id.fixture_results_list_team_2_nickname);
            team1Score = itemView.findViewById(R.id.fixture_results_list_team_1_score);
            team2Score = itemView.findViewById(R.id.fixture_results_list_team_2_score);
            team1Record = itemView.findViewById(R.id.fixture_results_list_team_1_record);
            team2Record = itemView.findViewById(R.id.fixture_results_list_team_2_record);
            time = itemView.findViewById(R.id.fixture_results_list_time);
            date = itemView.findViewById(R.id.fixture_results_list_date);
            finished = itemView.findViewById(R.id.fixture_results_finished);
            liveCurrentQuarter = itemView.findViewById(R.id.live_current_quarter);
        }
    }

    /**
     * Tests the game date against todays date to change games from fixtures to results
     */

    private boolean gameDateToValue(String date, String time) {
        String[] splitDate = date.split(" ");
        String month = splitDate[0];
        String day = splitDate[1];
        String year = splitDate[2];

        String formatted = year + formatNumber(monthNameToValue(month)) + day;

        if(formatted.equals(getDateFormatted())){
            if(confirmTime(getTimeFormatted(), time)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param currentTime the current time
     * @param gameTime time that the game will be played
     * @return true if the game is in 10 minutes time, false otherwise
     */
    private Boolean confirmTime(String currentTime, String gameTime){
        String currentSplit = currentTime.substring(0,2);
        String gameSplit = gameTime.substring(0,2);

        if(Integer.valueOf(gameSplit) - Integer.valueOf(currentSplit) == 1){
            int checkTimes = Integer.valueOf(gameTime.replace(":", ""))
                    - (Integer.valueOf(currentTime)+40);
            if(checkTimes <= 10 /*&& checkTimes >=0*/){
                return true;
            } else {
                return false;
            }
        } else {
            int checkTimes = Integer.valueOf(gameTime.replace(":", ""))
                    - Integer.valueOf(currentTime);
            if(checkTimes <= 10 /*&& checkTimes >=0*/){
                return true;
            } else {
                return false;
            }
        }
    }

    private String getTimeFormatted() {
        String todayDateTime = String.valueOf(Calendar.getInstance().getTime());

        // Calendar.getInstance().getTime() returns a long string of various data for today, split and access what we need
        String[] splitTime = todayDateTime.split(" ");
        String retrievedTime = splitTime[3];
        String[] formatTime = retrievedTime.split(":");
        String formattedTime = formatTime[0] + formatTime[1];
        return formattedTime;
    }

    private String getDateFormatted() {
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
