package project.projectapp.GamesFragment.GameResult;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import project.projectapp.R;

public class TeamBreakdownRecyclerViewAdapter extends RecyclerView.Adapter<TeamBreakdownRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String>  playerNames, playerNumbers, playerScores, playerFouls;

    private Context context;

    public TeamBreakdownRecyclerViewAdapter(Context thisContext, ArrayList<String> playerName,
                                            ArrayList<String> playerNumber, ArrayList<String> playerScore,
                                            ArrayList<String> playerFoul){
        context = thisContext;
        playerNames = playerName;
        playerNumbers = playerNumber;
        playerScores = playerScore;
        playerFouls = playerFoul;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_team_breakdown_list, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.number.setText(playerNumbers.get(position));
        holder.name.setText(playerNames.get(position));
        holder.score.setText(playerScores.get(position));
        holder.fouls.setText(playerFouls.get(position));
    }

    @Override
    public int getItemCount() {
        return playerNames.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        TextView number;
        TextView name;
        TextView score;
        TextView fouls;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.team_1_breakdown_parent_layout);
            number = itemView.findViewById(R.id.team_1_breakdown_player_number);
            name = itemView.findViewById(R.id.team_1_breakdown_parent_name);
            score = itemView.findViewById(R.id.team_1_breakdown_player_score);
            fouls = itemView.findViewById(R.id.team_1_breakdown_player_fouls);

        }
    }
}
