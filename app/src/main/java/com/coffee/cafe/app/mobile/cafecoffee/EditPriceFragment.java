package com.coffee.cafe.app.mobile.cafecoffee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import Model.Shop;
import Model.User;

public class EditPriceFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;

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
        return inflater.inflate(R.layout.fragment_edit_price, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initText();
        initUpdateButton();
    }

    void initText()
    {
        TextView cappuccino = getView().findViewById(R.id.edit_price_cappuccino);
        TextView espresso = getView().findViewById(R.id.edit_price_espresso);
        TextView americano= getView().findViewById(R.id.edit_price_americano);
        TextView macchiato = getView().findViewById(R.id.edit_price_macchiato);
        TextView latte= getView().findViewById(R.id.edit_price_latte);
        TextView mocha = getView().findViewById(R.id.edit_price_mocha);
        TextView cocoa = getView().findViewById(R.id.edit_price_cocoa);
        cappuccino.setText(shop.getMenuPrice().get("Cappuccino")+"");
        espresso.setText(shop.getMenuPrice().get("Espresso")+"");
        americano.setText(shop.getMenuPrice().get("Americano")+"");
        macchiato.setText(shop.getMenuPrice().get("Macchiato")+"");
        latte.setText(shop.getMenuPrice().get("Latte")+"");
        mocha.setText(shop.getMenuPrice().get("Mocha")+"");
        cocoa.setText(shop.getMenuPrice().get("Cocoa")+"");
    }

    void initUpdateButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.edit_price_progress_bar);
        Button updateButton = getView().findViewById(R.id.edit_price_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                TextView cappuccino = getView().findViewById(R.id.edit_price_cappuccino);
                TextView espresso = getView().findViewById(R.id.edit_price_espresso);
                TextView americano= getView().findViewById(R.id.edit_price_americano);
                TextView macchiato = getView().findViewById(R.id.edit_price_macchiato);
                TextView latte= getView().findViewById(R.id.edit_price_latte);
                TextView mocha = getView().findViewById(R.id.edit_price_mocha);
                TextView cocoa = getView().findViewById(R.id.edit_price_cocoa);
                try {
                    HashMap<String, Integer> menuPrice = new HashMap<>();
                    menuPrice.put("Cappuccino", Integer.parseInt(cappuccino.getText().toString()));
                    menuPrice.put("Espresso", Integer.parseInt(espresso.getText().toString()));
                    menuPrice.put("Americano", Integer.parseInt(americano.getText().toString()));
                    menuPrice.put("Macchiato", Integer.parseInt(macchiato.getText().toString()));
                    menuPrice.put("Latte", Integer.parseInt(latte.getText().toString()));
                    menuPrice.put("Mocha", Integer.parseInt(mocha.getText().toString()));
                    menuPrice.put("Cocoa", Integer.parseInt(cocoa.getText().toString()));
                    shop.setMenuPrice(menuPrice);
                    fbStore.collection("shop").document(shop.getShopName()).set(shop)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("cafe", "update success");
                                    Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                                    getFragmentManager().popBackStack();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("cafe", "update fail : " + e.getMessage());
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (NumberFormatException e)
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "some field isn't a number", Toast.LENGTH_SHORT).show();
                    Log.d("cafe", "some field isn't a number");
                }
            }
        });
    }
}
