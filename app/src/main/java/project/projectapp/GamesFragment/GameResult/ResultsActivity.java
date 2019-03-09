package project.projectapp.GamesFragment.GameResult;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import project.projectapp.R;

public class ResultsActivity extends AppCompatActivity {

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

    private int playerCount;
    private String team1, team2, score1, score2, nickname1, nickname2, logo1, logo2, abbreviation1,
            abbreviation2, wins1, losses1, wins2, losses2, scoreQ11, scoreQ21, scoreQ31, scoreQ41,
            scoreQ12, scoreQ22, scoreQ32, scoreQ42, breakdown, gameId;

    private ArrayList<String> team1Players, team2Players, team1Numbers, team2Numbers,
            team1PlayerScores, team2PlayerScores, team1Fouls, team2Fouls, allNumbers,
            allPlayerScores, allFouls;

    private ExpandableLayout gameBreakdownLayout, team2PlayerBreakdownLayout, team1PlayerBreakdownLayout;
    private ImageView team1Logo, team2Logo, team1ToolbarLogo, team2ToolbarLogo;
    private TextView team1Nickname, team2Nickname, team1Score, team2Score, team1ToolbarName,
            team2ToolbarName, team1ByQuarterName, team2ByQuarterName, team1Total, team2Total,
            team1ScoreQ1, team1ScoreQ2, team1ScoreQ3, team1ScoreQ4, team2ScoreQ1, team2ScoreQ2,
            team2ScoreQ3, team2ScoreQ4, gameBreakdown, gameBreakdownText, team1PlayerData,
            team2PlayerData;

    private LinearLayout information;

    private Animation slideUp, slideDown;

    private Toolbar toolbar;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        Log.d("activityruntime", "Option loading");

        playerCount = 0;

        gameBreakdownLayout = findViewById(R.id.results_game_breakdown_layout);
        team1PlayerBreakdownLayout = findViewById(R.id.results_team_1_player_breakdown);
        team2PlayerBreakdownLayout = findViewById(R.id.results_team_2_player_breakdown);

        team1Nickname = findViewById(R.id.results_team_1_nickname);
        team2Nickname = findViewById(R.id.results_team_2_nickname);
        team1Score = findViewById(R.id.results_team_1_score);
        team2Score = findViewById(R.id.results_team_2_score);
        team1Logo = findViewById(R.id.results_team_1);
        team2Logo = findViewById(R.id.results_team_2);
        team1ToolbarLogo = findViewById(R.id.results_toolbar_team_1);
        team2ToolbarLogo = findViewById(R.id.results_toolbar_team_2);
        team1ToolbarName = findViewById(R.id.results_toolbar_abbreviation_1);
        team2ToolbarName = findViewById(R.id.results_toolbar_abbreviation_2);
        team1ByQuarterName = findViewById(R.id.results_by_quarter_team_1);
        team2ByQuarterName = findViewById(R.id.results_by_quarter_team_2);
        team1Total = findViewById(R.id.results_team_1_total);
        team2Total = findViewById(R.id.results_team_2_total);
        team1ScoreQ1 = findViewById(R.id.results_team_1_score_q1);
        team1ScoreQ2 = findViewById(R.id.results_team_1_score_q2);
        team1ScoreQ3 = findViewById(R.id.results_team_1_score_q3);
        team1ScoreQ4 = findViewById(R.id.results_team_1_score_q4);
        team2ScoreQ1 = findViewById(R.id.results_team_2_score_q1);
        team2ScoreQ2 = findViewById(R.id.results_team_2_score_q2);
        team2ScoreQ3 = findViewById(R.id.results_team_2_score_q3);
        team2ScoreQ4 = findViewById(R.id.results_team_2_score_q4);
        gameBreakdown = findViewById(R.id.results_game_breakdown);
        gameBreakdownText = findViewById(R.id.results_game_breakdown_text);
        team1PlayerData = findViewById(R.id.results_team_1_player_data);
        team2PlayerData = findViewById(R.id.results_team_2_player_data);

        team1Players = new ArrayList<>();
        team2Players = new ArrayList<>();
        team1Numbers = new ArrayList<>();
        team2Numbers = new ArrayList<>();
        team1PlayerScores = new ArrayList<>();
        team2PlayerScores = new ArrayList<>();
        team1Fouls = new ArrayList<>();
        team2Fouls = new ArrayList<>();
        allNumbers = new ArrayList<>();
        allPlayerScores = new ArrayList<>();
        allFouls = new ArrayList<>();

        information = findViewById(R.id.results_toolbar_information);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        toolbar = findViewById(R.id.results_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);

        expandingLists(gameBreakdownLayout, gameBreakdown);
        expandingLists(team1PlayerBreakdownLayout, team1PlayerData);
        expandingLists(team2PlayerBreakdownLayout, team2PlayerData);
        retrieveIntentData();
        addInfoWhenCollapsed();

    }

    private void expandingLists(final ExpandableLayout layout, final TextView text){
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.toggle();
                if(layout.isExpanded()){
                    text.setCompoundDrawablesWithIntrinsicBounds(0,0,
                            R.drawable.ic_arrow_drop_down_black_24dp, 0);
                } else {
                    text.setCompoundDrawablesWithIntrinsicBounds(0,0,
                            R.drawable.ic_arrow_drop_up_black_24dp, 0);
                }
            }
        });
    }

    private void retrieveIntentData(){
        team1 = getIntent().getStringExtra(TEAM_1);
        team2 = getIntent().getStringExtra(TEAM_2);
        score1 = getIntent().getStringExtra(TEAM_1_SCORE);
        score2 = getIntent().getStringExtra(TEAM_2_SCORE);
        nickname1 = getIntent().getStringExtra(TEAM_1_NICKNAME);
        nickname2 = getIntent().getStringExtra(TEAM_2_NICKNAME);
        logo1 = getIntent().getStringExtra(TEAM_1_LOGO);
        logo2 = getIntent().getStringExtra(TEAM_2_LOGO);
        abbreviation1 = getIntent().getStringExtra(TEAM_1_ABBREVIATION);
        abbreviation2 = getIntent().getStringExtra(TEAM_2_ABBREVIATION);
        scoreQ11 = getIntent().getStringExtra(TEAM_1_SCORE_Q1);
        scoreQ21 = getIntent().getStringExtra(TEAM_1_SCORE_Q2);
        scoreQ31 = getIntent().getStringExtra(TEAM_1_SCORE_Q3);
        scoreQ41 = getIntent().getStringExtra(TEAM_1_SCORE_Q4);
        scoreQ12 = getIntent().getStringExtra(TEAM_2_SCORE_Q1);
        scoreQ22 = getIntent().getStringExtra(TEAM_2_SCORE_Q2);
        scoreQ32 = getIntent().getStringExtra(TEAM_2_SCORE_Q3);
        scoreQ42 = getIntent().getStringExtra(TEAM_2_SCORE_Q4);
        breakdown = getIntent().getStringExtra(GAME_BREAKDOWN);
        gameId = getIntent().getStringExtra(GAME_ID);

        getData();
    }

    private void getData() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team1PlayerScores.clear(); // Both are cleared otherwise data overlaps
                team2PlayerScores.clear(); // Both are cleared otherwise data overlaps
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    for(DataSnapshot teamData : data.getChildren()){
                        if(teamData.getKey().equals("GameId")){
                            if(teamData.getValue().toString().equals(gameId)){
                                getTeamData(data, "Team 1", team1Players, team1Numbers,
                                        team1PlayerScores, team1Fouls, 1);
                                getTeamData(data, "Team 2", team2Players, team2Numbers,
                                        team2PlayerScores, team2Fouls, 2);
                            }
                        }
                    }
                }
                setLayoutData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTeamData(DataSnapshot data, String team, ArrayList<String> teamNameArray,
                             ArrayList<String> teamPlayerArray, ArrayList<String> teamScoresArray,
                             ArrayList<String> teamFoulsArray, int whichTeam) {
        for(DataSnapshot gameData : data.getChildren()){
            if(gameData.getKey().equals(team)){
                for(DataSnapshot teamData : gameData.getChildren()){
                    if(teamData.getKey().equals("Players")){
                        for(DataSnapshot playerData : teamData.getChildren()){
                            getPlayerData(playerData, teamNameArray, teamPlayerArray, teamScoresArray,
                                    teamFoulsArray, whichTeam);
                        }
                    }
                }
            }
        }
    }

    private void getPlayerData(DataSnapshot data, ArrayList<String> teamNameArray,
                               ArrayList<String> teamPlayerArray, ArrayList<String> teamScoresArray,
                               ArrayList<String> teamFoulsArray, int whichTeam) {
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
        formatPlayerScores(teamScoresArray, whichTeam);
    }

    private void formatPlayerScores(ArrayList<String> teamScore, int whichTeam) {
        ArrayList<String> scores = new ArrayList<>();

        for(int i = 0; i < teamScore.size(); i+=3) {
            int score = Integer.valueOf(teamScore.get(i))
                    + Integer.valueOf(teamScore.get(i + 1))
                    + Integer.valueOf(teamScore.get(i + 2));

            scores.add(String.valueOf(score));
        }

        if(whichTeam == 1){
            team1PlayerScores = scores;
        } else {
            team2PlayerScores = scores;
        }
    }

    /**
     * Using Glide.with(getApplicationContext()) instead of Glide.with(this) because there is a bug
     * with the library regarding destroyed activities
     */
    private void setLayoutData(){
        team1Nickname.setText(nickname1);
        team2Nickname.setText(nickname2);
        team1Score.setText(score1);
        team2Score.setText(score2);
        Glide.with(getApplicationContext())
                .load(logo1)
                .into(team1Logo);
        Glide.with(getApplicationContext())
                .load(logo2)
                .into(team2Logo);
        Glide.with(getApplicationContext())
                .load(logo1)
                .into(team1ToolbarLogo);
        Glide.with(getApplicationContext())
                .load(logo2)
                .into(team2ToolbarLogo);
        team1ToolbarName.setText(abbreviation1);
        team2ToolbarName.setText(abbreviation2);
        team1ByQuarterName.setText(abbreviation1);
        team2ByQuarterName.setText(abbreviation2);
        team1Total.setText(score1);
        team2Total.setText(score2);
        team1ScoreQ1.setText(scoreQ11);
        team1ScoreQ2.setText(scoreQ21);
        team1ScoreQ3.setText(scoreQ31);
        team1ScoreQ4.setText(scoreQ41);
        team2ScoreQ1.setText(scoreQ12);
        team2ScoreQ2.setText(scoreQ22);
        team2ScoreQ3.setText(scoreQ32);
        team2ScoreQ4.setText(scoreQ42);
        gameBreakdownText.setText(breakdown);
        team1PlayerData.setText(nickname1);
        team2PlayerData.setText(nickname2);

        recyclerViewSetupTeam1();
        recyclerViewSetupTeam2();
    }

    private void addInfoWhenCollapsed() {
        AppBarLayout appBarLayout = findViewById(R.id.results_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            boolean allowAnimationUp = true;
            boolean allowAnimationDown = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset != 0) {
                    information.setVisibility(View.INVISIBLE);
                    if(allowAnimationDown){
                        information.startAnimation(slideDown);
                        allowAnimationUp = true;
                        allowAnimationDown = false;
                    }
                    isShow = true;
                } else {
                    information.setVisibility(View.VISIBLE);
                    if(allowAnimationUp){
                        information.startAnimation(slideUp);
                        allowAnimationUp = false;
                        allowAnimationDown = true;
                    }
                    isShow = false;
                }
            }
        });
    }

    /**
     * Home team
     */
    private void recyclerViewSetupTeam1() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_team_1_breakdown);
        TeamBreakdownRecyclerViewAdapter adapter = new TeamBreakdownRecyclerViewAdapter(this,
                team1Players, team1Numbers, team1PlayerScores, team1Fouls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Away team
     */
    private void recyclerViewSetupTeam2() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_team_2_breakdown);
        TeamBreakdownRecyclerViewAdapter adapter = new TeamBreakdownRecyclerViewAdapter(this,
                team2Players, team2Numbers, team2PlayerScores, team2Fouls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * Called when an option is selected, in this case the back button since it is the only option
     * @param item - our menu items, so the back button
     * @return - either true or the parent class' onOptionsItemSelected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Default onBackPressed, returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
