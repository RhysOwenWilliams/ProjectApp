package project.projectapp.GamesFragment.GameResult;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import project.projectapp.R;

public class BreakdownResultsFragment extends Fragment {

    private final String GAME_BREAKDOWN = "gameBreakdown";

    private TextView gameBreakdown;

    private String breakdown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.breakdown_results_fragment, container, false);

        breakdown = getActivity().getIntent().getStringExtra(GAME_BREAKDOWN);

        gameBreakdown = view.findViewById(R.id.results_game_breakdown_text);
        gameBreakdown.setText(breakdown);

        return view;
    }
}
