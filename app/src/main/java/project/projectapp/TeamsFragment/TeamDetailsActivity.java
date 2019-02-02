package project.projectapp.TeamsFragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import project.projectapp.R;

public class TeamDetailsActivity extends AppCompatActivity {

    private static final String TEAM_DETAILS_TITLE = "Team Details";

    private static final String TEAM_NAME = "teamName";
    private static final String TEAM_ADDRESS = "teamAddress";
    private static final String TEAM_CONTACT_NAME = "teamContactName";
    private static final String TEAM_CONTACT_EMAIL_ADDRESS = "teamContactEmailAddress";
    private static final String TEAM_WINS = "teamWins";
    private static final String TEAM_LOSSES = "teamLosses";
    private static final String TEAM_KIT_COLOUR = "teamKitColour";
    private static final String TEAM_LOCATION_LATITUDE = "teamLocationLatitude";
    private static final String TEAM_LOCATION_LONGITUDE = "teamLocationLongitude";

    private Button viewLocation;
    private ImageView logo;
    private TextView teamName, teamRecord, teamContactName, teamContactEmailAddress,
        teamAddress, teamKitColour;
    private Toolbar toolbar;

    private String retrievedTeamName, retrievedTeamWins, retrievedTeamLosses,
            retrievedTeamContactName, retrievedTeamContactEmailAddress, retrievedTeamAddress,
            retrievedTeamLocationLatitude, retrievedTeamLocationLongitude, retrievedTeamKitColour;
    private int teamLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        viewLocation = findViewById(R.id.button_team_location);
        teamName = findViewById(R.id.text_view_selected_name);
        teamRecord = findViewById(R.id.text_view_selected_record);
        teamContactName = findViewById(R.id.text_view_selected_contact_name);
        teamContactEmailAddress = findViewById(R.id.text_view_selected_contact_email_address);
        teamAddress = findViewById(R.id.text_view_selected_address);
        teamKitColour = findViewById(R.id.text_view_selected_kit_colour);
        logo = findViewById(R.id.team_details_logo);

        toolbar = findViewById(R.id.team_details_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);

        viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMapsView();
            }
        });

        retrieveTeamDetails();
    }

    private void toMapsView() {
        Intent maps = new Intent(TeamDetailsActivity.this, TeamDetailsMapsLocation.class);
        maps.putExtra("latitude", retrievedTeamLocationLatitude);
        maps.putExtra("longitude", retrievedTeamLocationLongitude);
        startActivity(maps);
    }

    private void retrieveTeamDetails(){
        retrievedTeamName = getIntent().getStringExtra(TEAM_NAME);
        retrievedTeamAddress = getIntent().getStringExtra(TEAM_ADDRESS);
        retrievedTeamContactName = getIntent().getStringExtra(TEAM_CONTACT_NAME);
        retrievedTeamContactEmailAddress = getIntent().getStringExtra(TEAM_CONTACT_EMAIL_ADDRESS);
        retrievedTeamWins = getIntent().getStringExtra(TEAM_WINS);
        retrievedTeamLosses = getIntent().getStringExtra(TEAM_LOSSES);
        retrievedTeamLocationLatitude = getIntent().getStringExtra(TEAM_LOCATION_LATITUDE);
        retrievedTeamLocationLongitude = getIntent().getStringExtra(TEAM_LOCATION_LONGITUDE);
        retrievedTeamKitColour = getIntent().getStringExtra(TEAM_KIT_COLOUR);

        setDisplayData();
    }

    private void setDisplayData(){
        teamName.setText(retrievedTeamName);
        teamRecord.setText(retrievedTeamWins + " - " + retrievedTeamLosses);
        teamContactName.setText(retrievedTeamContactName);
        teamContactEmailAddress.setText(retrievedTeamContactEmailAddress);
        teamAddress.setText(formatAddress(retrievedTeamAddress));
        teamKitColour.setText("Kit Colourway: " + retrievedTeamKitColour);

        getTeamLogo(retrievedTeamName);
    }

    private void getTeamLogo(String teamName){
        String name = teamName.replace(" ", "_")
                .toLowerCase();
        String formatToDrawable = "@drawable/" + name;
        int image = getResources().getIdentifier(formatToDrawable,null, getPackageName());
        Drawable res = getResources().getDrawable(image);
        logo.setImageDrawable(res);
    }

    private String formatAddress(String address){
        String formatted = address.replace("; ", ", ");
        return formatted;
    }

    /**
     * Called when an option is selected, in this case the back button since it is the only option
     * @param item - our menu items, so the back button
     * @return - the parent class' onOptionsItemSelected method
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
