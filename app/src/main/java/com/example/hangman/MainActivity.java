package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String word;
    Character character;
    StringBuilder temptxt = new StringBuilder("_ ");
    ArrayList<Character> rightOnes = new ArrayList();
    int tries;
    ArrayList<Character> searchedHistory = new ArrayList();

    private static final long START_TIME_IN_MILLIS = 60000;
    private TextView mTextViewCD;
    private Button btnStartPause;
    private Button btnReset;
    private ProgressBar progressBar;

    private CountDownTimer countDownTimer;
    private boolean runningTime;
    private long timeLeftMS = START_TIME_IN_MILLIS;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        ArrayList<String> nameList = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.gameWordList)));
        Random r = new Random();
        word = nameList.get((r.nextInt(nameList.size())));



        TextView textView1 = findViewById(R.id.textView);
        ImageView img = findViewById(R.id.imageView);
        //TextView textView2 = (TextView) findViewById(R.id.editText);

        buildWord();
        textView1.setText(temptxt.toString());
        tries = 10;
        int pic = R.drawable.prog0;
        scale(img, pic);
        mTextViewCD = findViewById(R.id.textView2);
        btnStartPause = findViewById(R.id.checkBtn);
        btnReset = findViewById(R.id.resetBtn);
    }





    public ArrayList<Character> setToAL(Set<String> list){
        ArrayList<Character> newList = new ArrayList<>();
        for (String x : list)
            newList.add(x.charAt(0));
        return newList;
    }
    public Set<String> aLtoSet(ArrayList<Character> list){
        ArrayList<String> newList = new ArrayList<>();
        for(Character x : list){
            newList.add(x.toString());
        }
        Set<String> set = new HashSet<String>(newList);
        return set;
    }

            ///////////////////////////////////

    public void setYellowColor(View v){
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.YELLOW);

    }

    public void goToNextPage(View view  )   {

    Intent intent = new Intent(MainActivity.this,Main2Activity.class);
    intent.putExtra("tries", tries);
    startActivityForResult(intent, 0);

    }

            /////////////////////////////////////
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("word", word);
        editor.putStringSet("allSearched", aLtoSet(searchedHistory));
        editor.putStringSet("rightChar", aLtoSet(rightOnes));
        editor.putLong("millisLeft", timeLeftMS);
        editor.putInt("tries", tries);
        editor.apply();
    }





    @Override
    protected void onStart() {
        super.onStart();
        try {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            timeLeftMS = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
            word = prefs.getString("word", "hello");
            searchedHistory = setToAL(prefs.getStringSet("allSearched", null));
            rightOnes = setToAL(prefs.getStringSet("rightChar", null));
            tries = prefs.getInt("tries", 10);
        }
        catch(Exception e){

        }
    }






    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                    timeLeftMS = millisUntilFinished;
                    updateCountDownText();
            }

            @Override
            public void onFinish() {
                runningTime= false;
                ImageView img = findViewById(R.id.imageView);
                int pic = R.drawable.prog10;
                scale(img, pic);
                Button btn = findViewById(R.id.checkBtn);
                btn.setEnabled(false);
                TextView textView3 = findViewById(R.id.textinfo);
                textView3.setText(textView3.getText() + "\n" + "Word: " + word);
            }
        }.start();
        runningTime = true;

    }
    private void pauseTimer() {
        countDownTimer.cancel();
        runningTime = false;

    }
    private void resetTimer(){
        timeLeftMS = START_TIME_IN_MILLIS;
        updateCountDownText();
    }
    private void updateCountDownText(){
        int minutes = (int) (timeLeftMS / 1000) / 60;
        int seconds = (int) (timeLeftMS / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        mTextViewCD.setText(timeLeftFormatted);

        TextView textView3 = findViewById(R.id.textinfo);
        progressBar = findViewById(R.id.progressBar2);
        TextView textView = findViewById(R.id.textView2);
        StringBuilder temptx = new StringBuilder(textView.getText());
        progressBar.setProgress((int)(Double.parseDouble(temptx.substring(3))*(1.66666)));
    }

    private void scale(ImageView img, int pic){
        Display screen = getWindowManager().getDefaultDisplay();
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true; // turn on boundaries
        BitmapFactory.decodeResource(getResources(), pic, options);

        int imgWidth = options.outWidth;
        int screenWidth = screen.getWidth();

        if (imgWidth >  screenWidth){
            int ratio = Math.round( (float) imgWidth / (float) screenWidth);
            options.inSampleSize = ratio;
        }
        options.inJustDecodeBounds = false;
        Bitmap scaledImg = BitmapFactory.decodeResource(getResources(), pic, options);
        img.setImageBitmap(scaledImg);
    }




    public void stopGame(View view){
       try{
           pauseTimer();
           updateCountDownText();}

       catch(Exception e){

       }
        TextView textView3 = findViewById(R.id.textinfo);
        //TextView textView1 = (TextView) findViewById(R.id.textView);
        textView3.setText("");
        Button btn = findViewById(R.id.checkBtn);
        btn.setEnabled(true);
    }




    public void checkTries(){
        ImageView img = findViewById(R.id.imageView);
            switch (tries){
                case 0: {
                    int pic = R.drawable.prog10;
                    scale(img, pic);
                    Button btn = findViewById(R.id.checkBtn);
                    pauseTimer();
                    resetTimer();
                    updateCountDownText();
                    btn.setEnabled(false);
                    TextView textView3 = findViewById(R.id.textinfo);
                    textView3.setText(textView3.getText() + "\n" + "Word: " + word);
                    break;
                }
                case 1:{
                    int pic = R.drawable.prog9;
                    scale(img, pic);
                    break;
                }
                case 2:{
                    int pic = R.drawable.prog8;
                    scale(img, pic);
                    break;
                }
                case 3:{
                    int pic = R.drawable.prog7;
                    scale(img, pic);
                    break;
                }
                case 4:{
                    int pic = R.drawable.prog6;
                    scale(img, pic);
                    break;
                }
                case 5:{
                    int pic = R.drawable.prog5;
                    scale(img, pic);
                    break;
                }
                case 6:{
                    int pic = R.drawable.prog4;
                    scale(img, pic);
                    break;
                }
                case 7:{
                    int pic = R.drawable.prog3;
                    scale(img, pic);
                    break;
                }
                case 8:{
                    int pic = R.drawable.prog2;
                    scale(img, pic);
                    break;
                }
                case 9:{
                    int pic = R.drawable.prog1;
                    scale(img, pic);
                    break;
                }
                default:{
                    int pic = R.drawable.prog11;
                    scale(img, pic);
                }
        }
    }
    /*public void setBlanks(TextView tv){

        for (int x= 1 ; x <  word.length(); x++){
            temptxt.append("_ ");
        }
        temptxt.deleteCharAt(temptxt.length()-1);

        tv.setText(temptxt.toString());
    }*/
    public void saveRight(Character c) {
        TextView textView3 = findViewById(R.id.textinfo);
        textView3.setText("test3");

        //if(!rightOnes.isEmpty()){
            for (Character g : rightOnes){
                if(g==c){
                    return;
                }
            }
       // }

            rightOnes.add(c);
    }




    public void checkWin(){
        TextView textView3 = findViewById(R.id.textinfo);
        Button btn = findViewById(R.id.checkBtn);
        if(temptxt.indexOf("_") != -1){
            return;
        }
        else {
            textView3.setText("you won");
            pauseTimer();
            resetTimer();
            updateCountDownText();}
            btn.setEnabled(false);
        }




    public void buildWord(){
        TextView textView3 = findViewById(R.id.textinfo);
        temptxt.delete(0, temptxt.length());
        for (int x = 0 ; x < word.length() ; x++){
            temptxt.append("_ ");
            for (int y = 0 ; y < rightOnes.size() ; y++){
                textView3.setText("test1");
                if (word.toLowerCase().charAt(x)==rightOnes.get(y)){
                    temptxt.delete(temptxt.length()-2, temptxt.length());
                    temptxt.append(rightOnes.get(y) + " ");
                    textView3.setText("test");
                    break;
                }
            }
        }
        temptxt.deleteCharAt(temptxt.length()-1);
    }

    public void checkWord(View view){
        updateCountDownText();
        if(!runningTime){
            startTimer();
        }

        TextView textView1 = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.editText);
        TextView textView3 = findViewById(R.id.textinfo);
        boolean found = false;

        if(textView2.getText().length()!=0) {
            character = textView2.getText().toString().toLowerCase().charAt(0);
            boolean checkingWord = false;
            for(Character x :searchedHistory){
                if(x==character){
                    checkingWord = true;
                }
            }
            if (!checkingWord) {
                searchedHistory.add(character);
                checkingWord=true;
            }

            for (int x = 0 ; x < word.length() ; x++){
                if (word.toLowerCase().charAt(x)==(character)){
                    textView3.setText("hilol");
                   saveRight(character);
                   found=true;
                   break;
                }
            }
            if (found==false){
                tries--;
            }
            found=false;
            buildWord();
            textView1.setText(temptxt.toString());
            //textView3.setText(rightOnes.size()); // test
            textView3.setText("");
            textView3.setText("Searched characters:");
            for(Character x :searchedHistory){
               textView3.setText(textView3.getText() + " " + x.toString());
            }
        }
        else {
            textView3.setText("You need to type in a letter");
        }

        buildWord();
        textView1.setText(temptxt.toString());
        //textView3.setText(rightOnes.size()); // test
        textView3.setText("");
        textView3.setText( "Number of tries left: " + tries +"\n");
        textView3.setText(textView3.getText() + "Searched characters:");
        for(Character x :searchedHistory){
            textView3.setText(textView3.getText() + " " + x.toString());
        }
        checkTries();
        checkWin();
        textView2.setText("");
        textView2.requestFocus();
    }




        public void saveGame(View view){
            /*SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor=prefs.edit();

            editor.putString("word", word);
            editor.putStringSet("allSearched", aLtoSet(searchedHistory));
            editor.putStringSet("rightChar", aLtoSet(rightOnes));
            editor.putLong("millisLeft", timeLeftMS);
            editor.putInt("tries", tries);
            editor.apply();*/
    }



    public void resetStats(View view){
       try {
           SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
           prefs.edit().clear().apply();
       }
       catch(Exception e){

       }
        Resources res = getResources();

        ArrayList<String> nameList = new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.gameWordList)));
        Random r = new Random();
        word = nameList.get((r.nextInt(nameList.size())));

        try{
            resetTimer();
            pauseTimer();
            updateCountDownText();}
        catch (Exception e){

        }

        tries = 10;
        ImageView img = findViewById(R.id.imageView);
        TextView textView3 = findViewById(R.id.textinfo);
        TextView textView1 = findViewById(R.id.textView);
        rightOnes.clear();
        searchedHistory.clear();
        textView3.setText("");
        buildWord();
        try{
            textView1.setText(temptxt.toString());
        }
        catch(Exception e)
        {

        }
        int pic = R.drawable.prog0;
        scale(img, pic);
        Button btn = findViewById(R.id.checkBtn);
        btn.setEnabled(true);

    }

}

