package com.coffee.cafe.app.mobile.cafecoffee;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Adapter.BeverageAdapter;
import Model.Beverage;
import Model.Order;
import Model.Shop;
import Model.User;

public class UpdateStatusFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
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

        checkAuthen();
        initCustomerPicture();
        initText();
        initBeverageList();
        initStatusButton();
        initUpdateButton();
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

    void initCustomerPicture()
    {
        fbStore.collection("user").whereEqualTo("username", order.getCustomerName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot doc : task.getResult())
                            {
                                String pictureName = doc.getString("pictureName");
                                ImageView customerPicture = getView().findViewById(R.id.update_status_customer_picture);
                                StorageReference imageRef = fbStorage.getReferenceFromUrl("gs://cafe-coffee-576ed.appspot.com")
                                        .child(pictureName);
                                GlideApp.with(getContext()).load(imageRef).into(customerPicture);
                            }
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.d("cafe", "get user error : " + task.getException());
                        }
                    }
                });
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
        sumPrice.setText("Total Price  : " + order.getSumPrice() + " ฿");
    }

    void initBeverageList()
    {
        ListView beverageList = getView().findViewById(R.id.update_status_beverage_list);
        BeverageAdapter adapter = new BeverageAdapter(getActivity(), R.layout.fragment_beverage_item, order.getBeverages());
        beverageList.setAdapter(adapter);
    }

    void initStatusButton()
    {
        setButtonColor();
//        final Button inQueueButton = getView().findViewById(R.id.update_status_in_queue_button);
        final Button inProgressButton = getView().findViewById(R.id.update_status_in_progress_button);
        final Button doneButton = getView().findViewById(R.id.update_status_done_button);
        final Button paidButton = getView().findViewById(R.id.update_status_paid_button);
        final CheckBox checkBox = getView().findViewById(R.id.update_status_save_log);
//        inQueueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                status = "in queue";
//                checkBox.setVisibility(View.GONE);
//                checkBox.setChecked(false);
//                setButtonColor();
//            }
//        });
        inProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "in progress";
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
                setButtonColor();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "done";
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
                setButtonColor();
            }
        });
        paidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "paid";
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                setButtonColor();
            }
        });
    }

    void setButtonColor()
    {
//        final Button inQueueButton = getView().findViewById(R.id.update_status_in_queue_button);
        final Button inProgressButton = getView().findViewById(R.id.update_status_in_progress_button);
        final Button doneButton = getView().findViewById(R.id.update_status_done_button);
        final Button paidButton = getView().findViewById(R.id.update_status_paid_button);
//        if (status.equals("in queue"))
//        {
//            inQueueButton.setBackgroundColor(Color.parseColor("#FFFF00"));
//            inProgressButton.setBackgroundResource(R.drawable.border_button);
//            doneButton.setBackgroundResource(R.drawable.actived_button);
//            paidButton.setBackgroundResource(R.drawable.actived_button);
//        }
        if (status.equals("in progress"))
        {
//            inQueueButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            inProgressButton.setBackgroundResource(R.drawable.border_button);
            doneButton.setBackgroundResource(R.drawable.actived_button);
            paidButton.setBackgroundResource(R.drawable.actived_button);
        }
        else if (status.equals("done"))
        {
//            inQueueButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            inProgressButton.setBackgroundResource(R.drawable.actived_button);
            doneButton.setBackgroundResource(R.drawable.border_button);
            paidButton.setBackgroundResource(R.drawable.actived_button);
        }
        else if (status.equals("paid"))
        {
//            inQueueButton.setBackgroundColor(Color.parseColor("#DDDDDD"));
            inProgressButton.setBackgroundResource(R.drawable.actived_button);
            doneButton.setBackgroundResource(R.drawable.actived_button);
            paidButton.setBackgroundResource(R.drawable.border_button);
        }
    }

    void initUpdateButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.update_status_progress_bar);
        Button updateButton = getView().findViewById(R.id.update_status_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                fbStore.collection("order").whereEqualTo("orderTime", order.getOrderTime()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    Log.d("cafe", "get data from order success + " + task.getResult().isEmpty());
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        Log.d("cafe", "document id : " + document.getId());
                                        if (status.equals("paid"))
                                        {
                                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                            {
                                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                            }
                                            Order order = document.toObject(Order.class);
                                            saveOrderInTextFile(order);
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
        });
    }

    void saveOrderInTextFile(Order order)
    {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Cafe Coffee");
            if (!root.exists()) {
                root.mkdirs();
            }
            File textFile = new File(root, "saleLog" + shop.getShopName() + ".txt");
            FileWriter writer = new FileWriter(textFile, true);
            writer.append("Customer Name : " + order.getCustomerName() + "\n");
            writer.append("Order Time : " + order.getOrderTime() + "\n");
            writer.append("Beverage List :\n" );
            ArrayList<Beverage> beverages = order.getBeverages();
            for (int i = 0; i < beverages.size(); i++)
            {
                writer.append(beverages.get(i).getType() + " " + beverages.get(i).getName() + " " +
                        beverages.get(i).getSize() + " : " + beverages.get(i).getPrice() + " X " +
                        beverages.get(i).getAmount() + " = " + beverages.get(i).getPrice("total")
                        + "\n");
            }
            writer.append("Total price : " + order.getSumPrice() + "\n\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("cafe", "write text file error : " + e.getMessage());
            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void initNavBar()
    {
        LinearLayout orderButton = getView().findViewById(R.id.update_status_order_button);
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

        LinearLayout editPriceButton = getView().findViewById(R.id.update_status_edit_price_button);
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

        LinearLayout logoutButton = getView().findViewById(R.id.update_status_logout_button);
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
