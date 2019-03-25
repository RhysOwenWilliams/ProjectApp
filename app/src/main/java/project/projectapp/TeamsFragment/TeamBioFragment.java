package project.projectapp.TeamsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import project.projectapp.R;

public class TeamBioFragment extends Fragment {


    private static final String TEAM_ADDRESS = "teamAddress";
    private static final String TEAM_CONTACT_NAME = "teamContactName";
    private static final String TEAM_CONTACT_EMAIL_ADDRESS = "teamContactEmailAddress";
    private static final String TEAM_KIT_COLOUR = "teamKitColour";
    private static final String TEAM_LOCATION_LATITUDE = "teamLocationLatitude";
    private static final String TEAM_LOCATION_LONGITUDE = "teamLocationLongitude";
    private static final String TEAM_COLOUR = "teamColour";

    private String retrievedTeamContactName, retrievedTeamContactEmailAddress, retrievedTeamAddress,
            retrievedTeamLocationLatitude, retrievedTeamLocationLongitude, retrievedTeamKitColour;

    private Button viewLocation;
    private TextView contactName, contactEmailAddress, teamAddress, teamKitColourway;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_bio_fragment, container, false);

        viewLocation = view.findViewById(R.id.selected_team_location);
        contactName = view.findViewById(R.id.selected_contact_name);
        contactEmailAddress = view.findViewById(R.id.selected_contact_email_address);
        teamAddress = view.findViewById(R.id.selected_address);
        teamKitColourway = view.findViewById(R.id.selected_kit_colourway);

        viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMapsView();
            }
        });

        retrieveTeamDetails();

        return view;
    }

    private void retrieveTeamDetails(){
        retrievedTeamContactName = getActivity().getIntent().getStringExtra(TEAM_CONTACT_NAME);
        retrievedTeamContactEmailAddress = getActivity().getIntent().getStringExtra(TEAM_CONTACT_EMAIL_ADDRESS);
        retrievedTeamAddress = getActivity().getIntent().getStringExtra(TEAM_ADDRESS);
        retrievedTeamLocationLatitude = getActivity().getIntent().getStringExtra(TEAM_LOCATION_LATITUDE);
        retrievedTeamLocationLongitude = getActivity().getIntent().getStringExtra(TEAM_LOCATION_LONGITUDE);
        retrievedTeamKitColour = getActivity().getIntent().getStringExtra(TEAM_KIT_COLOUR);
        setTeamDetails();
    }

    private void setTeamDetails(){
        contactName.setText(retrievedTeamContactName);
        contactEmailAddress.setText(retrievedTeamContactEmailAddress);
        teamAddress.setText(formatAddress(retrievedTeamAddress));
        teamKitColourway.setText(retrievedTeamKitColour);
    }


    private void toMapsView() {
        Intent maps = new Intent(getActivity(), TeamDetailsMapsLocation.class);
        maps.putExtra("latitude", retrievedTeamLocationLatitude);
        maps.putExtra("longitude", retrievedTeamLocationLongitude);
        startActivity(maps);

    }


    private String formatAddress(String address){
        String formatted = address.replace("; ", ", ");
        return formatted;
    }
}