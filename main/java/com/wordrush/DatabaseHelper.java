package com.wordrush;

/**
 * Created by Prakhar Dev Gupta on 21-06-2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Random;


public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext ;
    private static final String DATABASE_NAME = "word.db";
    public final static String DATABASE_PATH ="/data/data/com.wordrush/databases/";
    public static final int DATABASE_VERSION = 1;
    //private String number;
    //private String word;
    int i;
   final int db_size= 1501;
    private int highScore;
    int num;
    private String possible;
    ///private String chk;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext =  context;
    }

    public  void createDB() throws IOException{
    //    boolean dbExists =  checkDatabase();
        checkDatabase();

        SQLiteDatabase db_read ;
        db_read = this.getWritableDatabase();                       //////////////////////////////////////////////////////////////
        db_read.close();

        try{
            copyDatabase();
        }catch (Exception e){Toast.makeText(myContext,e.getMessage(),Toast.LENGTH_SHORT).show();}


    }

    private void copyDatabase()throws IOException{

       // InputStream myinput  = myContext.getAssets().open(DATABASE_NAME+".db");
        InputStream myinput  = myContext.getAssets().open(DATABASE_NAME);

        String outfilename = DATABASE_PATH+DATABASE_NAME;
        OutputStream myoutput =  new FileOutputStream(outfilename);


        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myinput.read(buffer)) > 0)
        {
            myoutput.write(buffer, 0, length);
        }

        myoutput.flush();
        myoutput.close();
        myinput.close();

    }

    private boolean checkDatabase() {

        SQLiteDatabase checkDB = null;


        try
        {
              checkDB = openDatabase();
        }
        catch (Exception r){r.printStackTrace();}


        if(checkDB != null)
            checkDB.close();
        return checkDB != null ? true : false;
    }

    public SQLiteDatabase openDatabase() throws SQLException{
        String myPath = DATABASE_PATH+DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);    /////////// check;

        return myDataBase;

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public int getNumber() {
        Random r = new Random();



        int x = r.nextInt(db_size)+1;
        if(x==347)
            getNumber();        /////A null value present at 347 in DB;


        return x;
    }

    public String getWord() {
        boolean ee =false;
        //String w =null;
        StringBuffer sb =  new StringBuffer();

        Cursor c ;

                try{
       // myDataBase.execSQL("select word from wordlist where serial ="+getNumber());

             //  c = myDataBase.rawQuery("select word from wordlist where serial =" +getNumber(),null);

                    num = getNumber();

                    c= myDataBase.rawQuery("select * from wordlist where _id ="+num,null);
                   // w = c.toString();

                    while(c.moveToNext()){
                        //sb.append("ID =" +c.getString(0)+"\n");
                        sb.append(""+c.getString(1));



                        //sb.append(""+shuffle(c.getString(1)));
                    }
                ee = true;
                }catch (Exception e){Toast.makeText(myContext,e.getMessage(),Toast.LENGTH_SHORT).show();}

        if(ee)
            return  sb.toString();
        else
            return "TTTT";    ///////////////////////////Testing String //////////////////////////

    }

    public String shuffle(String str) {
        Random r = new Random();
        StringBuilder s  = new StringBuilder(str);
        StringBuilder shuffled_string = new StringBuilder();

        while(s.length() != 0){

            int index  = r.nextInt(s.length());
            char c = s.charAt(index);
            shuffled_string.append(c);
            s.deleteCharAt(index);

        }
        return shuffled_string.toString();
    }

    public boolean check_my_ans(String check_str) {

        boolean result;
        Cursor c = myDataBase.rawQuery("select * from wordlist where word = '"+check_str+"'",null);
        if (c.getCount() >0)
            result = true;
        else
            result = false;

        return result;
    }

    public String getPossible() {

        Cursor c; StringBuffer sb = new StringBuffer();
        StringBuffer snum = new StringBuffer();

        try {
            c = myDataBase.rawQuery("select * from wordlist where _id =" + num, null);
            while (c.moveToNext()) {
                //String i = ""+String.valueOf(c.getInt(2));
                try {
                    snum.append("" + String.valueOf(c.getInt(2)));
                } catch (Exception e) {
                    Toast.makeText(myContext, "yoyo  " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }

                try {
                    i = Integer.parseInt(snum.toString().trim());
                    //i = i + 100;
                  //  Toast.makeText(myContext, "i = " + i, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(myContext, "Inner try " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (i > 1)
                    //sb.append("" + String.valueOf(c.getInt(2)) + " possible words");
                    sb.append("" + i + " possible words");

                else
                    /*sb.append("" + String.valueOf(c.getInt(2)) + " possible word");*/
                    sb.append("" + i + " possible word");

            }
            catch(Exception e){
                Toast.makeText(myContext, "sdffg  " + e.getMessage(), Toast.LENGTH_LONG).show();
                // Toast.makeText(myContext,String.valueOf(num),Toast.LENGTH_LONG).show();
            }




        return sb.toString();
    }
}
