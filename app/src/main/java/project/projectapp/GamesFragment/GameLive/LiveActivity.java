package project.projectapp.GamesFragment.GameLive;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import project.projectapp.R;

//TODO: set the quarter score to null, then when a quarter has started change those values to 0 such that the 'LIVE Q..' can be changed accordingly
public class LiveActivity extends AppCompatActivity {

    private static final long TIME_PER_QUARTER = 600000;

    private Button quarterCountdown;
    private TextView quarterTime;

    private CountDownTimer countDownTimer;

    private boolean quarterIsPlaying;
    private int quarter;
    private long timeRemaining = TIME_PER_QUARTER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_game);

        quarterCountdown = findViewById(R.id.live_start_quarter_countdown);
        quarterTime = findViewById(R.id.live_quarter_time);

        quarterCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quarterIsPlaying){
                    pauseQuarter();
                } else {
                    resumeQuarter();
                }
            }
        });
    }

    private void pauseQuarter() {
        countDownTimer.cancel();
        quarterIsPlaying = false;
        quarterCountdown.setText("Resume");
    }
    private void resumeQuarter() {
        // called every second
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimer();
                quarter++;
            }

            @Override
            public void onFinish() {

            }
        }.start();

        quarterIsPlaying = true;
        quarterCountdown.setText("Pause");
    }

    private void updateTimer() {
        int minutes = (int) timeRemaining / 1000 / 60;
        int seconds = (int) timeRemaining / 1000 % 60;

        String remaining = String.format("%02d:%02d", minutes, seconds);

        quarterTime.setText(remaining);
    }
}
