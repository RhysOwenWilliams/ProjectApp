package project.projectapp.GamesFragment.GameLive.OfficiateGame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.projectapp.Account.ProfileActivity;
import project.projectapp.MainActivity;
import project.projectapp.R;

public class BreakdownActivity extends AppCompatActivity {

    private final String GAME_ID = "gameIds";

    private Button completeBreakdown;
    private EditText gameBreakdown;

    private String gameId;

    private DatabaseReference databaseReferenceCompleteGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_game_breakdown);

        completeBreakdown = findViewById(R.id.post_game_breakdown_complete);
        gameBreakdown = findViewById(R.id.post_game_breakdown_text);

        completeBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeGame();
            }
        });
    }

    private void completeGame() {
        gameId = getIntent().getStringExtra(GAME_ID);
        final String breakdown = gameBreakdown.getText().toString();

        databaseReferenceCompleteGame = FirebaseDatabase.getInstance().getReference("Games");

        databaseReferenceCompleteGame.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReferenceCompleteGame.child("Game "+ gameId)
                        .child("Breakdown")
                        .setValue(breakdown);

                databaseReferenceCompleteGame.child("Game "+ gameId)
                        .child("Type")
                        .setValue("Result");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        Intent complete = new Intent(this, MainActivity.class);
        startActivity(complete);
    }
}