package com.coffee.cafe.app.mobile.cafecoffee;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import Adapter.OrderAdapter;
import Adapter.ShopOwnerOrderAdapter;
import Model.Order;
import Model.Shop;
import Model.User;

public class ShopOwnerHomeFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;
    ArrayList<Order> orders;
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
        return inflater.inflate(R.layout.fragment_shop_owner_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkAuthen();
        initWelcomeText();
        initEditPriceButton();
        initLogoutButton();
        initOrderList();
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

    void initWelcomeText()
    {
        TextView welcomeText = getView().findViewById(R.id.shop_owner_home_welcome_text);
        welcomeText.setText("Shop " + shop.getShopName() + " Welcome " + shop.getOwner());
    }

    void initEditPriceButton()
    {
        Button editPriceButton = getView().findViewById(R.id.shop_owner_home_edit_price_button);
        editPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putSerializable("Shop object", shop);
                Fragment fragment = new EditPriceFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
    }

    void initLogoutButton()
    {
        Button logoutButton = getView().findViewById(R.id.shop_owner_home_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAuth.signOut();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .commit();
            }
        });
    }
    void initOrderList()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.shop_owner_home_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        fbStore.collection("order").whereEqualTo("shopName", shop.getShopName()).orderBy("orderTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null)
                {
                    progressBar.setVisibility(View.GONE);
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
                        progressBar.setVisibility(View.GONE);
                        orders = (ArrayList<Order>) temp.clone();
                        Log.d("cafe", "from shopOwnerHome getactivity : " + getActivity());
                        ListView orderList = getView().findViewById(R.id.shop_owner_order_list);
                        ShopOwnerOrderAdapter adapter = new ShopOwnerOrderAdapter(getActivity(), R.layout.fragment_shop_owner_order_item, orders);
                        orderList.setAdapter(adapter);
                        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("User object", user);
                                bundle.putSerializable("Order object", orders.get(position));
                                bundle.putSerializable("Shop object", shop);
                                Fragment fragment = new UpdateStatusFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                            }
                        });

                    }
                    catch (NullPointerException ex)
                    {
                        progressBar.setVisibility(View.GONE);
                        Log.d("cafe", "catch NullPointerException" + ex.getMessage());

                    }
                    Log.d("cafe", "data changed");
                }
            }
        });
    }

    void initSetShopLocationButton()
    {
        final ProgressBar progressBar2 = getView().findViewById(R.id.shop_owner_home_progress_bar_location);
        Button setLocationButton = getView().findViewById(R.id.shop_owner_home_set_location);
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
                                        Log.d("cafe", "location is null in shopOwnerHomeFragment");
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
}
