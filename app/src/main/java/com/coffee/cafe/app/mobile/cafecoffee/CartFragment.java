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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import Adapter.CartAdapter;
import Model.Order;
import Model.Shop;
import Model.User;

public class CartFragment extends Fragment {

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
        order = (Order) bundle.getSerializable("Order object");
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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkAuthen();
        initBackButton();
        initCartList();
        initSumPrice();
        initConfirmButton();
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

    void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.cart_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
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
        sumPrice.setText("Total Price : " + sum + " ฿");
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
                order.setShopName(shop.getShopName());
                if (order.getSumPrice() == 0)
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                order.setOrderTime();
                order.setShopId(shop.getDocumentId());
                fbStore.collection("order").add(order)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        fbStore.collection("order").document(documentReference.getId())
                                .update("documentId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "order success", Toast.LENGTH_SHORT).show();
                                        Fragment fragment = new StatusFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("User object", user);
                                        bundle.putSerializable("Shop object", shop);
                                        fragment.setArguments(bundle);
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                        ft.replace(R.id.main_view, fragment).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("cafe", "add documentId to firebase error : " + e.getMessage());
                                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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

    void initNavBar()
    {
        LinearLayout navShop = getView().findViewById(R.id.cart_nav_shop);
        LinearLayout navStatus = getView().findViewById(R.id.cart_nav_status);
        LinearLayout navLogout = getView().findViewById(R.id.cart_nav_logout);
        navShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment homeFragment = new CustomerHomeFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).addToBackStack(null).commit();
            }
        });
        navStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new StatusFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
        navLogout.setOnClickListener(new View.OnClickListener() {
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
