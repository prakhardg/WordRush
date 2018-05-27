package com.wordrush;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by Prakhar Dev Gupta on 26-05-2016.
 */
public class Third_Activity extends Activity {

    @Override
    public void onBackPressed() {
       GoHome(efforts);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    TextView efforts; View line; Button h_you,a_you,r_you,p_you; TextView score_made;
    Animation animation;
    ImageView home,restart_game; int id;
    String possible_word;
    int score; Context context = null; TextView new_best; SoundPool high = null;
    String current_color;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.third_activity);

        context = this;
        efforts = (TextView)findViewById(R.id.but_your_efforts);
        h_you = (Button) findViewById(R.id.h_you);
        a_you= (Button) findViewById(R.id.a_you);
        r_you  = (Button) findViewById(R.id.r_you);
        p_you= (Button) findViewById(R.id.p_you);
        home = (ImageView) findViewById(R.id.home);
        restart_game = (ImageView) findViewById(R.id.restart_game);
        new_best = (TextView)findViewById(R.id.new_best);

        Glide.with(context).load(R.drawable.home).into(home);
        Glide.with(context).load(R.drawable.replay).into(restart_game);

        home.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bottom_up));
        restart_game.startAnimation(AnimationUtils.loadAnimation(context,R.anim.bottom_up));

        animation = AnimationUtils.loadAnimation(Third_Activity.this, R.anim.fast_fadein);

        line =      findViewById(R.id.view_custom);
        score_made = (TextView) findViewById(R.id.score_made);

        score =  getIntent().getExtras().getInt("score");
        possible_word = getIntent().getExtras().getString("word");
        current_color = getIntent().getExtras().getString("color");

        try {
            String s = getIntent().getExtras().getString("psbl");
            int i = Character.getNumericValue(s.charAt(0));

            if (i == 1) {
                efforts.setText("It is the only possible word!");
            } else if(i==2) {
                efforts.setText("Or " + String.valueOf(i - 1) + " more word!");
            }
            else {
                efforts.setText("Or " + String.valueOf(i - 1) + " more words!");
            }
        }catch (Exception e){
            Toast.makeText(getApplication(),"fishy  "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        //efforts.setText(getIntent().getExtras().getString("psbl"));

        score_made.setText(String.valueOf(score));


        GradientDrawable h_you_gd = (GradientDrawable)h_you.getBackground().getCurrent();
        h_you_gd.setColor(Color.parseColor(current_color));

        try {
            line.setBackgroundColor(Color.parseColor(current_color));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();}

        GradientDrawable a_you_gd = (GradientDrawable)a_you.getBackground().getCurrent();
        a_you_gd.setColor(Color.parseColor(current_color));

        GradientDrawable r_you_gd = (GradientDrawable)r_you.getBackground().getCurrent();
        r_you_gd.setColor(Color.parseColor(current_color));

        GradientDrawable p_you_gd = (GradientDrawable)p_you.getBackground().getCurrent();
        p_you_gd.setColor(Color.parseColor(current_color));

        h_you.setText(String.valueOf(possible_word.charAt(0)));
        a_you.setText(String.valueOf(possible_word.charAt(1)));
        r_you.setText(String.valueOf(possible_word.charAt(2)));
        p_you.setText(String.valueOf(possible_word.charAt(3)));


        h_you.startAnimation(AnimationUtils.loadAnimation(this, R.anim.h));
        a_you.startAnimation(AnimationUtils.loadAnimation(this,R.anim.h));
        r_you.startAnimation(AnimationUtils.loadAnimation(this,R.anim.h));
        p_you.startAnimation(AnimationUtils.loadAnimation(this,R.anim.h));


        if(getIntent().getExtras().getBoolean("achieved")) {


            new_best.setVisibility(View.VISIBLE);
        }


                if(new_best.getVisibility() == View.VISIBLE)
                {

                    try {
                        high = new SoundPool(10, AudioManager.STREAM_MUSIC, 0); //Working only in Lollipop and lower. Not playing in Marshmallow
                        id = high.load(this, R.raw.highscore, 1);
                        //Toast.makeText(getApplicationContext(),"high.load",Toast.LENGTH_LONG).show();

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"soundpool prob  "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                    AudioManager audiomanager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float maxVol = (float) audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float vol = actualVol / maxVol;
                    try {
                        high.play(id, vol, vol, 1, 0, 1f);
                    }catch (Exception r){
                        Toast.makeText(getApplicationContext(),"Cannot play omg   "+r.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    //catch (Exception)
                }


        }





    public void GoHome(View v)
    {
        Intent i = new Intent(Third_Activity.this,first_screen.class);
        startActivity(i);
        finish();
    }

    public void PlayAgain(View v){
        Intent i = new Intent(Third_Activity.this,Game_Activity.class);
        startActivity(i);
        Third_Activity.this.overridePendingTransition(R.anim.bottom_up,R.anim.fo1);
        finish();
    }


}
