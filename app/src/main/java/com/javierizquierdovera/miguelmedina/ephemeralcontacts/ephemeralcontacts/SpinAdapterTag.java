package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Tag;

/**
 * Created by lifka on 2/01/17.
 */

public class SpinAdapterTag extends ArrayAdapter<Tag> {

    private Context context;
    private ArrayList<Tag> tags;

    public SpinAdapterTag(Context context, int resource, ArrayList<Tag> tags) {
        super(context, resource);
        this.context = context;
        this.tags = tags;
    }

    public int getCount(){
        return tags.size();
    }

    public Tag getItem(int position){
        return tags.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView label = new TextView(context);

        label.setText(tags.get(position).getTag());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        label.setBackgroundColor(ContextCompat.getColor(context, R.color.SpinColor));
        label.setPadding(60,25,60,25);
        label.setText(tags.get(position).getTag());

        return label;
    }
}
