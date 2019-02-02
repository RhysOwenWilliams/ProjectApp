package project.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import project.projectapp.Account.LoginActivity;

public class SplashActivity extends AppCompatActivity{

    private ImageView imageView;
    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        imageView = (ImageView) findViewById(R.id.applogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                finish();
            }
        }, SPLASH_TIME_OUT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        imageView.startAnimation(animation);
    }
}
