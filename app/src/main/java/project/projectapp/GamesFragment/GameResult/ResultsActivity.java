package project.projectapp.GamesFragment.GameResult;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
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

import project.projectapp.GamesFragment.GameLive.Team1RosterScore;
import project.projectapp.GamesFragment.GameLive.Team2RosterScore;
import project.projectapp.PagerAdapter;
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
    private final String GAME_ID = "gameIds";
    private final String TEAM_1_COLOURS = "team1Colours";
    private final String TEAM_2_COLOURS = "team2Colours";

    private int colour1, colour2;
    private String score1, score2, nickname1, nickname2, logo1, logo2, abbreviation1,
            abbreviation2, scoreQ11, scoreQ21, scoreQ31, scoreQ41,
            scoreQ12, scoreQ22, scoreQ32, scoreQ42;

    private ImageView team1Logo, team2Logo, team1ToolbarLogo, team2ToolbarLogo;
    private TextView team1Nickname, team2Nickname, team1Score, team2Score, team1ToolbarName,
            team2ToolbarName, team1ByQuarterName, team2ByQuarterName, team1Total, team2Total,
            team1ScoreQ1, team1ScoreQ2, team1ScoreQ3, team1ScoreQ4, team2ScoreQ1, team2ScoreQ2,
            team2ScoreQ3, team2ScoreQ4;
    private View team1Colour, team2Colour;

    private LinearLayout information;

    private Animation slideUp, slideDown;

    private Toolbar toolbar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

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
        team1Colour = findViewById(R.id.results_team_1_colour);
        team2Colour = findViewById(R.id.results_team_2_colour);

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

        viewPager = findViewById(R.id.results_game_view_pager);

        TabLayout tabs = findViewById(R.id.results_game_tab_layout);
        tabs.setupWithViewPager(viewPager);

        retrieveIntentData();
        addInfoWhenCollapsed();
        setLayoutData();
    }

    private void retrieveIntentData(){
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
        colour1 = Color.parseColor(getIntent().getStringExtra(TEAM_1_COLOURS));
        colour2 = Color.parseColor(getIntent().getStringExtra(TEAM_2_COLOURS));
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
        team1Colour.setBackgroundColor(colour1);
        team2Colour.setBackgroundColor(colour2);

        setupViewPager(viewPager);
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

    private void setupViewPager(ViewPager viewPager){
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BreakdownResultsFragment(), "Breakdown");
        adapter.addFragment(new Team1ResultFragment(), nickname1);
        adapter.addFragment(new Team2ResultFragment(), nickname2);
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
