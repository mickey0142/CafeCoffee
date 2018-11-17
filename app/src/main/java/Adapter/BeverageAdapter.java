package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coffee.cafe.app.mobile.cafecoffee.R;

import java.util.ArrayList;

import Model.Beverage;

public class BeverageAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Beverage> beverages;

    public BeverageAdapter(Context context, int  resource, ArrayList<Beverage> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.beverages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View beverageList = LayoutInflater.from(context).inflate(R.layout.fragment_beverage_item, parent, false);
        Beverage beverage = beverages.get(position);
        TextView name = beverageList.findViewById(R.id.beverage_item_name);
        TextView sumPrice = beverageList.findViewById(R.id.beverage_item_sum_price);
        TextView moreDetail = beverageList.findViewById(R.id.beverage_item_more_detail);
        name.setText("Menu : " + beverage.getType() + " " + beverage.getName() + " " + beverage.getSize());
        sumPrice.setText("Price : " + beverage.getPrice() + " X " + beverage.getAmount() + " = " + beverage.getPrice("total") + " ฿");
        moreDetail.setText(beverage.getMoreDetail());
        Log.d("cafe", "price : " + beverage.getPrice());
        Log.d("cafe", "amount : " + beverage.getAmount());
        Log.d("cafe", "total : " + beverage.getPrice("total"));
        if (position % 2 == 1)
        {
            beverageList.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }
        return beverageList;
    }
}
