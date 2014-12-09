package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ColorChoiceGridViewAdapter extends BaseAdapter {
    int [] colors;
    Context context;

    public ColorChoiceGridViewAdapter(Context context, int[]colors) {
        this.context = context;
        this.colors= colors;
    }
    @Override
    public int getCount() {
        if(colors == null)
            return 0;
        else
            return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;

        if (convertView == null) {
            view = new TextView(context);
        }
        else {
            view = (TextView) convertView;
        }

        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(colors[position]);
        return view;
    }
}
