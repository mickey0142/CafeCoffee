package com.coffee.cafe.app.mobile.cafecoffee;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Model.User;

public class ProfileFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    String type = "customer";
    final int OPEN_CAMERA_PROFILE = 1;
    final int OPEN_CAMERA_SHOP = 2;
    final int PICK_IMAGE_PROFILE = 3;
    final int PICK_IMAGE_SHOP = 4;
    String profilePicturePath;
    String shopPicturePath;
    String profilePictureName;
    String shopPictureName;
    Uri profilePictureUri;
    Uri shopPictureUri;
    FusedLocationProviderClient fusedLocationClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null)
        {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initConfirmButton();
    }
//    void initConfirmButton(){
//        final ProgressBar progressBar = getView().findViewById(R.id.profile_progress_bar);
//        Button confirmButton = getView().findViewById(R.id.profile_confirm_button);
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//
//           }
//        });
//    }
}
