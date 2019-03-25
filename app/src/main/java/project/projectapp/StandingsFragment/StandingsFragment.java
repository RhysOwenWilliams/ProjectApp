package project.projectapp.StandingsFragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import project.projectapp.R;

public class StandingsFragment extends Fragment {

    private DatabaseReference databaseReference;

    private String teamName, logo;
    private int loss, wins, points;

    private HashMap<String, Integer> standings, sortedStandings;

    private ArrayList<String> teamNames, teamWins, teamLosses, teamWinPercentages, teamTotalPoints,
            teamLogo;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standings_fragment, container, false);

        standings = new HashMap<>();
        sortedStandings = new HashMap<>();

        teamNames = new ArrayList<>();
        teamWins = new ArrayList<>();
        teamLosses = new ArrayList<>();
        teamWinPercentages = new ArrayList<>();
        teamLogo = new ArrayList<>();
        teamTotalPoints = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar_standings);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        addAllTeamStandingsData(view);

        return view;
    }

    private void addAllTeamStandingsData(View view){
        final View VIEW = view;

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Teams");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot myData : dataSnapshot.getChildren()){
                    teamName = myData.getKey();
                    getTeamWinsLosses(myData);
                    scoreTotal(wins, loss);
                    String mapKey = teamName + ", " + wins + ", " + loss + ", " + logo;

                    standings.put(mapKey, points);
                    sortByValue(standings);
                    recyclerViewSetup(VIEW);
                }
                organiseStandingsColumns(sortedStandings);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /**
     * Separates the database data and stores them into strings
     * @param dataSnapshot
     */
    private void getTeamWinsLosses(DataSnapshot dataSnapshot){
        teamName = dataSnapshot.getKey();

        String data = String.valueOf(dataSnapshot.getValue());
        String removeCurlyBrackets = data.replace("{", "")
                .replace("}", "");
        String[] splitData = removeCurlyBrackets.split(",");

        for(int i = 0; i < splitData.length; i++){
            setWinsLoss(splitData[6], splitData[3]);
            String[] split = splitData[10].split("=", 2);
            logo = split[1];
        }
    }

    /**
     * Gets the wins and losses, formats it and then stores it in variables
     * @param winsValue number of wins
     * @param lossValue number of losses
     */
    private void setWinsLoss(String winsValue, String lossValue){
        String[] splitWins = winsValue.split("=");
        String[] splitLoss = lossValue.split("=");

        wins = Integer.valueOf(splitWins[1]);
        loss = Integer.valueOf(splitLoss[1]);
    }

    /**
     * Formats the total points of a team by convert a single win to 2 points, then adding the
     * number of losses since losses count as 1
     * @param winsValue
     * @param lossValue
     */
    private void scoreTotal(int winsValue, int lossValue){
        points = 0;
        for(int i = 0; i < winsValue; i++){
            points += 2;
        }

        points = points + lossValue;
    }

    /**
     * Used to sort the standings into the correct order of most points at the top and least at
     * the bottom. If points are equal, then they are sorted by their name alphabetically
     * @param standings
     */
    public void sortByValue(HashMap<String, Integer> standings){
        List<Map.Entry<String, Integer> > list = new LinkedList<>(standings.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if(o1.getValue() != o2.getValue()){
                    return (o2.getValue()).compareTo(o1.getValue());
                } else {
                    return (o1.getKey()).compareTo(o2.getKey());
                }
            }
        });

        HashMap<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> m : list) {
            sorted.put(m.getKey(), m.getValue());
        }

        sortedStandings = sorted;
    }

    private void organiseStandingsColumns(Map<String, Integer> standings){
        for(HashMap.Entry<String, Integer> s : standings.entrySet()){
            String[] splitKey = s.getKey().split(", ");
            String team = splitKey[0];
            String wins = splitKey[1];
            String losses = splitKey[2];
            String logo = splitKey[3];
            String winPercentage = calculateWinPercentage(wins, losses);
            String totalPoints = String.valueOf(s.getValue());

            teamLogo.add(logo);
            teamNames.add(team);
            teamWins.add(wins);
            teamLosses.add(losses);
            teamWinPercentages.add(winPercentage);
            teamTotalPoints.add(totalPoints);
        }
    }

    private String calculateWinPercentage(String wins, String losses){
        double totWins = Integer.valueOf(wins);
        double totLosses = Integer.valueOf(losses);
        double totOverall = totWins + totLosses;

        // Correctly formats the win percentage to 3 digits beyond the decimal
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String percentage = decimalFormat.format(totWins/totOverall);

        // Ensures that all win percentages are formatted correctly
        if(percentage.length() == 3){
            percentage = percentage + "00";
        } else if(percentage.length() == 4){
            percentage = percentage + "0";
        }

        return percentage;
    }

    private void recyclerViewSetup(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_standings);
        StandingsRecyclerViewAdapter adapter = new StandingsRecyclerViewAdapter(getContext(),
                teamNames, teamWins, teamLosses, teamWinPercentages, teamLogo, teamTotalPoints);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //The recyclerview is done loading and we can now hide the progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }
}
