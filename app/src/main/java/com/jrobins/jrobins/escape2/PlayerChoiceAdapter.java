package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jrobins.jrobins.escape2.R;

public class PlayerChoiceAdapter extends ArrayAdapter {
    private LayoutInflater inflater;

    public PlayerChoiceAdapter(Activity activity, String[] items){
        super(activity, R.layout.player_choice, items);
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_choice, parent, false);
        }

        //Button yourButton= (Button)  convertView  .findViewById(R.id.YOUR_BUTTON_ID);
        //yourButton.setOnClickListener(new OnClickListener()

        /*
        {
            @Override
            public void onClick(View v)
            {
                // Your code that you want to execute on this button click
                Intent myIntent = new Intent(CurrentActivity.this, NextActivity.class);
                CurrentActivity.this.startActivity(myIntent);

            }

        });*/

        final EditText playerName = (EditText) convertView.findViewById(R.id.playerName);
        final Button button = (Button) convertView.findViewById(R.id.button);

        playerName.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    button.setText("X");
                else
                    button.setText(playerName.getText().toString().substring(0,1).toUpperCase());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        return convertView ;
    }

}