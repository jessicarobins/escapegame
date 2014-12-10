package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrobins.jrobins.escape2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerChoiceAdapter extends ArrayAdapter<Player> {
    private LayoutInflater inflater;


    public ArrayList<Player> players;
    private HashMap<String, String> playerNameValues = new HashMap<String, String>();
    private HashMap<String, Integer> buttonColorValues = new HashMap<String, Integer>();
    private Activity activity;



    public PlayerChoiceAdapter(Activity activity, List<Player> players){
        super(activity, R.layout.player_choice, players);
        this.players = (ArrayList<Player>) players;
        this.activity = activity;
        inflater = activity.getWindow().getLayoutInflater();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_choice, parent, false);

        }

        final int p = position;
        final EditText playerName = (EditText) convertView.findViewById(R.id.playerName);
        //set the tag of the textbox so we can use it in the hashmap
        playerName.setTag("theFirstEditTextAtPos:"+position);

        //if that tag does not exist in the hashmap, put the default player value there
        if(!playerNameValues.containsKey(playerName.getTag()))
            playerNameValues.put(playerName.getTag().toString(), players.get(position).name());
         /*
        else {
            //set the text then add the tag?
            playerName.setText(players.get(position).name());

        }*/
        playerName.setText(playerNameValues.get(playerName.getTag().toString()));


        //playerName.setText("Player " + position);

        final Button button = (Button) convertView.findViewById(R.id.button);
        button.setTag("button"+position);
        if(!buttonColorValues.containsKey(button.getTag()))
            buttonColorValues.put(button.getTag().toString(), new Integer(players.get(position).color()));

        button.setBackgroundColor(buttonColorValues.get(button.getTag()));
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(button);
            }
        });

        //playerNameValues.put(playerName.getTag().toString(), playerName.getText().toString());

        playerName.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    button.setText("X");
                    players.get(p).setName(null);
                }
                else {
                    if (s.length() == 1)
                        button.setText(playerName.getText().toString().substring(0, 1).toUpperCase());
                    else {
                        String btnText = playerName.getText().toString().substring(0, 1).toUpperCase() +
                                playerName.getText().toString().substring(1,2).toLowerCase();
                        button.setText(btnText);
                    }

                    //players.get(p).setName(playerName.getText().toString());
                    //System.out.println("setting player name = " +playerName.getText().toString());
                }
                PlayerChoiceAdapter.this.playerNameValues.put(playerName.getTag().toString(), s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });


        playerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    players.get(p).setName(playerName.getText().toString());
                    System.out.println("setting name to " + playerName.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return convertView ;
    }

    public String getPlayerName(int position){
        //here you need to recreate the id for the first editText
        String result = playerNameValues.get("theFirstEditTextAtPos:"+position);
        if(result ==null)
            result = "default value";

        return result;
    }

    @Override
    public Player getItem(int position){
        String name = playerNameValues.get("theFirstEditTextAtPos:"+position);
        if(name ==null)
            name = "default value";

        int color = buttonColorValues.get("button"+position);

        return new Player(name, color);
    }

    private void showAlertDialog(final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //final Button button = (Button) findViewById(R.id.button);
        GridView gridView = new GridView(getContext());
        //gridView.setBackgroundColor(Color.TRANSPARENT);
        //int w = getContext().getResources().getDisplayMetrics().widthPixels;
        //gridView.setLayoutParams(new AbsListView.LayoutParams(w/2, AbsListView.LayoutParams.WRAP_CONTENT));

        final int[] colors = getContext().getResources().getIntArray(R.array.player_color_choices);

        gridView.setAdapter(new ColorChoiceGridViewAdapter(getContext(), colors));
        gridView.setNumColumns(4);
        builder.setView(gridView);
        final AlertDialog alertDialog = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something here
                button.setBackgroundColor(colors[position]);
                buttonColorValues.put(button.getTag().toString(), new Integer(colors[position]));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        //alertDialog.getWindow().setLayout(400, 800);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 400;

        //lp.height = 800;
        //lp.x=-170;
        //lp.y=100;
        alertDialog.getWindow().setAttributes(lp);
    }
}