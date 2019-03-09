package project.projectapp.GamesFragment.GameLive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import project.projectapp.GamesFragment.GameResult.TeamBreakdownRecyclerViewAdapter;
import project.projectapp.R;
import project.projectapp.TeamsFragment.TeamDetailsMapsLocation;

public class Team1RosterScore extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_1_live_fragment, container, false);

        return view;
    }
}