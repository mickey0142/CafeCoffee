package com.coffee.cafe.app.mobile.cafecoffee;

import android.animation.Animator;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Beverage;
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
        Beverage.setMenuPrice(shop.getMenuPrice());
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null)
        {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initStatusButton();
        initCartButton();
        initCappuccino();
        initEspresso();
        initAmericano();
        initMacchiato();
        initLatte();
        initMocha();
        initCocoa();
    }

    void initStatusButton()
    {
        Button statusButton = getView().findViewById(R.id.shop_status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new StatusFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putSerializable("Shop object", shop);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
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
                bundle.putSerializable("Shop object", shop);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
    }

    void setMenuClickAnimation(int mainLayoutId, final int optionLayoutId)
    {
        final LinearLayout mainLayout = getView().findViewById(mainLayoutId);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout optionLayout = getView().findViewById(optionLayoutId);
                if (optionLayout.getVisibility() == View.GONE)
                {
                    optionLayout.setAlpha(0f);
                    optionLayout.setVisibility(View.VISIBLE);
                    optionLayout.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }
                else if (optionLayout.getVisibility() == View.VISIBLE)
                {
                    optionLayout.animate()
                            .alpha(0f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    optionLayout.setVisibility(View.GONE);
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
    }

    void initOption(int layoutId, String type)
    {
        final String coffeeType = type;
        final Beverage beverage = new Beverage(coffeeType);
        beverage.setSize("normal");
        beverage.setType("hot");
        beverage.setAmount(1);
        LinearLayout linearLayout = getView().findViewById(layoutId);
        final TextView price = linearLayout.findViewById(R.id.option_price);
        String priceStr = "Price : " + beverage.getPrice();
        if (beverage.getAmount() > 1)
        {
            priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
        }
        price.setText(priceStr);

        final Button normalButton = linearLayout.findViewById(R.id.option_size_normal_button);
        final Button bigButton = linearLayout.findViewById(R.id.option_size_big_button);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalButton.setBackgroundColor(Color.parseColor("#FFFF00"));
                bigButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                beverage.setSize("normal");
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });
        bigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                bigButton.setBackgroundColor(Color.parseColor("#FFFF00"));
                beverage.setSize("big");
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });

        final Button hotButton = linearLayout.findViewById(R.id.option_hot_button);
        final Button coldButton = linearLayout.findViewById(R.id.option_cold_button);
        final Button frappeButton = linearLayout.findViewById(R.id.option_frappe_button);
        hotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotButton.setBackgroundColor(Color.parseColor("#FFFF00"));
                coldButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                frappeButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                beverage.setType("hot");
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });
        coldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                coldButton.setBackgroundColor(Color.parseColor("#FFFF00"));
                frappeButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                beverage.setType("cold");
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });
        frappeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                coldButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
                frappeButton.setBackgroundColor(Color.parseColor("#FFFF00"));
                beverage.setType("frappe");
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });

        final TextView amount = linearLayout.findViewById(R.id.option_amount);
        Button increaseButton = linearLayout.findViewById(R.id.option_increase_amount);
        Button decreaseButton = linearLayout.findViewById(R.id.option_decrease_amount);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(amount.getText().toString());
                num += 1;
                amount.setText(num+"");
                beverage.setAmount(Integer.parseInt(amount.getText().toString()));
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(amount.getText().toString());
                if (num > 1 )
                {
                    num -= 1;
                }
                amount.setText(num+"");
                beverage.setAmount(Integer.parseInt(amount.getText().toString()));
                String priceStr = "Price : " + beverage.getPrice();
                if (beverage.getAmount() > 1)
                {
                    priceStr += " X " + beverage.getAmount() + " = " + beverage.getPrice("total");
                }
                price.setText(priceStr);
            }
        });

        final EditText moreDetail = linearLayout.findViewById(R.id.option_more_detail);

        Button addButton = linearLayout.findViewById(R.id.option_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beverage.setMoreDetail(moreDetail.getText().toString());
                order.addBeverage(beverage);
                order.setStatus("in queue");
                order.setCustomerName(user.getUsername());
                Toast.makeText(getContext(), coffeeType + " added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void initCappuccino()
    {
        setMenuClickAnimation(R.id.shop_cappuccino, R.id.shop_cappuccino_option);
        initOption(R.id.shop_cappuccino_option, "Cappuccino");
    }
    void initEspresso()
    {
        setMenuClickAnimation(R.id.shop_espresso, R.id.shop_espresso_option);
        initOption(R.id.shop_espresso_option, "Espresso");
    }
    void initAmericano()
    {
        setMenuClickAnimation(R.id.shop_americano, R.id.shop_americano_option);
        initOption(R.id.shop_americano_option, "Americano");
    }

    void initMacchiato()
    {
        setMenuClickAnimation(R.id.shop_macchiato, R.id.shop_macchiato_option);
        initOption(R.id.shop_macchiato_option, "Macchiato");
    }

    void initLatte()
    {
        setMenuClickAnimation(R.id.shop_latte, R.id.shop_latte_option);
        initOption(R.id.shop_latte_option, "Latte");
    }

    void initMocha()
    {
        setMenuClickAnimation(R.id.shop_mocha, R.id.shop_mocha_option);
        initOption(R.id.shop_mocha_option, "Mocha");
    }

    void initCocoa()
    {
        setMenuClickAnimation(R.id.shop_cocoa, R.id.shop_cocoa_option);
        initOption(R.id.shop_cocoa_option, "Cocoa");
    }
}
