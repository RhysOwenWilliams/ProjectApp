package project.projectapp.StandingsFragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;

import project.projectapp.R;

public class StandingsRecyclerViewAdapter extends RecyclerView.Adapter<StandingsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> teamNames, teamWins, teamLosses, teamWinPercentages, teamTotalPoints,
            teamStandingsLocation, teamLogo;

    private Context context;

    public StandingsRecyclerViewAdapter(Context context, ArrayList<String> teamNames, ArrayList<String> teamWins,
                                        ArrayList<String> teamLosses, ArrayList<String> teamWinPercentages,
                                        ArrayList<String> teamLogo, ArrayList<String> teamTotalPoints) {
        this.context = context;
        this.teamNames = teamNames;
        this.teamWins = teamWins;
        this.teamLosses = teamLosses;
        this.teamWinPercentages = teamWinPercentages;
        this.teamLogo = teamLogo;
        this.teamTotalPoints = teamTotalPoints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_standings_list_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        teamStandingsLocation = new ArrayList<>();
        return holder;
    }

    /**
     *
     */
    private void setTeamStandingsLocation(){
        for(int i = 1; i <= teamNames.size(); i++){
            teamStandingsLocation.add(String.valueOf(i));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setTeamStandingsLocation();
        holder.teamPosition.setText(teamStandingsLocation.get(position));
        holder.name.setText(teamNames.get(position));
        holder.wins.setText(teamWins.get(position));
        holder.loss.setText(teamLosses.get(position));
        holder.winPercentage.setText(teamWinPercentages.get(position));
        holder.totalPoints.setText(teamTotalPoints.get(position));

        Glide.with(context)
                .load(teamLogo.get(position))
                .into(holder.logo);

        // Removes the click animation since it appears like the click should do something otherwiSe
        holder.parentLayout.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return teamNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView teamPosition;
        ImageView logo;
        TextView name;
        TextView wins;
        TextView loss;
        TextView winPercentage;
        TextView totalPoints;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamPosition = itemView.findViewById(R.id.text_view_position_standings);
            logo = itemView.findViewById(R.id.image_view_standings);
            name = itemView.findViewById(R.id.text_view_team_name_standings);
            wins = itemView.findViewById(R.id.text_view_wins_standings);
            loss = itemView.findViewById(R.id.text_view_losses_standings);
            winPercentage = itemView.findViewById(R.id.text_view_win_percentage_standings);
            totalPoints = itemView.findViewById(R.id.text_view_win_total_points_standings);
            parentLayout = itemView.findViewById(R.id.standings_parent_layout);
        }
    }
}
