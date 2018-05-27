package com.wordrush;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by hpuser on 26-05-2016.
 */
public class Game_Activity extends Activity {
    Utilities utilities; String word=null;String shuffled_word=null;
    long game_time = 33*1000; boolean help = false; boolean achieved = false;
     int start_time =3000;          ///String chk;
    int selected = 0;
    int game_score =0;
    int highscore;
    int prev_high;
    boolean gameOver = false;
    TextView possible ;
    String psbl;


/*    String [] crazy_colors = {"#0099cc","#f209b8","#e04e69",
            "#03bf77","#e8e002","#ff4747","#027993","#ff4081","#20ba1d","#002f7c","ff5d00","#b231a5"};*/
/*String [] crazy_colors = {"#8a2be2","#0000ff","#dc143c","#7fffd4","#ff1493","#20b2aa",
        "#ff4500","#da70d6","#ff0000","#008080","#ffff00","#ff7f50","#0099cc"};*/

    String [] crazy_colors = {"#734D75","#ff0088","#ff9452","#4cb899"
            ,"#751515","#f40b0b","#00ad5a","#0090CF","#8100CF","#CF00A0"};

    Random random;
    String color;

    Animation blinking;

    String hs,as,rs,ps;
    String check_str;

    Handler game_time_handler,  handler;
    
    boolean isStarted = true;
    ImageView response;
    Button h,a,r,p; 
    Button c1,c2,c3,c4;
    TextView score,high_score_in_game,running_time; TextView ready; RelativeLayout main_view,rel_to_hide;

    DatabaseHelper myDB;
  //  Thread  timer;
   MediaPlayer mp = null;
    SoundPool right=null,wrong=null;
    SoundPool comp = null;

    int id1,id2,id3;

   SharedPreferences pref;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.game_activity);

        achieved  =  false;
        gameOver = false;



        myDB = new DatabaseHelper(this);        ///Calling the database I stored in my assets folder


        //Create and open DB;
     try {
            myDB.createDB();
        // Toast.makeText(getApplicationContext(),"OPENED!",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            myDB.openDatabase();
          //  Toast.makeText(getApplicationContext(),"OPENED!",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }



        mp = MediaPlayer.create(Game_Activity.this,R.raw.toc);

        right = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        id1 = right.load(this,R.raw.correct,1);
        comp = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        id3 = comp.load(this,R.raw.timeover,1);
        wrong = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        id2 = wrong.load(this,R.raw.www,1);


        h = (Button)findViewById(R.id.h);
        a = (Button) findViewById(R.id.a);
        r = (Button) findViewById(R.id.r);
        p = (Button) findViewById(R.id.p);
        c1 = (Button) findViewById(R.id.choice1);
        c2= (Button) findViewById(R.id.choice2);
        c3 = (Button) findViewById(R.id.choice3);
        c4 = (Button) findViewById(R.id.choice4);
        possible = (TextView)findViewById(R.id.possible);

        
        blinking = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);      ////Unused animation
        handler =  new Handler();
        game_time_handler = new Handler();
        utilities = new Utilities();
        main_view = (RelativeLayout) findViewById(R.id.main_view);
        rel_to_hide = (RelativeLayout) findViewById(R.id.rel_to_hide);
        ready = (TextView) findViewById(R.id.ready);








        score = (TextView) findViewById(R.id.game_score);
        if(game_score/10  == 0){
            score.setText("0"+String.valueOf(game_score));
        }
        else
        score.setText(String.valueOf(game_score));


        high_score_in_game = (TextView) findViewById(R.id.high_score_in_game);


        running_time = (TextView) findViewById(R.id.running_time);

        response= (ImageView) findViewById(R.id.response);
        response.setVisibility(View.GONE);


        updater_before_play();
        start_new_chance();


        update_game_time();

        try{

            prev_high = Integer.parseInt(high_score_in_game.getText().toString());


        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"prev_high problem  "+e.getMessage(),Toast.LENGTH_LONG).show();
        }       ////////////////Changes made on 8-7-2016


    }

    private void start_new_chance() {           ////////////////////////////////////////////////////////////Main Code/////////////

        pref = getSharedPreferences("scoring", Context.MODE_PRIVATE);
        try{
            highscore = pref.getInt("highscore",0);

        }catch (Exception e){
            highscore =0;
        }


        if(highscore <10){
            high_score_in_game.setText("0"+String.valueOf(highscore));
        }
        else
            high_score_in_game.setText(String.valueOf(highscore));




        if(game_score>highscore){
            highscore = game_score;         //////////////Well DONE!!!!!!! :D :D :D

            SharedPreferences.Editor editor = pref.edit();
            achieved = true;
            editor.putInt("highscore",highscore);
            editor.commit();                            //////////////xxtx
        }

        if(highscore <10){
            high_score_in_game.setText("0" + String.valueOf(highscore));
        }
        else
            high_score_in_game.setText(String.valueOf(highscore));





        selected =0;
        c1.setBackgroundResource(R.drawable.rr2); c1.setText("");
        c2.setBackgroundResource(R.drawable.rr2); c2.setText("");
        c3.setBackgroundResource(R.drawable.rr2); c3.setText("");
        c4.setBackgroundResource(R.drawable.rr2); c4.setText("");



        h.setBackgroundResource(R.drawable.rr);
        a.setBackgroundResource(R.drawable.rr);
        r.setBackgroundResource(R.drawable.rr);
        p.setBackgroundResource(R.drawable.rr);

        random = new Random();
        GradientDrawable h_gd = (GradientDrawable)h.getBackground().getCurrent();
        GradientDrawable a_gd = (GradientDrawable)a.getBackground().getCurrent();
        GradientDrawable r_gd = (GradientDrawable)r.getBackground().getCurrent();
        GradientDrawable p_gd = (GradientDrawable)p.getBackground().getCurrent();

        int xxx = random.nextInt(crazy_colors.length );
        try{
        color = crazy_colors[xxx];          }catch (Exception e){Toast
                .makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT)
                .show();}
                //////////////////////tube
      //  Toast.makeText(getApplicationContext(),"Color = "+xxx + "\nSize = "+crazy_colors.length ,Toast.LENGTH_SHORT).show();
        h_gd.setColor(Color.parseColor(color));
        a_gd.setColor(Color.parseColor(color));
        r_gd.setColor(Color.parseColor(color));
        p_gd.setColor(Color.parseColor(color));


        h.setEnabled(true);
        a.setEnabled(true);
        r.setEnabled(true);
        p.setEnabled(true);

        try {
            word = myDB.getWord();
            shuffled_word = myDB.shuffle(word);
            psbl = myDB.getPossible();      //////////////////////////////////////////////////////

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"oh no  .."+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


            shuffled_word = myDB.shuffle(word);

            hs = String.valueOf(shuffled_word.charAt(0));
            as = String.valueOf(shuffled_word.charAt(1));
            rs= String.valueOf(shuffled_word.charAt(2));
            ps=  String.valueOf(shuffled_word.charAt(3));       //I want to keep the record
                                                                // of which letter belongs to which
                                                                // button at that instant

            h.setText(hs);
            a.setText(as);
            r.setText(rs);
            p.setText(ps);

        possible.setText(psbl); ///////////////////////////////////////////////////////////////////////////////


            h.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fast_fadein));
            a.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fast_fadein));
            r.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fast_fadein));
            p.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fast_fadein));


    }
    public void Choose(View v){
        switch (v.getId()){
            case R.id.h:
                if(h.getText().equals(""))
                {
                    h.setBackgroundResource(R.drawable.rr2);
                    h.setEnabled(false);

                }
                else{
                    switch (selected){
                        case 0:
                            ++selected;
                            c1.setText(hs); c1.setBackgroundResource(R.drawable.rr);
                            GradientDrawable c1_gd  = (GradientDrawable)c1.getBackground().getCurrent();
                            c1_gd.setColor(Color.parseColor(color));

                            h.setBackgroundResource(R.drawable.rr2);
                            h.setText("");
                            h.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                           /// c4.setEnabled(true);            ///Starts working for backpress.
                            break;
                        case 1:
                            ++selected;
                            c2.setText(hs);c2.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c2_gd = (GradientDrawable)c2.getBackground().getCurrent();
                            c2_gd.setColor(Color.parseColor(color));


                            h.setBackgroundResource(R.drawable.rr2);
                            h.setText("");
                            h.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                           // c4.setEnabled(true);            ///Starts working for backpress.
                            break;
                        case 2:
                            ++selected;
                            c3.setText(hs);c3.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c3_gd = (GradientDrawable)c3.getBackground().getCurrent();
                            c3_gd.setColor(Color.parseColor(color));


                            h.setEnabled(false);
                            h.setText("");
                            h.setBackgroundResource(R.drawable.rr2);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                         //   c4.setEnabled(true);
                            break;
                        case 3:
                            ++selected;
                            c4.setText(hs);c4.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c4_gd = (GradientDrawable)c4.getBackground().getCurrent();
                            c4_gd.setColor(Color.parseColor(color));

                            h.setText("");
                            h.setBackgroundResource(R.drawable.rr2);
                            h.setEnabled(false);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                           // c4.setEnabled(true);

                            try {
                                check_str = c1.getText().toString() + c2.getText().toString() + c3.getText().toString() + c4.getText().toString();

                            }catch (Exception e)
                            {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            if(myDB.check_my_ans(check_str))
                            {
                                ++game_score;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                float vol = actualVol/maxVol;
                                right.play(id1,vol,vol,1,0,1f);}


                                if(game_score/10  == 0){
                                    score.setText("0"+String.valueOf(game_score));
                                }
                                else
                                    score.setText(String.valueOf(game_score));
                                game_time+=2000;

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);


                            }
                            else
                            {
                                game_time -=1000;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    wrong.play(id2,vol,vol,1,0,1f);}

                                if(game_time<0)
                                    game_time = -1000;
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);


                            }

                            break;

                    }
                }
                break;
            case R.id.a:

                if(a.getText().equals("")){
                    a.setBackgroundResource(R.drawable.rr2);
                    a.setEnabled(false);
                }

                else {
                    switch (selected){
                        case 0:
                            ++selected;
                            c1.setText(as); c1.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c1_gd  = (GradientDrawable)c1.getBackground().getCurrent();
                            c1_gd.setColor(Color.parseColor(color));

                            a.setBackgroundResource(R.drawable.rr2);
                            a.setText("");
                            a.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                            break;

                        case 1:
                            ++selected;
                            c2.setText(as);c2.setBackgroundResource(R.drawable.rr);
                            GradientDrawable c2_gd = (GradientDrawable)c2.getBackground().getCurrent();
                            c2_gd.setColor(Color.parseColor(color));
                            a.setBackgroundResource(R.drawable.rr2);
                            a.setText("");
                            a.setEnabled(false);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 2:
                            ++selected;
                            c3.setText(as);c3.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c3_gd = (GradientDrawable)c3.getBackground().getCurrent();
                            c3_gd.setColor(Color.parseColor(color));

                            a.setEnabled(false);
                            a.setText("");
                            a.setBackgroundResource(R.drawable.rr2);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 3:
                            ++selected;
                            c4.setText(as);c4.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c4_gd = (GradientDrawable)c4.getBackground().getCurrent();
                            c4_gd.setColor(Color.parseColor(color));

                            a.setText("");
                            a.setBackgroundResource(R.drawable.rr2);
                            a.setEnabled(false);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);


                            try {

                                check_str = c1.getText().toString() + c2.getText().toString() + c3.getText().toString() + c4.getText().toString();

                            }catch (Exception e)
                            {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            if(myDB.check_my_ans(check_str))
                            {
                                ++game_score;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    right.play(id1,vol,vol,1,0,1f);}

                                if(game_score/10  == 0){
                                    score.setText("0"+String.valueOf(game_score));
                                }
                                else
                                    score.setText(String.valueOf(game_score));
                                game_time+=2000;

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);

                            }
                            else
                            {
                                game_time -=1000;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    wrong.play(id2,vol,vol,1,0,1f);}

                                if(game_time<0)
                                    game_time = -1000;

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);

                            }


                            break;




                    }
                }
                break;

            case R.id.r :
                if(r.getText().equals("")){
                    r.setEnabled(false);
                    r.setBackgroundResource(R.drawable.rr2);
                }
                else {
                    switch (selected){
                        case 0:
                            ++selected;
                            c1.setText(rs); c1.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c1_gd  = (GradientDrawable)c1.getBackground().getCurrent();
                            c1_gd.setColor(Color.parseColor(color));

                            r.setBackgroundResource(R.drawable.rr2);
                            r.setText("");
                            r.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                            break;
                        case 1:
                            ++selected;
                            c2.setText(rs);c2.setBackgroundResource(R.drawable.rr);
                            GradientDrawable c2_gd = (GradientDrawable)c2.getBackground().getCurrent();
                            c2_gd.setColor(Color.parseColor(color));
                            r.setBackgroundResource(R.drawable.rr2);
                            r.setText("");
                            r.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 2:
                            ++selected;
                            c3.setText(rs);c3.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c3_gd = (GradientDrawable)c3.getBackground().getCurrent();
                            c3_gd.setColor(Color.parseColor(color));

                            r.setEnabled(false);
                            r.setText("");
                            r.setBackgroundResource(R.drawable.rr2);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 3:
                            ++selected;
                            c4.setText(rs);c4.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c4_gd = (GradientDrawable)c4.getBackground().getCurrent();
                            c4_gd.setColor(Color.parseColor(color));

                            r.setText("");
                            r.setBackgroundResource(R.drawable.rr2);
                            r.setEnabled(false);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                            try {
                                check_str = c1.getText().toString() + c2.getText().toString() + c3.getText().toString() + c4.getText().toString();

                            }catch (Exception e)
                            {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            if(myDB.check_my_ans(check_str))
                            {
                                ++game_score;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    right.play(id1,vol,vol,1,0,1f);}
                                if(game_score/10  == 0){
                                    score.setText("0"+String.valueOf(game_score));
                                }
                                else
                                    score.setText(String.valueOf(game_score));
                                game_time+=2000;

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);


                            }
                            else
                            {
                                game_time -=1000;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    wrong.play(id2,vol,vol,1,0,1f);}

                                if(game_time<0)
                                    game_time = -1000;


                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);

                            }


                            break;
                    }
                }
                break;
            case R.id.p:
                if(p.getText().equals("")){
                    p.setEnabled(false);
                    p.setBackgroundResource(R.drawable.rr2);
                }
                else {
                    switch (selected){
                        case 0:
                            ++selected;
                            c1.setText(ps); c1.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c1_gd  = (GradientDrawable)c1.getBackground().getCurrent();
                            c1_gd.setColor(Color.parseColor(color));

                            p.setBackgroundResource(R.drawable.rr2);
                            p.setText("");
                            p.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 1:
                            ++selected;
                            c2.setText(ps);c2.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c2_gd = (GradientDrawable)c2.getBackground().getCurrent();
                            c2_gd.setColor(Color.parseColor(color));

                            p.setBackgroundResource(R.drawable.rr2);
                            p.setText("");
                            p.setEnabled(false);        //Need to re enable.
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);

                            break;
                        case 2:
                            ++selected;
                            c3.setText(ps);c3.setBackgroundResource(R.drawable.rr);
                            GradientDrawable c3_gd = (GradientDrawable)c3.getBackground().getCurrent();
                            c3_gd.setColor(Color.parseColor(color));

                            p.setEnabled(false);
                            p.setText("");
                            p.setBackgroundResource(R.drawable.rr2);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                         ///   c4.setEnabled(true);
                            break;

                        case 3:
                            ++selected;
                            c4.setText(ps);c4.setBackgroundResource(R.drawable.rr);

                            GradientDrawable c4_gd = (GradientDrawable)c4.getBackground().getCurrent();
                            c4_gd.setColor(Color.parseColor(color));


                            p.setText("");
                            p.setBackgroundResource(R.drawable.rr2);
                            p.setEnabled(false);
                            c1.setEnabled(true);
                            c2.setEnabled(true);
                            c3.setEnabled(true);
                           // c4.setEnabled(true);

                            try {
                                check_str = c1.getText().toString() + c2.getText().toString() + c3.getText().toString() + c4.getText().toString();
                            }catch (Exception e)
                            {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            if(myDB.check_my_ans(check_str))
                            {
                                ++game_score;

                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    right.play(id1,vol,vol,1,0,1f);}
                                if(game_score/10  == 0){
                                    score.setText("0"+String.valueOf(game_score));
                                }
                                else
                                    score.setText(String.valueOf(game_score));
                                game_time+=2000;

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);



                            }
                            else
                            {


                                game_time -= 1000;
                                { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                    float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    float vol = actualVol/maxVol;
                                    wrong.play(id2,vol,vol,1,0,1f);}

                                if(game_time<0)
                                    game_time = -1000;
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {            //////Nice JOB!!
                                    @Override
                                    public void run() {
                                        try {

                                            start_new_chance();
                                        }catch (Exception r){
                                            Toast.makeText(getApplicationContext(),"xxx  "+r.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },400);


                            }


                            break;



                    }
                }


        }

    }

    public void Unselect (View v){

        if(selected==4)
            return;

        switch (v.getId()) {
            case R.id.choice3:


                if (c3.getText().toString().equals("")) {
                    //////////Do nothing;
                } else {

                    if (c3.getText().toString().equals(hs)) {
                        c3.setText("");
                        c3.setEnabled(false);
                        c3.setBackgroundResource(R.drawable.rr2);
                        h.setEnabled(true);
                        h.setText(hs);
                        h.setBackgroundResource(R.drawable.rr);


                        GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                        h_gd.setColor(Color.parseColor(color));
                        --selected;


                    } else if (c3.getText().toString().equals(as)) {
                        c3.setText("");
                        c3.setEnabled(false);
                        c3.setBackgroundResource(R.drawable.rr2);
                        a.setEnabled(true);
                        a.setText(as);
                        a.setBackgroundResource(R.drawable.rr);
                        GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                        a_gd.setColor(Color.parseColor(color));
                        --selected;
                    } else if (c3.getText().toString().equals(rs)) {
                        c3.setText("");
                        c3.setEnabled(false);
                        c3.setBackgroundResource(R.drawable.rr2);
                        r.setText(rs);
                        r.setEnabled(true);
                        r.setBackgroundResource(R.drawable.rr);
                        GradientDrawable r_gd = (GradientDrawable) r.getBackground().getCurrent();
                        r_gd.setColor(Color.parseColor(color));
                        --selected;
                    } else if (c3.getText().toString().equals(ps)) {
                        c3.setText("");
                        c3.setEnabled(false);
                        c3.setBackgroundResource(R.drawable.rr2);
                        p.setEnabled(true);
                        p.setBackgroundResource(R.drawable.rr);
                        p.setText(ps);
                        GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                        p_gd.setColor(Color.parseColor(color));
                        --selected;

                    } else
                        Toast.makeText(getApplicationContext(), "Some logical error in C3", Toast.LENGTH_LONG).show();

                }

                break;


            case R.id.choice2:
                if (c2.getText().toString().equals("")) {
                    //////////Do nothing;
                } else {
                    switch (selected) {
                        case 4:
                            break;

                        case 2:

                            c2.setBackgroundResource(R.drawable.rr2);
                            c2.setEnabled(false);
                            if (c2.getText().toString().equals(hs)) {

                                c2.setText("");
                                h.setEnabled(true);
                                h.setText(hs);
                                h.setBackgroundResource(R.drawable.rr);
                                GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                                h_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c2.getText().toString().equals(as)) {
                                c2.setText("");
                                a.setEnabled(true);
                                a.setText(as);
                                a.setBackgroundResource(R.drawable.rr);
                                GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                                a_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c2.getText().toString().equals(rs)) {
                                c2.setText("");

                                r.setEnabled(true);
                                r.setText(rs);
                                r.setBackgroundResource(R.drawable.rr);
                                GradientDrawable r_gd = (GradientDrawable) a.getBackground().getCurrent();
                                r_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c2.getText().toString().equals(ps)) {
                                c2.setText("");
                                p.setText(ps);
                                p.setEnabled(true);
                                p.setBackgroundResource(R.drawable.rr);
                                GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                                p_gd.setColor(Color.parseColor(color));
                                --selected;

                            }
                            break;
                        case 3:
                            if (c2.getText().toString().equals(hs)) {

                                c2.setText(c3.getText().toString());
                                c3.setText("");

                                c3.setBackgroundResource(R.drawable.rr2);       ////Using as backspace;
                                c3.setEnabled(false);
                                h.setEnabled(true);
                                h.setText(hs);
                                h.setBackgroundResource(R.drawable.rr);
                                GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                                h_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c2.getText().toString().equals(as)) {

                                c2.setText(c3.getText().toString());
                                c3.setText("");
                                c3.setBackgroundResource(R.drawable.rr2);       ////Using as backspace;
                                c3.setEnabled(false);

                                a.setEnabled(true);
                                a.setText(as);
                                a.setBackgroundResource(R.drawable.rr);
                                GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                                a_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c2.getText().toString().equals(rs)) {

                                c2.setText(c3.getText().toString());
                                c3.setText("");
                                c3.setBackgroundResource(R.drawable.rr2);       ////Using as backspace;
                                c3.setEnabled(false);

                                r.setEnabled(true);
                                r.setText(rs);
                                r.setBackgroundResource(R.drawable.rr);
                                GradientDrawable r_gd = (GradientDrawable) a.getBackground().getCurrent();
                                r_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c2.getText().toString().equals(ps)) {

                                c2.setText(c3.getText().toString());
                                c3.setText("");
                                c3.setBackgroundResource(R.drawable.rr2);       ////Using as backspace;
                                c3.setEnabled(false);

                                p.setText(ps);
                                p.setEnabled(true);
                                p.setBackgroundResource(R.drawable.rr);
                                GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                                p_gd.setColor(Color.parseColor(color));
                                --selected;

                            }
                            break;


                    }
                }
                break;
            case R.id.choice1:
                if (c1.getText().toString().equals("")) {
                    ///Do nothing;
                } else {      ////Switch cases for selected value;

                    switch (selected) {
                        case 1:
                            c1.setEnabled(false);
                            c1.setBackgroundResource(R.drawable.rr2);
                            if (c1.getText().toString().equals(hs)) {
                                c1.setText("");
                                h.setText(hs);
                                h.setEnabled(true);
                                h.setBackgroundResource(R.drawable.rr);
                                GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                                h_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c1.getText().toString().equals(as)) {
                                c1.setText("");
                                a.setText(as);
                                a.setEnabled(true);
                                a.setBackgroundResource(R.drawable.rr);
                                GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                                a_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c1.getText().toString().equals(rs)) {
                                c1.setText("");
                                r.setText(rs);
                                r.setEnabled(true);
                                r.setBackgroundResource(R.drawable.rr);
                                GradientDrawable r_gd = (GradientDrawable) r.getBackground().getCurrent();
                                r_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c1.getText().toString().equals(ps)) {
                                c1.setText("");
                                p.setText(ps);
                                p.setEnabled(true);
                                p.setBackgroundResource(R.drawable.rr);
                                GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                                p_gd.setColor(Color.parseColor(color));
                                --selected;
                            }
                            break;
                        case 4:
                            //++selected;
                            break;  /////Don't let the player do anything once all 4 letters have been selected

                        case 2:
                            if (c1.getText().toString().equals(hs)) {
                                c1.setText(c2.getText().toString());
                                c2.setEnabled(false);
                                c2.setBackgroundResource(R.drawable.rr2);
                                c2.setText("");
                                h.setBackgroundResource(R.drawable.rr);
                                h.setText(hs);
                                h.setEnabled(true);

                                GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                                h_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c1.getText().toString().equals(as)) {
                                c1.setText(c2.getText().toString());
                                c2.setEnabled(false);
                                c2.setBackgroundResource(R.drawable.rr2);
                                c2.setText("");

                                a.setText(as);
                                a.setBackgroundResource(R.drawable.rr);
                                a.setEnabled(true);
                                GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                                a_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c1.getText().toString().equals(rs)) {
                                c1.setText(c2.getText().toString());
                                c2.setEnabled(false);
                                c2.setBackgroundResource(R.drawable.rr2);
                                c2.setText("");

                                r.setEnabled(true);
                                r.setText(rs);
                                r.setBackgroundResource(R.drawable.rr);
                                GradientDrawable r_gd = (GradientDrawable) r.getBackground().getCurrent();
                                r_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c1.getText().toString().equals(ps)) {
                                c1.setText(c2.getText().toString());
                                c2.setEnabled(false);
                                c2.setBackgroundResource(R.drawable.rr2);
                                c2.setText("");

                                p.setText(ps);
                                p.setEnabled(true);
                                p.setBackgroundResource(R.drawable.rr);
                                GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                                p_gd.setColor(Color.parseColor(color));
                                --selected;
                            }
                            break;
                        case 3:
                            if (c1.getText().toString().equals(hs)) {
                                c1.setText(c2.getText().toString());
                                c2.setText(c3.getText().toString());
                                c3.setEnabled(false);
                                c3.setText("");
                                c3.setBackgroundResource(R.drawable.rr2);
                                c3.setEnabled(false);
                                h.setText(hs);
                                h.setEnabled(true);
                                h.setBackgroundResource(R.drawable.rr);
                                GradientDrawable h_gd = (GradientDrawable) h.getBackground().getCurrent();
                                h_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c1.getText().toString().equals(as)) {
                                c1.setText(c2.getText().toString());
                                c2.setText(c3.getText().toString());
                                c3.setEnabled(false);
                                c3.setText("");
                                c3.setBackgroundResource(R.drawable.rr2);
                                c3.setEnabled(false);

                                a.setEnabled(true);
                                a.setText(as);
                                a.setBackgroundResource(R.drawable.rr);
                                GradientDrawable a_gd = (GradientDrawable) a.getBackground().getCurrent();
                                a_gd.setColor(Color.parseColor(color));
                                --selected;

                            } else if (c1.getText().toString().equals(rs)) {
                                c1.setText(c2.getText().toString());
                                c2.setText(c3.getText().toString());
                                c3.setEnabled(false);
                                c3.setBackgroundResource(R.drawable.rr2);
                                c3.setEnabled(false);
                                c3.setText("");

                                r.setText(rs);
                                r.setEnabled(true);
                                r.setBackgroundResource(R.drawable.rr);
                                GradientDrawable r_gd = (GradientDrawable) r.getBackground().getCurrent();
                                r_gd.setColor(Color.parseColor(color));
                                --selected;
                            } else if (c1.getText().toString().equals(ps)) {
                                c1.setText(c2.getText().toString());
                                c2.setText(c3.getText().toString());
                                c3.setEnabled(false);
                                c3.setBackgroundResource(R.drawable.rr2);
                                c3.setEnabled(false);
                                c3.setText("");

                                p.setEnabled(true);
                                p.setText(ps);
                                p.setBackgroundResource(R.drawable.rr);
                                GradientDrawable p_gd = (GradientDrawable) p.getBackground().getCurrent();
                                p_gd.setColor(Color.parseColor(color));
                                --selected;
                            }
                            break;

                    }

                }
        }

        }



    Runnable runnable_game_time = new Runnable() {
        @Override
        public void run() {
            if(game_time != -1000)
            game_time =game_time - 1000;
            if(help)
                game_time -= 1000;

            if(game_time <=1000*10 && game_time >=0 )

               mp.start();
             if(game_time > 10*1000 && mp.isPlaying())
            {
               mp.seekTo(0);
                mp.pause();

            }
            update_game_time();
        }
    };
    private void update_game_time() {

        if(game_time == -1000){

            rel_to_hide.setVisibility(View.VISIBLE);
            main_view.setVisibility(View.GONE);
            ready.setText("TIME UP!");
            gameOver = true;

            mp.stop();
            mp.release();

            right.release();        /////////////////////Check here//////////////It is correct and necessary
            wrong.release();
            help = true;

            { AudioManager audiomanager = (AudioManager)getSystemService(AUDIO_SERVICE);
                float actualVol = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVol = (float)audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float vol = actualVol/maxVol;
                comp.play(id3,vol,vol,1,0,1f);
            }


            game_time_handler.postDelayed(runnable_game_time,3300);
        }
        else if(game_time == -2000){
            try {
                game_time_handler.removeCallbacks(runnable_game_time);
            }catch (Exception r){Toast.makeText(getApplicationContext(),r.getMessage(),Toast.LENGTH_SHORT).show();}



            try{


                Bundle bundle = new Bundle();
                bundle.putString("color", color);
                bundle.putInt("score", game_score);
                bundle.putBoolean("achieved",achieved);
                bundle.putString("psbl",psbl);


                bundle.putString("word", word);

            Intent gameOver = new Intent(Game_Activity.this,Third_Activity.class);
                gameOver.putExtras(bundle);
            startActivity(gameOver);
                Game_Activity.this.overridePendingTransition(R.anim.bu2, R.anim.fo1);
            finish();
            }
            catch (Exception r ){Toast.makeText(getApplicationContext(),r.getMessage(),Toast.LENGTH_SHORT).show();}
        }
        else{

        running_time.setText("" + utilities.millisecondstoTimer(game_time));
        game_time_handler.postDelayed(runnable_game_time,1000);}
    }


    Runnable run = new Runnable() {
        @Override
        public void run() {
            start_time -=1000;
            updater_before_play();
        }
    };

    private void updater_before_play()  {

        if(start_time==0) {
            ready.setText("GO..");
            handler.postDelayed(run, 750);
        }
        else if(start_time== -1000)
        {
            rel_to_hide.setVisibility(View.GONE);
            main_view.setVisibility(View.VISIBLE);
            try{
                isStarted = true;
            handler.removeCallbacks(run);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        else {

            String pass = String.valueOf(start_time/1000);
            ready.setText(pass);
            handler.postDelayed(run, 750);

        }


    }

    public void GoHome(View v)
    {

        final Dialog dialog = new Dialog(Game_Activity.this);
        dialog.setTitle("Exit game?");
        dialog.setContentView(R.layout.quit_confirmation);
        dialog.show();
        ImageView tick, cross;

        tick = (ImageView)dialog.findViewById(R.id.tick);
        cross =(ImageView)dialog.findViewById(R.id.cross);

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Game_Activity.this,first_screen.class);
                startActivity(i);
                dialog.dismiss();

                if(right !=null)
                    right =null;
                if(wrong != null)
                    wrong = null;
                if(comp != null)
                    comp = null;

                if(achieved && !gameOver )          //////////////Changes made on 8-7-2016
                {
                    //highscore = prev_high;


                    SharedPreferences.Editor editor = pref.edit();
                   // achieved = true;
                    editor.putInt("highscore",prev_high);
                    editor.commit();
                }

                if(mp.isPlaying())
                {
                    mp.stop(); mp.release();}

                game_time_handler.removeCallbacks(runnable_game_time);
                handler.removeCallbacks(run);
                finish();

            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onBackPressed() {
        GoHome(h);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(run);
        game_time_handler.removeCallbacks(runnable_game_time);


/*        if(achieved && !gameOver )          //////////////Changes made on 8-7-2016
        {
            //highscore = prev_high;

            Toast.makeText(getApplicationContext(),"Achieved and not gameover",Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = pref.edit();
            // achieved = true;
            editor.putInt("highscore",prev_high);
            editor.commit();
        }
        else
            Toast.makeText(getApplicationContext(),"Else part",Toast.LENGTH_LONG).show();*/

       super.onDestroy();

    }

    @Override
    protected void onStop() {

        if(achieved && !gameOver )          //////////////Changes made on 8-7-2016
        {
            //highscore = prev_high;

            //Toast.makeText(getApplicationContext(),"Achieved and not gameover",Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = pref.edit();
            // achieved = true;
            editor.putInt("highscore",prev_high);
            editor.commit();
        }
       /* else
            Toast.makeText(getApplicationContext(),"Else part",Toast.LENGTH_LONG).show();
*/

        super.onStop();
    }
}
