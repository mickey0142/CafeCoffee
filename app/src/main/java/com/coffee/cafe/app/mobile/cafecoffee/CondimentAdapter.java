package com.coffee.cafe.app.mobile.cafecoffee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Condiment;

public class CondimentAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Condiment> condiments;

    public CondimentAdapter(Context context, int  resource, ArrayList<Condiment> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.condiments = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View condimentItem = LayoutInflater.from(context).inflate(R.layout.fragment_condiment_item, parent, false);
        TextView name = condimentItem.findViewById(R.id.condiment_item_name);
        TextView price = condimentItem.findViewById(R.id.condiment_item_price);
        Condiment condiment = condiments.get(position);
        Log.d("cafe", "condiment : " + condiment);
        Log.d("cafe", "position : " + position);
        Log.d("cafe", "name : " + condiment.getName());
        name.setText(condiment.getName());
        price.setText(condiment.getPrice() + " X " + condiment.getAmount() + " = " + condiment.getSumPrice());
        return condimentItem;
    }
}
