package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffee.cafe.app.mobile.cafecoffee.R;

import java.util.ArrayList;

import Model.Order;

public class ShopOwnerOrderAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Order> orders;

    public ShopOwnerOrderAdapter(Context context, int  resource, ArrayList<Order> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.orders = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View orderList = LayoutInflater.from(context).inflate(R.layout.fragment_shop_owner_order_item, parent, false);
        TextView customerName = orderList.findViewById(R.id.shop_owner_order_customer_name);
        TextView time = orderList.findViewById(R.id.shop_owner_order_time);
        ImageView statusIcon = orderList.findViewById(R.id.status_icon);
//        TextView status = orderList.findViewById(R.id.shop_owner_order_status);
        Order order = orders.get(position);
        customerName.setText("Customer name : " + order.getCustomerName());
        time.setText("Order time : " + order.orderTimeReverse());
        if (order.getStatus().equals("in queue"))
        {
            statusIcon.setImageResource(R.drawable.status_inqueue);
        }
        else if (order.getStatus().equals("in progress"))
        {
            statusIcon.setImageResource(R.drawable.status_inprogrss);
        }
        else if (order.getStatus().equals("done"))
        {
            statusIcon.setImageResource(R.drawable.status_done);
        }
//        status.setText("Status : " + order.getStatus());
        return orderList;
    }
}
