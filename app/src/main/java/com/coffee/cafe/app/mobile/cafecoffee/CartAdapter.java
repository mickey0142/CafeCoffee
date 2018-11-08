package com.coffee.cafe.app.mobile.cafecoffee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Beverage;
import Model.Order;

public class CartAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Beverage> beverages;
    Order order;
    TextView cartPrice;

    public CartAdapter(Context context, int  resource, ArrayList<Beverage> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.beverages = objects;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    public void setCartPrice(TextView cartPrice)
    {
        this.cartPrice = cartPrice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View cartItem = LayoutInflater.from(context).inflate(R.layout.fragment_cart_item, parent, false);
        TextView name = cartItem.findViewById(R.id.cart_item_name);
        TextView price = cartItem.findViewById(R.id.cart_item_price);
        TextView sumPrice = cartItem.findViewById(R.id.cart_item_sum_price);
        TextView moreDetail = cartItem.findViewById(R.id.cart_item_more_detail);
        final Beverage beverage = beverages.get(position);
        name.setText(beverage.getType() + " " + beverage.getName() + " " + beverage.getSize());
        price.setText(beverage.getPrice()+"");
        sumPrice.setText("Total Price : " + beverage.getPrice() + " X " + beverage.getAmount() + " = " + beverage.getPrice("total"));
        moreDetail.setText(beverage.getMoreDetail());
        Button removeButton = cartItem.findViewById(R.id.cart_item_remove_button);
        final int pos = position;
        final CartAdapter temp = this;
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beverages.remove(pos);
                int num = order.getSumPrice();
                cartPrice.setText("Total Price : " + num);
                temp.notifyDataSetChanged();
            }
        });
        Button increaseButton = cartItem.findViewById(R.id.cart_item_increase_amount);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beverage.increaseAmount();
                int num = order.getSumPrice();
                cartPrice.setText("Total Price : " + num);
                temp.notifyDataSetChanged();
            }
        });
        Button decreaseButton = cartItem.findViewById(R.id.cart_item_decrease_amount);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beverage.decreaseAmount();
                int num = order.getSumPrice();
                cartPrice.setText("Total Price : " + num);
                temp.notifyDataSetChanged();
            }
        });
        return cartItem;
    }
}
