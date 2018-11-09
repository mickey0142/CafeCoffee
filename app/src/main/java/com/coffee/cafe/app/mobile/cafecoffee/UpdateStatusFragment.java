package com.coffee.cafe.app.mobile.cafecoffee;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Adapter.BeverageAdapter;
import Model.Order;
import Model.Shop;
import Model.User;

public class UpdateStatusFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Shop shop;
    Order order;
    String status;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        order = (Order) bundle.getSerializable("Order object");
        shop = (Shop) bundle.getSerializable("Shop object");
        status = order.getStatus();
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null)
        {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_update_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initText();
        initBeverageList();
        initStatusButton();
        initUpdateButton();
    }

    void initText()
    {
        TextView customerName = getView().findViewById(R.id.update_status_customer_name);
        TextView status = getView().findViewById(R.id.update_status_status);
        TextView orderTime = getView().findViewById(R.id.update_status_order_time);
        TextView sumPrice = getView().findViewById(R.id.update_status_sum_price);
        customerName.setText("Customer Name : " + order.getCustomerName());
        status.setText("Status : " + order.getStatus());
        orderTime.setText("Order Time : " + order.orderTimeReverse());
        sumPrice.setText("Total Price  : " + order.getSumPrice());
    }

    void initBeverageList()
    {
        ListView beverageList = getView().findViewById(R.id.update_status_beverage_list);
        BeverageAdapter adapter = new BeverageAdapter(getActivity(), R.layout.fragment_beverage_item, order.getBeverages());
        beverageList.setAdapter(adapter);
    }

    void initStatusButton()
    {
        final Button inQueueButton = getView().findViewById(R.id.update_status_in_queue_button);
        final Button inProgressButton = getView().findViewById(R.id.update_status_in_progress_button);
        final Button doneButton = getView().findViewById(R.id.update_status_done_button);
        final Button paidButton = getView().findViewById(R.id.update_status_paid_button);
        inQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "in queue";
                setButtonColor();
            }
        });
        inProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "in progress";
                setButtonColor();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "done";
                setButtonColor();
            }
        });
        paidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "paid";
                setButtonColor();
            }
        });
    }

    void setButtonColor()
    {
        final Button inQueueButton = getView().findViewById(R.id.update_status_in_queue_button);
        final Button inProgressButton = getView().findViewById(R.id.update_status_in_progress_button);
        final Button doneButton = getView().findViewById(R.id.update_status_done_button);
        final Button paidButton = getView().findViewById(R.id.update_status_paid_button);
        if (status.equals("in queue"))
        {
            inQueueButton.setBackgroundColor(Color.parseColor("FFFF00"));
            inProgressButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            doneButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            paidButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        else if (status.equals("in progress"))
        {
            inQueueButton.setBackgroundColor(Color.parseColor("DDDDDD"));
            inProgressButton.setBackgroundColor(Color.parseColor("#FFFF00"));
            doneButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            paidButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        else if (status.equals("done"))
        {
            inQueueButton.setBackgroundColor(Color.parseColor("DDDDDD"));
            inProgressButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            doneButton.setBackgroundColor(Color.parseColor("#FFFF00"));
            paidButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        else if (status.equals("paid"))
        {
            inQueueButton.setBackgroundColor(Color.parseColor("DDDDDD"));
            inProgressButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            doneButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            paidButton.setBackgroundColor(Color.parseColor("#FFFF00"));
        }
    }

    void initUpdateButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.update_status_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        fbStore.collection("order").whereEqualTo("orderTime", "").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d("cafe", "document id : " + document.getId());
                                if (status.equals("paid"))
                                {
                                    fbStore.collection("order").document(document.getId()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                                                    Log.d("cafe", "delete success");
                                                    getFragmentManager().popBackStack();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "update error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("cafe", "delete error : " + e.getMessage());
                                        }
                                    });
                                }
                                else
                                {
                                    order.setStatus(status);
                                    fbStore.collection("order").document(document.getId()).set(order)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                                                    Log.d("cafe", "update success");
                                                    getFragmentManager().popBackStack();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "update error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("cafe", "update error : " + e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Log.d("cafe", "get order before delete error : " + task.getException());
                            Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
