package com.wordrush;

/**
 * Created by Prakhar Dev Gupta on 26-05-2016.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;


public class first_screen extends Activity {

   ImageView play;  Button letsGO; TextView word,rush,high,best;
    ImageView quit,help;

    DatabaseHelper db;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //Theme problem Solved

        setContentView(R.layout.activity_first_screen);
        play = (ImageView)findViewById(R.id.play);
        word =(TextView)findViewById(R.id.word);
        rush = (TextView)findViewById(R.id.rush);
        quit =(ImageView)findViewById(R.id.quit);
        help =  (ImageView)findViewById(R.id.help);
        high = (TextView)findViewById(R.id.high_score);

        db = new DatabaseHelper(first_screen.this);
        try {
            db.createDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            db.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
       //     high.setText(String.valueOf(db.getHighScore()));

            SharedPreferences pref =  getSharedPreferences("scoring", Context.MODE_PRIVATE);
            int highScore =  pref.getInt("highscore",0);
            if(highScore<10){
                high.setText("0"+String.valueOf(highScore));

            }
            else
                high.setText(String.valueOf(highScore));

        }catch (Exception e){Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();}



        best =(TextView)findViewById(R.id.tv_best_score);

        final TextView high =(TextView)findViewById(R.id.high_score);
        TextView best = (TextView)findViewById(R.id.tv_best_score);


        word.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.fade_in));
        rush.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.late_fadein));
        high.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.late_fadein));
        quit.startAnimation(AnimationUtils.loadAnimation(this,R.anim.btm_up2));
     //   high.startAnimation(AnimationUtils.loadAnimation(this,R.anim.late_fadein));
        best.startAnimation(AnimationUtils.loadAnimation(this,R.anim.late_fadein));
        help.startAnimation(AnimationUtils.loadAnimation(this,R.anim.btm_up2));

        play.startAnimation(AnimationUtils.loadAnimation(this,R.anim.btm_up2));


       // play.startAnimation(AnimationUtils.loadAnimation(first_screen.this,android.R.anim.fade_in));

       //
       // play.setImageResource(R.drawable.abc);
        high.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               /* high.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.fade_in));
                high.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.bottom_up));*/
                high.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.fade_in));

                return false;
            }
        });
        best.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                high.startAnimation(AnimationUtils.loadAnimation(first_screen.this,R.anim.fade_in));
                return true;
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void PlayGame(View view)
    {
        try{
        Intent game = new Intent(first_screen.this,Game_Activity.class);
        startActivity(game);

           first_screen.this.overridePendingTransition(R.anim.bu2, R.anim.fo1);

        }
        catch (Exception e){Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

    }

    public void Quit(View v){

        onBackPressed();

    }
    public void Help(View v){



    final Dialog help = new Dialog(this);

        help.setContentView(R.layout.help_new);
        letsGO = (Button)help.findViewById(R.id.letsGO);
        help.show();

        letsGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                help.dismiss();
            }
        });

    }



    boolean t = false;

    @Override
    public void onBackPressed() {



       if(t== true){



           Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
           onDestroy();

        }

        Toast.makeText(getApplicationContext(),"Press again to exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                t = false;
            }
        },3000);

        t=true;



    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
