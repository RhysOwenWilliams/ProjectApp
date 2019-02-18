package project.projectapp.GamesFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.projectapp.R;


public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>{

    private RelativeLayout fixtureView;
    private LinearLayout resultsView;

    private ArrayList<String> allTeam1, allTeam2, gameDates, gameTimes, gameTypes, gameLocations,
        team1Scores, team2Scores, team1Wins, team1Losses, team2Wins, team2Losses;

    private Context context;

    public GamesRecyclerViewAdapter(Context thisContext, ArrayList<String> team1,
                                    ArrayList<String> team2, ArrayList<String> gameDate,
                                    ArrayList<String> gameTime, ArrayList<String> gameType,
                                    ArrayList<String> gameLocation, ArrayList<String> team1Score,
                                    ArrayList<String> team2Score, ArrayList<String> team1Win,
                                    ArrayList<String> team1Loss, ArrayList<String> team2Win,
                                    ArrayList<String> team2Loss){
        context = thisContext;
        allTeam1 = team1;
        allTeam2 = team2;
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_fixtures_results_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

        fixtureView = view.findViewById(R.id.fixtures_display);
        resultsView = view.findViewById(R.id.results_display);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.team1Nickname.setText(allTeam1.get(position));
        holder.team2Nickname.setText(allTeam2.get(position));
        holder.team1Record.setText(team1Wins.get(position) + " - " + team1Losses.get(position));
        holder.team2Record.setText(team2Wins.get(position) + " - " + team2Losses.get(position));

        if(gameTypes.get(position).equals("Result")){
            resultsView.setVisibility(View.VISIBLE);
            holder.team1Score.setText(team1Scores.get(position));
            holder.team2Score.setText(team2Scores.get(position));
        } else {
            fixtureView.setVisibility(View.VISIBLE);
            holder.time.setText(gameTimes.get(position));
            holder.date.setText(gameDates.get(position));
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked: " + allTeam1.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return allTeam2.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
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
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fixture_results_list_parent);
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
        }
    }
}
