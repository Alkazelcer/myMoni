package com.example.peka.moneytracker.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.peka.moneytracker.R;
import com.example.peka.moneytracker.models.Tag;

import java.util.List;

/**
 * Created by peka on 18.05.17.
 */

public class TagListAdapter extends ArrayAdapter<Tag> {
    private final Context context;
    private final List<Tag> values;

    public TagListAdapter(Context context, List<Tag> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tag_listview_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.listview_item);
        textView.setText(values.get(position).getName());

        return rowView;
    }

}
