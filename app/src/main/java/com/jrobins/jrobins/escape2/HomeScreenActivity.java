package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class HomeScreenActivity extends Activity {

    Button button;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpWindow();
        setUpPlayButton();
        setUpHelp();
        setUpResume();
        setUpMapCreator();
        setUpSettings();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpPlayButton(){
        button = ( Button ) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            intent = new Intent(HomeScreenActivity.this, ChoosePlayersActivity.class);

            startActivity(intent);
            }

        });
    }

    private void setUpHelp(){
        button = ( Button ) findViewById(R.id.rules);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(HomeScreenActivity.this, HelpActivity.class);

                startActivity(intent);
            }

        });
    }

    private void setUpMapCreator(){
        button = ( Button ) findViewById(R.id.create);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(HomeScreenActivity.this, MapCreatorActivity.class);

                startActivity(intent);
            }

        });
    }

    private void setUpSettings(){
        button = ( Button ) findViewById(R.id.settings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);

                startActivity(intent);
            }

        });
    }

    private void setUpResume(){
        button = ( Button ) findViewById(R.id.resume);
        SharedPreferences prefs = getSharedPreferences("current_game", Context.MODE_PRIVATE);
        boolean activeGame = prefs.getBoolean("activeGame", false);
        if(activeGame) {
            button.setVisibility(View.VISIBLE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(HomeScreenActivity.this, GamePlay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

        });
    }

    private void setUpWindow(){

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);

        //set width of sidebar based on screen width

        //sidebar = (LinearLayout) findViewById(R.id.sidebar);


    }
}
