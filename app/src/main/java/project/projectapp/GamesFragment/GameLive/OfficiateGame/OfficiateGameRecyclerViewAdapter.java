package project.projectapp.GamesFragment.GameLive.OfficiateGame;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import project.projectapp.R;

/**
 * Used to add the recyclerview for our teams, handles clicks etc on the recyclerview
 */
public class OfficiateGameRecyclerViewAdapter extends RecyclerView
        .Adapter<OfficiateGameRecyclerViewAdapter.ViewHolder> {

    private String gameId, team;

    private ArrayList<String> playerNames, playerNumbers, onePointers, twoPointers,
            threePointers, fouls;

    private Context context;

    private DatabaseReference databaseReference, databaseReferenceRunningQuarter;

    public OfficiateGameRecyclerViewAdapter(Context context, ArrayList<String> playerNames,
                                            ArrayList<String> playerNumbers,
                                            ArrayList<String> onePointers,
                                            ArrayList<String> twoPointers,
                                            ArrayList<String> threePointers,
                                            ArrayList<String> fouls, String gameId, String team){
        this.context = context;
        this.playerNames = playerNames;
        this.playerNumbers = playerNumbers;
        this.onePointers = onePointers;
        this.twoPointers = twoPointers;
        this.threePointers = threePointers;
        this.fouls = fouls;
        this.gameId = gameId;
        this.team = team;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_player_live_score_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.playerNumber.setText("#"+playerNumbers.get(position));
        holder.playerName.setText(playerNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.playerScoresFouls.toggle();
                if(holder.playerScoresFouls.isExpanded()){
                    holder.triangleUpDown.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else {
                    holder.triangleUpDown.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }
            }
        });
        holder.playerOnePointers.setText(onePointers.get(position));
        holder.playerTwoPointers.setText(twoPointers.get(position));
        holder.playerThreePointers.setText(threePointers.get(position));
        holder.playerFouls.setText(fouls.get(position));

        incrementPlayerData(holder.incrementOnePointers, position);
        incrementPlayerData(holder.incrementTwoPointers, position);
        incrementPlayerData(holder.incrementThreePointers, position);
        incrementPlayerData(holder.incrementFouls, position);
        decrementPlayerData(holder.decrementOnePointers, position);
        decrementPlayerData(holder.decrementTwoPointers, position);
        decrementPlayerData(holder.decrementThreePointers, position);
        decrementPlayerData(holder.decrementFouls, position);
    }

    private void incrementPlayerData(ImageButton button, final int position){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.officiate_game_increment_one_pointer){
                    alterPlayerData(position, "1-pointers", "increment");
                } else if(v.getId() == R.id.officiate_game_increment_two_pointer){
                    alterPlayerData(position, "2-pointers", "increment");
                } else if(v.getId() == R.id.officiate_game_increment_three_pointer){
                    alterPlayerData(position, "3-pointers", "increment");
                } else if(v.getId() == R.id.officiate_game_increment_foul){
                    alterPlayerData(position, "fouls", "increment");
                }
            }
        });
    }

    private void decrementPlayerData(ImageButton button, final int position){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.officiate_game_decrement_one_pointer){
                    alterPlayerData(position, "1-pointers", "decrement");
                } else if(v.getId() == R.id.officiate_game_decrement_two_pointer){
                    alterPlayerData(position, "2-pointers", "decrement");
                } else if(v.getId() == R.id.officiate_game_decrement_three_pointer){
                    alterPlayerData(position, "3-pointers", "decrement");
                } else if(v.getId() == R.id.officiate_game_decrement_foul){
                    alterPlayerData(position, "fouls", "decrement");
                }
            }
        });
    }

    private void alterPlayerData(int position, final String dataToChange,
                                 final String incrementOrDecrement){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child("Game " + gameId)
                .child(team)
                .child("Players")
                .child("Player " + getPlayerPosition(position))
                .child("data")
                .child(dataToChange);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
                        if(incrementOrDecrement == "increment"){
                            databaseReference.setValue(value + 1);
                        } else if (incrementOrDecrement == "decrement"){
                            if(value != 0){
                                databaseReference.setValue(value - 1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String getPlayerPosition(int position){
        switch (position){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
            case 10:
                return "K";
            case 11:
                return "L";
            default:
                return null;
        }
    }


    @Override
    public int getItemCount() {
        return onePointers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
        ExpandableLayout playerScoresFouls;
        TextView playerName;
        TextView playerNumber;
        TextView playerOnePointers;
        TextView playerTwoPointers;
        TextView playerThreePointers;
        TextView playerFouls;
        ImageButton incrementOnePointers;
        ImageButton incrementTwoPointers;
        ImageButton incrementThreePointers;
        ImageButton incrementFouls;
        ImageButton decrementOnePointers;
        ImageButton decrementTwoPointers;
        ImageButton decrementThreePointers;
        ImageButton decrementFouls;
        ImageView triangleUpDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.officiate_game_team_parent_layout);
            playerScoresFouls = itemView.findViewById(R.id.officiate_game_team_expanding_layout);
            playerName = itemView.findViewById(R.id.officiate_game_players);
            playerNumber = itemView.findViewById(R.id.officiate_game_numbers);
            playerOnePointers = itemView.findViewById(R.id.officiate_game_one_pointer_score);
            playerTwoPointers = itemView.findViewById(R.id.officiate_game_two_pointer_score);
            playerThreePointers = itemView.findViewById(R.id.officiate_game_three_pointer_score);
            playerFouls = itemView.findViewById(R.id.officiate_game_fouls_count);
            incrementOnePointers = itemView.findViewById(R.id.officiate_game_increment_one_pointer);
            incrementTwoPointers = itemView.findViewById(R.id.officiate_game_increment_two_pointer);
            incrementThreePointers = itemView.findViewById(R.id.officiate_game_increment_three_pointer);
            incrementFouls = itemView.findViewById(R.id.officiate_game_increment_foul);
            decrementOnePointers = itemView.findViewById(R.id.officiate_game_decrement_one_pointer);
            decrementTwoPointers = itemView.findViewById(R.id.officiate_game_decrement_two_pointer);
            decrementThreePointers = itemView.findViewById(R.id.officiate_game_decrement_three_pointer);
            decrementFouls = itemView.findViewById(R.id.officiate_game_decrement_foul);
            triangleUpDown = itemView.findViewById(R.id.officiate_game_team_image);
        }
    }
}
