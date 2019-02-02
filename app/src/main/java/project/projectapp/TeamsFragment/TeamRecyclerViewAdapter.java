package project.projectapp.TeamsFragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.R;

/**
 * Used to add the recyclerview for our teams, handles clicks etc on the recyclerview
 */
public class TeamRecyclerViewAdapter extends RecyclerView.Adapter<TeamRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "datapassing";

    private static final String TEAM_NAME = "teamName";
    private static final String TEAM_ADDRESS = "teamAddress";
    private static final String TEAM_CONTACT_NAME = "teamContactName";
    private static final String TEAM_CONTACT_EMAIL_ADDRESS = "teamContactEmailAddress";
    private static final String TEAM_WINS = "teamWins";
    private static final String TEAM_LOSSES = "teamLosses";
    private static final String TEAM_KIT_COLOUR = "teamKitColour";
    private static final String TEAM_LOCATION_LATITUDE = "teamLocationLatitude";
    private static final String TEAM_LOCATION_LONGITUDE = "teamLocationLongitude";

    private ArrayList<String> teamNames, selectedTeamData;
    private ArrayList<Integer> teamLogo;
    private Context context;

    private DatabaseReference databaseReference;

    private String teamName, address, contactName, loss, latitude, longitude, wins,
            contactEmailAddress, kitColour, selectedTeamName, selectedTeamWins, selectedTeamLosses,
            selectedTeamContactName, selectedTeamContactEmailAddress, selectedTeamAddress,
            selectedTeamLatitude, selectedTeamLongitude, selectedTeamKitColour;

    public TeamRecyclerViewAdapter(Context thisContext, ArrayList<String> names,
                                   ArrayList<Integer> logos){
        context = thisContext;
        teamNames = names;
        teamLogo = logos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_teams_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context)
                .asBitmap()
                .load(teamLogo.get(position))
                .into(holder.teamImage);
        Log.d("teams", teamNames.toString());
        holder.teamName.setText(teamNames.get(position));

        holder.teamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTeamDetails(position);
            }
        });

    }

    private void getTeamDetails(final int position) {
        selectedTeamData = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams")
                .child(teamNames.get(position));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = String.valueOf(dataSnapshot.getValue());
                String removeCurlyBrackets = data.replace("{", "")
                        .replace("}", "");
                String[] splitData = removeCurlyBrackets.split(",");

                for(int i = 0; i < splitData.length; i++){
                    address = splitData[0];
                    contactName = splitData[1];
                    loss = splitData[2];
                    latitude = splitData[3];
                    longitude = splitData[4];
                    wins = splitData[5];
                    contactEmailAddress = splitData[6];
                    kitColour = splitData[7];
                }
                teamName = teamNames.get(position);

                retrieveSelectedData(teamName, formatTeamData(address, contactName,
                        loss, latitude, longitude, wins, contactEmailAddress, kitColour));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void retrieveSelectedData(String name, ArrayList<String> selectedTeamDetails){
        for(int i = 0; i < selectedTeamDetails.size(); i++){
            selectedTeamName = name;
            selectedTeamWins = selectedTeamDetails.get(0);
            selectedTeamLosses = selectedTeamDetails.get(1);
            selectedTeamContactName = selectedTeamDetails.get(2);
            selectedTeamContactEmailAddress = selectedTeamDetails.get(3);
            selectedTeamAddress = selectedTeamDetails.get(4);
            selectedTeamLatitude = selectedTeamDetails.get(5);
            selectedTeamLongitude = selectedTeamDetails.get(6);
            selectedTeamKitColour = selectedTeamDetails.get(7);
        }

        Intent intent = new Intent(context, TeamDetailsActivity.class);
        intent.putExtra(TEAM_NAME, selectedTeamName);
        intent.putExtra(TEAM_WINS, selectedTeamWins);
        intent.putExtra(TEAM_LOSSES, selectedTeamLosses);
        intent.putExtra(TEAM_CONTACT_NAME, selectedTeamContactName);
        intent.putExtra(TEAM_CONTACT_EMAIL_ADDRESS, selectedTeamContactEmailAddress);
        intent.putExtra(TEAM_ADDRESS, selectedTeamAddress);
        intent.putExtra(TEAM_LOCATION_LATITUDE, selectedTeamLatitude);
        intent.putExtra(TEAM_LOCATION_LONGITUDE, selectedTeamLongitude);
        intent.putExtra(TEAM_KIT_COLOUR, selectedTeamKitColour);
        context.startActivity(intent);

    }

    public ArrayList<String> formatTeamData(String address, String contactName,
                                                  String loss, String latitude, String longitude,
                                                  String wins, String contactEmailAddress,
                                                  String kitColour){
        ArrayList<String> rawData = new ArrayList<>();
        rawData.add(wins);
        rawData.add(loss);
        rawData.add(contactName);
        rawData.add(contactEmailAddress);
        rawData.add(address);
        rawData.add(latitude);
        rawData.add(longitude);
        rawData.add(kitColour);

        ArrayList<String> formatted = new ArrayList<>();
        for(String data : rawData){
            String[] removeUnwanted = data.split("=");
            formatted.add(removeUnwanted[1]);
        }
        return formatted;
    }

    @Override
    public int getItemCount() {
        return teamLogo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView teamImage;
        TextView teamName;
        RelativeLayout teamLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamImage = itemView.findViewById(R.id.image_view_teams);
            teamName = itemView.findViewById(R.id.text_view_team_name_teams);
            teamLayout = itemView.findViewById(R.id.teams_parent_layout);
        }
    }
}