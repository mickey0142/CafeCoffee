package com.coffee.cafe.app.mobile.cafecoffee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Beverage;
import Model.Order;

public class OrderAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Order> orders;

    public OrderAdapter(Context context, int  resource, ArrayList<Order> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.orders = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View orderList = LayoutInflater.from(context).inflate(R.layout.fragment_order_item, parent, false);
        TextView status = orderList.findViewById(R.id.order_item_status);
        Order order = orders.get(position);
        status.setText("Status : " + order.getStatus());
        TextView time = orderList.findViewById(R.id.order_item_time);
        time.setText("Order Time : " + order.orderTimeReverse());
        TextView sumPrice = orderList.findViewById(R.id.order_item_sum_price);
        int num = order.getSumPrice();
        sumPrice.setText("Total Price : " + num);
        ListView beverageList = orderList.findViewById(R.id.order_item_list);
        BeverageAdapter beverageAdapter = new BeverageAdapter(context, R.layout.fragment_beverage_item, order.getBeverages());
        beverageList.setAdapter(beverageAdapter);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToPixel(60*order.getBeverages().size()));
        beverageList.setLayoutParams(layoutParams);
        return orderList;
    }

    public int convertToPixel(double dp)
    {
        float scale = getContext().getResources().getDisplayMetrics().density;
        int pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }
}
