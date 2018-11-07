package com.coffee.cafe.app.mobile.cafecoffee;

import android.animation.Animator;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Beverage;
import Model.Condiment;
import Model.Order;
import Model.Shop;
import Model.User;

public class ShopFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;
    Order order = new Order();

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
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCartButton();
        initCappuccino();
    }

    void initCartButton()
    {
        Button cartButton = getView().findViewById(R.id.shop_cart_button);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CartFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Order object", order);
                bundle.putSerializable("User object", user);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
    }

    void initCappuccino()
    {
        LinearLayout cappuccinoLayout = getView().findViewById(R.id.shop_cappuccino);
        cappuccinoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout cappuccinoOption = getView().findViewById(R.id.shop_cappuccino_option);
                if (cappuccinoOption.getVisibility() == View.GONE)
                {
                    cappuccinoOption.setAlpha(0f);
                    cappuccinoOption.setVisibility(View.VISIBLE);
                    cappuccinoOption.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }
                else if (cappuccinoOption.getVisibility() == View.VISIBLE)
                {
                    cappuccinoOption.animate()
                            .alpha(0f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    cappuccinoOption.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                }
            }
        });
        Button cappuccinoAddButton = getView().findViewById(R.id.shop_cappuccino_add_button);
        cappuccinoAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beverage beverage = new Beverage("Cappuccino");
                beverage.setAmount(1);// set amount here later
                Condiment condiment = new Condiment("Whip");
                condiment.setAmount(1);
                beverage.addCondiment(condiment);
                order.addBeverage(beverage);
                order.setStatus("in queue");
                order.setCustomerName(user.getUsername());
                Toast.makeText(getContext(), "Cappuccino added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
