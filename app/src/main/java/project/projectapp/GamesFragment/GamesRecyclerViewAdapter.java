package project.projectapp.GamesFragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.GamesFragment.GameLive.LiveActivity;
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


    private ArrayList<String> team1Names, team2Names, team1Logos, team2Logos, team1Nicknames,
            team2Nicknames, gameDates, gameTimes, gameTypes, gameLocations, team1Scores,
            team2Scores, team1Wins, team1Losses, team2Wins, team2Losses, team1Abbreviations,
            team2Abbreviations, team1ScoresQ1, team1ScoresQ2, team1ScoresQ3, team1ScoresQ4,
            team2ScoresQ1, team2ScoresQ2, team2ScoresQ3, team2ScoresQ4, gameBreakdowns, gameIds;

    private Context context;

    public GamesRecyclerViewAdapter(Context thisContext, ArrayList<String> team1Name,
                                    ArrayList<String> team2Name, ArrayList<String> team1Logo,
                                    ArrayList<String> team2Logo, ArrayList<String> team1,
                                    ArrayList<String> team2, ArrayList<String> gameDate,
                                    ArrayList<String> gameTime, ArrayList<String> gameType,
                                    ArrayList<String> gameLocation, ArrayList<String> team1Score,
                                    ArrayList<String> team2Score, ArrayList<String> team1Win,
                                    ArrayList<String> team1Loss, ArrayList<String> team2Win,
                                    ArrayList<String> team2Loss, ArrayList<String> team1Abbreviation,
                                    ArrayList<String> team2Abbreviation, ArrayList<String> team1ScoreQ1,
                                    ArrayList<String> team1ScoreQ2, ArrayList<String> team1ScoreQ3,
                                    ArrayList<String> team1ScoreQ4, ArrayList<String> team2ScoreQ1,
                                    ArrayList<String> team2ScoreQ2, ArrayList<String> team2ScoreQ3,
                                    ArrayList<String> team2ScoreQ4, ArrayList<String> gameBreakdown,
                                    ArrayList<String> gameId){
        context = thisContext;
        team1Names = team1Name;
        team2Names = team2Name;
        team1Logos = team1Logo;
        team2Logos = team2Logo;
        team1Nicknames = team1;
        team2Nicknames = team2;
        gameDates = gameDate;
        gameTimes = gameTime;
        gameTypes = gameType;
        gameLocations = gameLocation;
        team1Scores = team1Score;
        team2Scores = team2Score;
        team1Wins = team1Win;
        team1Losses = team1Loss;
        team2Wins = team2Win;
        team2Losses = team2Loss;
        team1Abbreviations = team1Abbreviation;
        team2Abbreviations = team2Abbreviation;
        team1ScoresQ1 = team1ScoreQ1;
        team1ScoresQ2 = team1ScoreQ2;
        team1ScoresQ3 = team1ScoreQ3;
        team1ScoresQ4 = team1ScoreQ4;
        team2ScoresQ1 = team2ScoreQ1;
        team2ScoresQ2 = team2ScoreQ2;
        team2ScoresQ3 = team2ScoreQ3;
        team2ScoresQ4 = team2ScoreQ4;
        gameBreakdowns = gameBreakdown;
        gameIds = gameId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_fixtures_results_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

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
        } else {
            holder.fixtureView.setVisibility(View.VISIBLE);
            holder.time.setText(gameTimes.get(position));
            holder.date.setText(gameDates.get(position));
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
                            gameBreakdowns.get(position), gameIds.get(position));
                } else if (gameTypes.get(position).equals("Live")){
                    gameLive(gameIds.get(position));
                } else {
                    Toast.makeText(context, "Clicked: " + gameTypes.get(position), Toast.LENGTH_SHORT).show();
                }
                Log.d("activityruntime", "Option selected");
            }
        });
    }

    private void gameLive(String gameId) {
        Intent live = new Intent(context, LiveActivity.class);
        live.putExtra(GAME_ID, gameId);
        context.startActivity(live);   }

    private void gameResult(String team1, String team2, String team1Score, String team2Score,
                            String team1Nickname, String team2Nickname, String team1Logo,
                            String team2Logo, String team1Abbreviation, String team2Abbreviation,
                            String team1ScoreQ1, String team1ScoreQ2, String team1ScoreQ3,
                            String team1ScoreQ4, String team2ScoreQ1, String team2ScoreQ2,
                            String team2ScoreQ3, String team2ScoreQ4, String gameBreakdown,
                            String gameId){
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
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fixture_results_list_parent);
            fixtureView = itemView.findViewById(R.id.fixtures_display);
            resultsView = itemView.findViewById(R.id.results_display);
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
        }
    }
}
