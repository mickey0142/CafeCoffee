package com.coffee.cafe.app.mobile.cafecoffee;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapter.ShopOwnerOrderAdapter;
import Model.Beverage;
import Model.Order;
import Model.Shop;
import Model.User;

public class ShopOwnerHomeFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;
    ArrayList<Order> orders;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        shop = (Shop) bundle.getSerializable("Shop object");
        Log.d("test", "user : " + user);
        Beverage.setMenuPrice(shop.getMenuPrice());
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
        initOrderList();
        initNavBar();
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
        TextView shopText = getView().findViewById(R.id.shop_owner_home_shop_name);
        shopText.setText(shop.getShopName());
        TextView welcomeText = getView().findViewById(R.id.shop_owner_home_welcome_text);
        welcomeText.setText(" Welcome " + shop.getOwner());
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



    void initNavBar()
    {
        LinearLayout editPriceButton = getView().findViewById(R.id.shop_owner_home_edit_price_button);
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

        LinearLayout logoutButton = getView().findViewById(R.id.shop_owner_home_logout_button);
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
