package com.coffee.cafe.app.mobile.cafecoffee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        shop = (Shop) bundle.getSerializable("Shop object");
        Log.d("test", "user : " + user);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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

        checkAuthen();
        initText();
        initUpdateButton();
        initNavBar();
        initSetShopLocationButton();
    }

    void checkAuthen()
    {
        if (fbAuth.getCurrentUser() == null)
        {
            Log.d("cafe", "not logged in return to login page");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_view, new LoginFragment())
                    .commit();
        }
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

    void initSetShopLocationButton()
    {
        final ProgressBar progressBar2 = getView().findViewById(R.id.edit_price_progress_bar_location);
        Button setLocationButton = getView().findViewById(R.id.edit_price_set_location);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.setVisibility(View.VISIBLE);
                try {
                    MapsInitializer.initialize(getActivity().getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                }
                try{
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null)
                                    {
                                        HashMap<String, Double> shopPosition = new HashMap<>();
                                        shopPosition.put("latitude", location.getLatitude());
                                        shopPosition.put("longitude", location.getLongitude());
                                        shop.setShopPosition(shopPosition);
                                    }
                                    else
                                    {
                                        Log.d("cafe", "location is null in shopOwnerHomeFragment" + location + fusedLocationClient);
                                        Toast.makeText(getContext(), "set shop location fail (error code = 1)", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar2.setVisibility(View.GONE);
                            Log.d("cafe", "get last location fail : " + e.getMessage());
                            Toast.makeText(getContext(), "set shop location fail (error code = 2)", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            fbStore.collection("shop").document(shop.getShopName()).set(shop)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar2.setVisibility(View.GONE);
                                            Log.d("cafe", "set location success");
                                            Toast.makeText(getContext(), "set location success", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar2.setVisibility(View.GONE);
                                    Log.d("cafe", "add shop error : " + e.getMessage());
                                    Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                catch (SecurityException e)
                {
                    progressBar2.setVisibility(View.GONE);
                    Log.d("cafe", "location permission not granted : " +e.getMessage());
                    Toast.makeText(getContext(), "set shop location fail (error code = 3)" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                catch (NullPointerException e)
                {
                    progressBar2.setVisibility(View.GONE);
                    Log.d("cafe", "catch NullPointerException : " + e.getMessage());
                    Toast.makeText(getContext(), "set shop location fail (error code = 4)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void initNavBar()
    {
        LinearLayout orderButton = getView().findViewById(R.id.edit_price_order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putSerializable("Shop object", shop);
                Fragment homeFragment = new ShopOwnerHomeFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).addToBackStack(null).commit();
            }
        });

        LinearLayout logoutButton = getView().findViewById(R.id.edit_price_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAuth.signOut();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want log out ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("cafe", "ERROR: dialog show.");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_view, new LoginFragment())
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("cafe", "ERROR: not active.");
                    }
                });
                builder.show();
            }
        });
    }
}
