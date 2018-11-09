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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapter.ShopListAdapter;
import Model.Shop;
import Model.User;

public class CustomerHomeFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    ArrayList<Shop> shopList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLogoutButton();
        initShopList();
    }

    void initLogoutButton()
    {
        Button logoutButton = getView().findViewById(R.id.customer_home_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAuth.signOut();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

    void initShopList()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.customer_home_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        fbStore.collection("shop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    ArrayList<Shop> temp = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Shop shop = document.toObject(Shop.class);
                        temp.add(shop);
                    }
                    shopList = (ArrayList<Shop>) temp.clone();
                    try {
                        ListView shopListView = getView().findViewById(R.id.customer_home_shop_list);
                        ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(), R.layout.fragment_shop_list_item, shopList);
                        shopListView.setAdapter(shopListAdapter);
                        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Fragment fragment = new ShopFragment();
                                Bundle bundle = new Bundle();
                                Log.d("test", shopList.get(position).toString());
                                bundle.putSerializable("Shop object", shopList.get(position));
                                bundle.putSerializable("User object", user);
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                            }
                        });
                    }
                    catch (NullPointerException e)
                    {
                        Log.d("cafe", "catch nullpointer : " + e.getMessage());
                    }
                }
                else
                {
                    Log.d("cafe", "get shop list error : " + task.getException());
                }
            }
        });
    }
}
