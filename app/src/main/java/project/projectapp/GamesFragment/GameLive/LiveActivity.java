package project.projectapp.GamesFragment.GameLive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import project.projectapp.PagerAdapter;
import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamBioFragment;
import project.projectapp.TeamsFragment.TeamRoster.TeamRosterFragment;

public class LiveActivity extends AppCompatActivity {

    private final String GAME_ID = "gameIds";
    private final String LIVE_TEAM_DATA = "liveTeamData";
    private final String TEAM_1_PLAYERS = "team1Players";
    private final String TEAM_1_NUMBERS = "team1Number";
    private final String TEAM_1_PLAYER_SCORES = "team1PlayerScores";
    private final String TEAM_1_FOULS = "team1Fouls";
    private final String TEAM_2_PLAYERS = "team2Players";
    private final String TEAM_2_NUMBERS = "team2Number";
    private final String TEAM_2_PLAYER_SCORES = "team2PlayerScores";
    private final String TEAM_2_FOULS = "team2Fouls";
    
    private ImageView team1Logo, team2Logo, team1ToolbarLogo, team2ToolbarLogo;
    private LinearLayout information;
    private TextView team1Nickname, team2Nickname, team1Abbreviation, team2Abbreviation,
            team1AbbrByQuarter, team2AbbrByQuarter, team1ScoreQ1, team1ScoreQ2, team1ScoreQ3,
            team1ScoreQ4, team2ScoreQ1, team2ScoreQ2, team2ScoreQ3, team2ScoreQ4, team1ScoreTotal,
            team2ScoreTotal, team1Score, team2Score, liveGameQuarter;

    private Toolbar toolbar;
    private ViewPager viewPager;

    private Animation fadeIn, fadeOut;

    private String gameId, team1, team2;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_game);

        team1Logo = findViewById(R.id.live_game_team_1);
        team2Logo = findViewById(R.id.live_game_team_2);
        team1ToolbarLogo = findViewById(R.id.live_game_toolbar_team_1);
        team2ToolbarLogo = findViewById(R.id.live_game_toolbar_team_2);
        information = findViewById(R.id.live_game_toolbar_information);
        team1Nickname = findViewById(R.id.live_game_team_1_nickname);
        team2Nickname = findViewById(R.id.live_game_team_2_nickname);
        team1Abbreviation = findViewById(R.id.live_game_toolbar_abbreviation_1);
        team2Abbreviation = findViewById(R.id.live_game_toolbar_abbreviation_2);
        team1AbbrByQuarter = findViewById(R.id.live_game_by_quarter_team_1);
        team2AbbrByQuarter = findViewById(R.id.live_game_by_quarter_team_2);
        team1ScoreQ1 = findViewById(R.id.live_game_team_1_score_q1);
        team1ScoreQ2 = findViewById(R.id.live_game_team_1_score_q2);
        team1ScoreQ3 = findViewById(R.id.live_game_team_1_score_q3);
        team1ScoreQ4 = findViewById(R.id.live_game_team_1_score_q4);
        team2ScoreQ1 = findViewById(R.id.live_game_team_2_score_q1);
        team2ScoreQ2 = findViewById(R.id.live_game_team_2_score_q2);
        team2ScoreQ3 = findViewById(R.id.live_game_team_2_score_q3);
        team2ScoreQ4 = findViewById(R.id.live_game_team_2_score_q4);
        team1ScoreTotal = findViewById(R.id.live_game_team_1_total);
        team2ScoreTotal = findViewById(R.id.live_game_team_2_total);
        team1Score = findViewById(R.id.live_game_team_1_score);
        team2Score = findViewById(R.id.live_game_team_2_score);
        liveGameQuarter = findViewById(R.id.live_game_which_quarter);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        toolbar = findViewById(R.id.live_game_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);

        viewPager = findViewById(R.id.live_game_view_pager);

        TabLayout tabs = findViewById(R.id.live_game_tab_layout);
        tabs.setupWithViewPager(viewPager);

        setTeamData();
        addInfoWhenCollapsed();
    }

    private void setTeamData() {
        gameId = getIntent().getStringExtra(GAME_ID);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot games : dataSnapshot.getChildren()){
                    for(DataSnapshot perGame : games.getChildren()){
                        if(perGame.getKey().equals("GameId")){
                            if(perGame.getValue().toString().equals(gameId)){
                                getTeamData(games, "Team 1");
                                getTeamData(games, "Team 2");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTeamData(DataSnapshot games, String team) {
        for(DataSnapshot getTeamData : games.getChildren()){
            if(getTeamData.getKey().equals(team)){
                for(DataSnapshot teamData : getTeamData.getChildren()){
                    if(teamData.getKey().equals("Name")){
                        if(team == "Team 1"){
                            getDataFromGamesDatabase(teamData.getValue().toString(), 1);
                        } else {
                            getDataFromGamesDatabase(teamData.getValue().toString(), 2);
                        }
                    }
                    if(teamData.getKey().equals("Score")){
                        if(team == "Team 1"){
                            getTeamScores(teamData, 1);
                        } else {
                            getTeamScores(teamData, 2);
                        }
                    }
                }
            }
            if(getTeamData.getKey().equals("CurrentQuarter")){
                liveGameQuarter.setText("LIVE - " + getTeamData.getValue().toString());
            }
        }
    }

    private void getTeamScores(DataSnapshot teamData, int whichTeam) {
        for(DataSnapshot perTeamScore : teamData.getChildren()){
            if(whichTeam == 1){
                if(perTeamScore.getKey().equals("Q1")){
                    team1ScoreQ1.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q2")){
                    team1ScoreQ2.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q3")){
                    team1ScoreQ3.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q4")){
                    team1ScoreQ4.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Total")){
                    team1ScoreTotal.setText(perTeamScore.getValue().toString());
                    team1Score.setText(perTeamScore.getValue().toString());
                }
            } else {
                if(perTeamScore.getKey().equals("Q1")){
                    team2ScoreQ1.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q2")){
                    team2ScoreQ2.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q3")){
                    team2ScoreQ3.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Q4")){
                    team2ScoreQ4.setText(perTeamScore.getValue().toString());
                }
                if(perTeamScore.getKey().equals("Total")){
                    team2ScoreTotal.setText(perTeamScore.getValue().toString());
                    team2Score.setText(perTeamScore.getValue().toString());
                }
            }
        }
    }

    private void getDataFromGamesDatabase(final String teamName, final int whichTeam){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot teams : dataSnapshot.getChildren()){
                    if(teams.getKey().equals(teamName)){
                        for(DataSnapshot teamData : teams.getChildren()){
                            if(teamData.getKey().equals("Nickname")){
                                if(whichTeam == 1){
                                    team1 = teamData.getValue().toString();
                                    team1Nickname.setText(team1);
                                } else {
                                    team2 = teamData.getValue().toString();
                                    team2Nickname.setText(team2);
                                }
                            }
                            if(teamData.getKey().equals("Logo")){
                                if(whichTeam == 1){
                                    Glide.with(getApplicationContext())
                                            .load(teamData.getValue().toString())
                                            .into(team1Logo);
                                    Glide.with(getApplicationContext())
                                            .load(teamData.getValue().toString())
                                            .into(team1ToolbarLogo);
                                } else {
                                    Glide.with(getApplicationContext())
                                            .load(teamData.getValue().toString())
                                            .into(team2Logo);
                                    Glide.with(getApplicationContext())
                                            .load(teamData.getValue().toString())
                                            .into(team2ToolbarLogo);
                                }
                            }
                            if(teamData.getKey().equals("Abbreviation")){
                                if(whichTeam == 1){
                                    team1Abbreviation.setText(teamData.getValue().toString());
                                    team1AbbrByQuarter.setText(teamData.getValue().toString());
                                } else {
                                    team2Abbreviation.setText(teamData.getValue().toString());
                                    team2AbbrByQuarter.setText(teamData.getValue().toString());
                                }
                            }
                        }
                    }
                }

                setupViewPager(viewPager, team1, team2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void addInfoWhenCollapsed() {
        AppBarLayout appBarLayout = findViewById(R.id.live_game_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    information.setVisibility(View.VISIBLE);
                    information.startAnimation(fadeIn);
                    isShow = true;
                } else if(isShow) {
                    information.setVisibility(View.INVISIBLE);
                    information.startAnimation(fadeOut);
                    isShow = false;
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, String team1, String team2){
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Team1RosterScore(), team1);
        adapter.addFragment(new Team2RosterScore(), team2);
        viewPager.setAdapter(adapter);
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
