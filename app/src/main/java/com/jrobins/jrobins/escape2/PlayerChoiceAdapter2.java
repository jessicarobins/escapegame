package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jrobins on 11/29/2014.
 */
public class PlayerChoiceAdapter2 extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data;

    public PlayerChoiceAdapter2(Context context, ArrayList<HashMap<String,String>> data){

        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int pos) {
        return data.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.player_choice,null,false);
            holder.playerName = (EditText) convertView.findViewById(R.id.playerName);
            holder.playerColor = (Button) convertView.findViewById(R.id.button);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.playerName.setText(data.get(position).get("name"));
        holder.playerColor.setBackgroundColor(Integer.parseInt(data.get(position).get("value")));

        convertView.setTag(holder);
        return convertView;
    }

    class ViewHolder {
        EditText playerName;
        Button playerColor;
    }

}
