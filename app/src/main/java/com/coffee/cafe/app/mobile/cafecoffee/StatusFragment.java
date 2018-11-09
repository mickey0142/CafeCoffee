package com.coffee.cafe.app.mobile.cafecoffee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapter.OrderAdapter;
import Model.Order;
import Model.Shop;
import Model.User;

public class StatusFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;
    ArrayList<Order> order = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        shop = (Shop) bundle.getSerializable("Shop object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null)
        {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initOrderList();
    }

    void initOrderList()
    {
        final ListView orderList = getView().findViewById(R.id.status_order_list);
        fbStore.collection("order").whereEqualTo("shopName", shop.getShopName()).whereEqualTo("customerName", user.getUsername())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null)
                        {
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("cafe", "Snapshot Listener Error : " + e.getMessage());
                            return;
                        }
                        else
                        {
                            ArrayList<Order> temp = new ArrayList<>();
                            for (QueryDocumentSnapshot document : documentSnapshots)
                            {
                                Order doc = document.toObject(Order.class);
                                temp.add(doc);
                            }
                            try
                            {
                                order = (ArrayList<Order>) temp.clone();
                                Log.d("cafe", "from status getactivity : " + getActivity());
                                OrderAdapter orderAdapter = new OrderAdapter(getActivity(), R.layout.fragment_order_item, order);
                                orderList.setAdapter(orderAdapter);
                            }
                            catch (NullPointerException ex)
                            {
                                Log.d("cafe", "catch NullPointerException" + ex.getMessage());

                            }
                            Log.d("cafe", "data changed");
                        }
                    }
                });
    }
}
