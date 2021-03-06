package project.projectapp.TeamsFragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import project.projectapp.PagerAdapter;
import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamRoster.TeamRosterFragment;

public class TeamDetailsActivity extends AppCompatActivity {

    private static final String TEAM_NAME = "teamName";
    private static final String TEAM_WINS = "teamWins";
    private static final String TEAM_LOSSES = "teamLosses";
    private static final String TEAM_LOGO = "teamLogo";
    private static final String TEAM_NICKNAME = "teamNickname";
    private static final String TEAM_COLOUR = "teamColour";

    private ImageView teamLogo, toolbarTeamLogo;
    private TextView teamName, teamRecord, teamNickname;
    private Toolbar toolbar;
    private TabLayout tabs;
    private LinearLayout information;
    private View splitBar;

    private int colour;
    private String retrievedTeamName, retrievedTeamWins, retrievedTeamLosses, retrievedTeamLogo,
            retrievedTeamNickname, retrievedColour;

    private Animation fadeIn, fadeOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        teamLogo = findViewById(R.id.team_details_logo);
        toolbarTeamLogo = findViewById(R.id.team_details_toolbar_logo);
        teamName = findViewById(R.id.team_details_team_name);
        teamRecord = findViewById(R.id.team_details_record);
        teamNickname = findViewById(R.id.team_details_toolbar_nickname);
        splitBar = findViewById(R.id.team_details_split_bar);

        information = findViewById(R.id.team_details_toolbar_information);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        toolbar = findViewById(R.id.team_details_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);


        ViewPager viewPager = findViewById(R.id.team_details_view_pager);
        setupViewPager(viewPager);

        tabs = findViewById(R.id.team_details_tab_layout);
        tabs.setupWithViewPager(viewPager);

        retrieveTeamDetails();
        addInfoWhenCollapsed();
    }

    private void retrieveTeamDetails(){
        retrievedTeamName = getIntent().getStringExtra(TEAM_NAME);
        retrievedTeamWins = getIntent().getStringExtra(TEAM_WINS);
        retrievedTeamLosses = getIntent().getStringExtra(TEAM_LOSSES);
        retrievedTeamLogo = getIntent().getStringExtra(TEAM_LOGO);
        retrievedTeamNickname = getIntent().getStringExtra(TEAM_NICKNAME);
        retrievedColour = getIntent().getStringExtra(TEAM_COLOUR);
        setTeamDetails();
    }

    private void setTeamDetails(){
        Glide.with(this)
                .load(retrievedTeamLogo)
                .into(teamLogo);
        Glide.with(this)
                .load(retrievedTeamLogo)
                .into(toolbarTeamLogo);
        teamName.setText(retrievedTeamName);
        teamRecord.setText(retrievedTeamWins + " - " + retrievedTeamLosses);
        teamNickname.setText(retrievedTeamNickname);

        colour = Color.parseColor(retrievedColour);
        splitBar.setBackgroundColor(colour);
        tabs.setSelectedTabIndicatorColor(colour);
    }

    private void addInfoWhenCollapsed() {
        AppBarLayout appBarLayout = findViewById(R.id.team_details_appbar);
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

    private void setupViewPager(ViewPager viewPager){
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TeamBioFragment(), "Bio");
        adapter.addFragment(new TeamRosterFragment(), "Roster");
        viewPager.setAdapter(adapter);
    }

    /**
     * Called when an option is selected, in this case the back button since it is the only option
     * @param item - our menu items, so the back button
     * @return - the parent class' onOptionsItemSelected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Default onBackPressed, returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
