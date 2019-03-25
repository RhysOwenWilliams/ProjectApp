package project.projectapp.TeamsFragment.TeamRoster;

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

public class RosterRecyclerViewAdapter extends RecyclerView.Adapter<RosterRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> playerNames, playerNumbers, playerPositions;

    private Context context;

    public RosterRecyclerViewAdapter(Context context, ArrayList<String> playerNames,
                                     ArrayList<String> playerNumbers,ArrayList<String> playerPositions){
        this.context = context;
        this.playerNames = playerNames;
        this.playerNumbers = playerNumbers;
        this.playerPositions = playerPositions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_roster_list, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.number.setText("#" + playerNumbers.get(position));
        holder.names.setText(playerNames.get(position));
        holder.position.setText(playerPositions.get(position));
    }

    @Override
    public int getItemCount() {
        return playerNames.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        TextView number;
        TextView names;
        TextView position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.roster_parent_layout);
            number = itemView.findViewById(R.id.roster_player_number);
            names = itemView.findViewById(R.id.roster_parent_name);
            position = itemView.findViewById(R.id.roster_player_position);
        }
    }
}
