package com.coffee.cafe.app.mobile.cafecoffee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Order;
import Model.User;

public class CartFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Order order = new Order();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        order = (Order) bundle.getSerializable("Order object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCartList();
        initSumPrice();
        initConfirmButton();
    }

    void initCartList()
    {
        ListView cartList = getView().findViewById(R.id.cart_list);
        CartAdapter cartAdapter = new CartAdapter(getActivity(), R.layout.fragment_cart_item, order.getBeverages());
        cartAdapter.setOrder(order);
        TextView cartPrice = getView().findViewById(R.id.cart_sum_price);
        cartAdapter.setCartPrice(cartPrice);
        cartList.setAdapter(cartAdapter);
    }

    void initSumPrice()
    {
        TextView sumPrice = getView().findViewById(R.id.cart_sum_price);
        int sum = order.getSumPrice();
        sumPrice.setText("Total Price : " + sum);
    }

    void initConfirmButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.cart_progress_bar);
        Button confirmButton = getView().findViewById(R.id.cart_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                order.calculateSumPrice();
                if (order.getSumPrice() == 0)
                {
                    Toast.makeText(getContext(), "cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                fbStore.collection("order").add(order)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "order success", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new StatusFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User object", user);
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.main_view, fragment).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("cafe", "add cart to firebase error : " + e.getMessage());
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
