package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ColorChoiceGridViewAdapter extends BaseAdapter {
    int [] colors;
    String [] letters;
    Context context;

    public ColorChoiceGridViewAdapter(Context context, int[]colors) {
        this.context = context;
        this.colors= colors;
        this.letters =null;
    }

    public ColorChoiceGridViewAdapter(Context context, int[]colors, String[] letters) {
        this.context = context;
        this.colors= colors;
        this.letters = letters;
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

        //view.setLayoutParams(new AbsListView.LayoutParams(100, 100));
        view.setBackgroundColor(colors[position]);
        if(letters != null && letters.length > 0) {
            view.setText(letters[position]);
        }
        else
            view.setText(" ");
        view.setGravity(Gravity.CENTER);
        view.setTextSize(30);
        Rect bounds = new Rect();
        Paint textPaint = view.getPaint();

        textPaint.getTextBounds("MM",0,2,bounds);
        int h = bounds.width();
        view.setHeight(h);
        view.setWidth(h);
        return view;
    }
}
