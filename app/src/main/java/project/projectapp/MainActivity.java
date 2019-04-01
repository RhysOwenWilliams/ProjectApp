package project.projectapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.projectapp.Account.LoginActivity;
import project.projectapp.Account.ProfileActivity;
import project.projectapp.Account.SettingsActivity;
import project.projectapp.GamesFragment.GamesFragment;
import project.projectapp.MessageFragment.MessengerFragment;
import project.projectapp.NewsFragment.NewsFragment;
import project.projectapp.StandingsFragment.StandingsFragment;
import project.projectapp.TeamsFragment.TeamsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String APP_TITLE = "South Wales Basketball D2";

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private TextView userEmailAddress;
    private TextView userUsername;
    private ImageView userProfileImage;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;

    private NavigationView navigationView;
    private View view;

    private String email;
    private String user;
    private String profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.nav_games).setChecked(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
               new GamesFragment()).commit();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(APP_TITLE);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.closed);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_person_white_24dp);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);

        userEmailAddress = view.findViewById(R.id.signedInUserEmailAddress);
        userUsername = view.findViewById(R.id.signedInUserUsername);
        userProfileImage = view.findViewById(R.id.profile_icon_drawer);
        progressBar = view.findViewById(R.id.profile_icon_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        getUserDetails();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_news:
                            selectedFragment = new NewsFragment();
                            break;
                        case R.id.nav_messages:
                            selectedFragment = new MessengerFragment();
                            break;
                        case R.id.nav_games:
                            selectedFragment = new GamesFragment();
                            break;
                        case R.id.nav_standings:
                            selectedFragment = new StandingsFragment();
                            break;
                        case R.id.nav_teams:
                            selectedFragment = new TeamsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                overridePendingTransition(R.anim.slide_anim_left, R.anim.slide_out_anim_right);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                overridePendingTransition(R.anim.slide_anim_left, R.anim.slide_out_anim_right);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                Intent logout = new Intent(this, LoginActivity.class);
                startActivity(logout);
        }
        return true;
    }


    public void getUserDetails(){
        email = firebaseUser.getEmail();
        userEmailAddress.setText(email);
        user = firebaseUser.getDisplayName();
        userUsername.setText("Welcome " + user + "!");

        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .into(userProfileImage);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Used to reload the user details when activity is resumed, used for when a user changes their
     * details in the profile activity
     */
    @Override
    public void onResume() {
        getUserDetails();
        super.onResume();
    }
}
