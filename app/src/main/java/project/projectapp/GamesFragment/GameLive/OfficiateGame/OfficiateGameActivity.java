package project.projectapp.GamesFragment.GameLive.OfficiateGame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.projectapp.Account.ProfileActivity;
import project.projectapp.PagerAdapter;
import project.projectapp.R;

//TODO: retrieve the scores for the quarter
//TODO: test an increment button to change the score
public class OfficiateGameActivity extends AppCompatActivity {

    private static final long TIME_PER_QUARTER = 10000; //600000

    private static final String QUARTER_2 = "Start Quarter 2";
    private static final String QUARTER_3 = "Start Quarter 3";
    private static final String QUARTER_4 = "Start Quarter 4";
    private static final String FINISHED = "Game Finished";
    private static final String TITLE = "How to use";

    private final String TEAM_1_NICKNAME = "team1Nickname";
    private final String TEAM_2_NICKNAME = "team2Nickname";
    private final String TEAM_1_LOGO = "team1Logo";
    private final String TEAM_2_LOGO = "team2Logo";
    private final String TEAM_1_ABBREVIATION = "team1Abbreviation";
    private final String TEAM_2_ABBREVIATION = "team2Abbreviation";
    private final String GAME_ID = "gameIds";

    private Boolean quarterIsPlaying, canAddScores;
    private String team1, team2, logo1, logo2, gameId, nickname1, nickname2;

    private EditText writeBreakdown;
    private FloatingActionButton quarterCountdown;
    private ImageView team1ToolbarLogo, team2ToolbarLogo, information;
    private TextView team1ToolbarAbbreviation, team2ToolbarAbbreviation, quarterTime,
            officiateGameStart, quarterNumber, scoreByQuarterTeam1, scoreByQuarterTeam2,
            quarter1, quarter2, quarter3, quarter4, finished, team1ScoreQ1, team1ScoreQ2,
            team1ScoreQ3, team1ScoreQ4, team2ScoreQ1, team2ScoreQ2, team2ScoreQ3, team2ScoreQ4,
            team1ScoreTotal, team2ScoreTotal;
    private Toolbar toolbar;

    private LinearLayout startGameQuarter, quarterTimer;

    private CountDownTimer countDownTimer;

    private ViewPager viewPager;

    private DatabaseReference databaseReference, databaseReferenceTotalScore,
            databaseReferenceCompleteGame;

    private int quarter, score;
    private long timeRemaining = TIME_PER_QUARTER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officiate_game);

        team1ToolbarLogo = findViewById(R.id.officiate_game_toolbar_team_1);
        team2ToolbarLogo = findViewById(R.id.officiate_game_toolbar_team_2);
        information = findViewById(R.id.officiate_game_information);
        team1ToolbarAbbreviation = findViewById(R.id.officiate_game_toolbar_abbreviation_1);
        team2ToolbarAbbreviation = findViewById(R.id.officiate_game_toolbar_abbreviation_2);
        officiateGameStart = findViewById(R.id.officiate_game_start);
        quarterNumber = findViewById(R.id.officiate_game_quarter);
        scoreByQuarterTeam1 = findViewById(R.id.officiate_game_by_quarter_team_1);
        scoreByQuarterTeam2 = findViewById(R.id.officiate_game_by_quarter_team_2);
        quarter1 = findViewById(R.id.officiate_game_quarter_1);
        quarter2 = findViewById(R.id.officiate_game_quarter_2);
        quarter3 = findViewById(R.id.officiate_game_quarter_3);
        quarter4 = findViewById(R.id.officiate_game_quarter_4);
        finished = findViewById(R.id.officiate_game_game_finished);
        team1ScoreQ1 = findViewById(R.id.officiate_game_team_1_score_q1);
        team1ScoreQ2 = findViewById(R.id.officiate_game_team_1_score_q2);
        team1ScoreQ3 = findViewById(R.id.officiate_game_team_1_score_q3);
        team1ScoreQ4 = findViewById(R.id.officiate_game_team_1_score_q4);
        team2ScoreQ1 = findViewById(R.id.officiate_game_team_2_score_q1);
        team2ScoreQ2 = findViewById(R.id.officiate_game_team_2_score_q2);
        team2ScoreQ3 = findViewById(R.id.officiate_game_team_2_score_q3);
        team2ScoreQ4 = findViewById(R.id.officiate_game_team_2_score_q4);
        team1ScoreTotal = findViewById(R.id.officiate_game_team_1_total);
        team2ScoreTotal = findViewById(R.id.officiate_game_team_2_total);

        startGameQuarter = findViewById(R.id.officiate_game_start_game_quarter);
        quarterTimer = findViewById(R.id.officiate_game_quarter_timer);

        quarter = 0;

        quarterCountdown = findViewById(R.id.officiate_game_quarter_countdown);
        quarterTime = findViewById(R.id.officiate_game_quarter_time);

        quarterIsPlaying = false;
        quarterCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quarterIsPlaying){
                    pauseQuarter();
                } else {
                    resumeQuarter();
                }
            }
        });

        toolbar = findViewById(R.id.officiate_game_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);

        viewPager = findViewById(R.id.officiate_game_view_pager);

        TabLayout tabs = findViewById(R.id.officiate_game_tab_layout);
        tabs.setupWithViewPager(viewPager);

        startGame();
        setTeamData();
        getIntentData();
        displayHowToUseInformation();
        setupViewPager(viewPager);
    }

    private void displayHowToUseInformation() {
        final String message = getResources().getString(R.string.how_to_use);
        final AlertDialog informationMessage = new AlertDialog.Builder(this)
                .create();

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationMessage.setMessage(message);
                informationMessage.setTitle(TITLE);
                informationMessage.setButton(Dialog.BUTTON_NEUTRAL, "Close",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        informationMessage.dismiss();
                    }
                });
                informationMessage.show();
            }
        });
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
                    if(teamData.getKey().equals("Score")){
                        if(team == "Team 1"){
                            getTeamScores(teamData, 1);
                        } else {
                            getTeamScores(teamData, 2);
                        }
                    }
                }
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
                }
            }

        }
    }

    private void startGame() {
        officiateGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quarter < 4){
                    startGameQuarter.setVisibility(View.INVISIBLE);
                    quarterTimer.setVisibility(View.VISIBLE);
                    timeRemaining = TIME_PER_QUARTER;
                    setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                    resumeQuarter();
                    if(quarter == 0){
                        allScore("Team 1");
                        allScore("Team 2");
                        setCurrentQuarter("Q1");
                        officiateGameStart.setText(QUARTER_2);
                        setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                    } else if(quarter == 1){
                        allScore("Team 1");
                        allScore("Team 2");
                        setCurrentQuarter("Q2");
                        officiateGameStart.setText(QUARTER_3);
                        setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                        quarterNumber.setText("Q2");
                    } else if(quarter == 2){
                        allScore("Team 1");
                        allScore("Team 2");
                        setCurrentQuarter("Q3");
                        officiateGameStart.setText(QUARTER_4);
                        setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                        quarterNumber.setText("Q3");
                    } else {
                        allScore("Team 1");
                        allScore("Team 2");
                        setCurrentQuarter("Q4");
                        officiateGameStart.setText("Click to Complete");
                        setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                        quarterNumber.setText("Q4");
                    }
                } else {
                    setCurrentQuarter("Total");
                    officiateGameStart.setText(FINISHED);
                    setColour(quarter1, quarter2, quarter3, quarter4, quarter);
                }
                quarter++;
            }
        });
    }

    private void setColour(TextView firstQuarter, TextView secondQuarter, TextView thirdQuarter,
                           TextView fourthQuarter, int numberOfCompleteQuarters){
        if(numberOfCompleteQuarters == 0){
            firstQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorAccent));
            secondQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
            thirdQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
            fourthQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
        } else if(numberOfCompleteQuarters == 1){
            firstQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            secondQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorAccent));
            thirdQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
            fourthQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
        } else if(numberOfCompleteQuarters == 2){
            firstQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            secondQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            thirdQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorAccent));
            fourthQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorPrimary));
        } else if(numberOfCompleteQuarters == 3){
            firstQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            secondQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            thirdQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            fourthQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorAccent));
        } else {
            firstQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            secondQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            thirdQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            fourthQuarter.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
            finished.setTextColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorBlack));
        }
    }

    private void setCurrentQuarter(final String quarter) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child("Game "+gameId)
                        .child("CurrentQuarter")
                        .setValue(quarter);
                databaseReference.child("Game "+gameId)
                         .child("Type")
                         .setValue("Live");
                if(quarter == "Total"){
                    writeGameBreakdown();
                } else {
                    databaseReference.child("Game "+gameId)
                            .child("Type")
                            .setValue("Live");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO: open up a new activity to let officials enter data
    private void writeGameBreakdown() {
        Intent breakdown = new Intent(this, BreakdownActivity.class);
        breakdown.putExtra(GAME_ID, gameId);
        startActivity(breakdown);
    }

    private void allScore(final String team){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child("Game "+gameId)
                .child(team)
                .child("Score")
                .child("Total");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                score = Integer.valueOf(dataSnapshot.getValue().toString());
                if(team == "Team 1"){
                    setLiveGameInDatabase(score, "Team1QuarterScore");
                } else {
                    setLiveGameInDatabase(score, "Team2QuarterScore");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setLiveGameInDatabase(int score, String team){
        databaseReferenceTotalScore = FirebaseDatabase.getInstance()
                .getReference("Live");
        databaseReferenceTotalScore.child("Game " + gameId)
                .child(team)
                .setValue(score);
    }


    private void pauseQuarter() {
        countDownTimer.cancel();
        quarterIsPlaying = false;

        quarterCountdown.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_play_arrow_white_24dp));
    }
    private void resumeQuarter() {
        canAddScores = true;
        // called every second
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        quarterIsPlaying = true;
        quarterCountdown.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_pause_black_24dp));
    }

    private void updateTimer() {
        int minutes = (int) timeRemaining / 1000 / 60;
        int seconds = (int) timeRemaining / 1000 % 60;

        String remaining = String.format("%02d:%02d", minutes, seconds);

        quarterTime.setText(remaining);

        if(remaining.equals("00:00")){
            startGameQuarter.setVisibility(View.VISIBLE);
            quarterTimer.setVisibility(View.INVISIBLE);
            canAddScores = false;
        }
    }
    private void getIntentData() {
        team1 = getIntent().getStringExtra(TEAM_1_ABBREVIATION);
        team2 = getIntent().getStringExtra(TEAM_2_ABBREVIATION);
        logo1 = getIntent().getStringExtra(TEAM_1_LOGO);
        logo2 = getIntent().getStringExtra(TEAM_2_LOGO);
        nickname1 = getIntent().getStringExtra(TEAM_1_NICKNAME);
        nickname2 = getIntent().getStringExtra(TEAM_2_NICKNAME);

        setIntentData();
    }

    private void setIntentData() {
        Glide.with(getApplicationContext())
                .load(logo1)
                .into(team1ToolbarLogo);
        Glide.with(getApplicationContext())
                .load(logo2)
                .into(team2ToolbarLogo);
        team1ToolbarAbbreviation.setText(team1);
        team2ToolbarAbbreviation.setText(team2);
        scoreByQuarterTeam1.setText(team1);
        scoreByQuarterTeam2.setText(team2);
    }

    private void setupViewPager(ViewPager viewPager){
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OfficiateGameTeam1RosterScore(), nickname1);
        adapter.addFragment(new OfficiateGameTeam2RosterScore(), nickname2);
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
