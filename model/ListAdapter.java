package com.example.lab1.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lab1.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<Drug> {
    Context context;
    int layoutResourceId;
    ArrayList<Drug> data = null;

    public ListAdapter(Context context, int layoutResourceId, List<Drug> drugs)
    {
        super(context,layoutResourceId, drugs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = (ArrayList)drugs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        HeaderHolder headerHolder;

        if(row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            headerHolder = new HeaderHolder();
            headerHolder.name = row.findViewById(R.id.nameTextView);
            headerHolder.quantity = row.findViewById(R.id.quantityTextView);

            row.setTag(headerHolder);
        }
        else {
            headerHolder = (HeaderHolder) row.getTag();
        }

        Drug drug = data.get(position);
        headerHolder.name.setText(drug.getName());
        headerHolder.quantity.setText(NumberFormat.getInstance().format(drug.getQuantity()));

        return row;
    }

    private class HeaderHolder {
        public TextView name;
        public TextView quantity;
    }

}
