package com.coffee.cafe.app.mobile.cafecoffee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Beverage;

public class CartAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Beverage> beverages;

    public CartAdapter(Context context, int  resource, ArrayList<Beverage> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.beverages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View cartItem = LayoutInflater.from(context).inflate(R.layout.fragment_cart_item, parent, false);
        TextView name = cartItem.findViewById(R.id.cart_item_name);
        TextView price = cartItem.findViewById(R.id.cart_item_price);
        TextView sumPrice = cartItem.findViewById(R.id.cart_item_sum_price);
        Beverage beverage = beverages.get(position);
        Log.d("cafe", "position : " + position);
        Log.d("cafe", "beverage" + beverage);
        Log.d("cafe", "name : " + beverage.getName());
        Log.d("cafe", "amount : " + beverage.getAmount());
        name.setText(beverage.getName());
        price.setText(beverage.getPrice()+"");
        sumPrice.setText("Total Price : " + beverage.getPrice("sum") + " X " + beverage.getAmount() + " = " + beverage.getPrice("total"));
        ListView condimentList = cartItem.findViewById(R.id.cart_item_condiment_list);
        CondimentAdapter condimentAdapter = new CondimentAdapter(context, R.layout.fragment_condiment_item, beverage.getCondimentList());
        condimentList.setAdapter(condimentAdapter);
        return cartItem;
    }
}
