package com.coffee.cafe.app.mobile.cafecoffee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import Model.Shop;
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
    User user;
    Shop shop;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        shop = (Shop) bundle.getSerializable("Shop object");
        type = user.getType();
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

        initChooseProfilePictureButton();
        initProfileOpenCameraButton();
        initUpdateButton();
    }

    void initChooseProfilePictureButton()
    {
        Button chooseButton = getView().findViewById(R.id.profile_choose_picture);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("image/*");
                intent = Intent.createChooser(chooseFile, "Choose a picture");
                startActivityForResult(intent, PICK_IMAGE_PROFILE);
            }
        });
    }

    void initProfileOpenCameraButton()
    {
        Button openCameraButton = getView().findViewById(R.id.profile_camera_button);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "choose app"), OPEN_CAMERA_PROFILE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_PROFILE || requestCode == PICK_IMAGE_SHOP)
        {
            profilePictureUri = null;
            shopPictureUri = null;
            String path = "";
            if (data == null) return;
            Uri uri = data.getData();
            if (requestCode == PICK_IMAGE_PROFILE)
            {
                ImageView profile = getView().findViewById(R.id.register_selected_image);
                profile.setImageURI(uri);
            }
            else if (requestCode == PICK_IMAGE_SHOP)
            {
                ImageView shop = getView().findViewById(R.id.register_shop_picture);
                shop.setImageURI(uri);
            }
            path = GetFilePathFromDevice.getPath(getContext(), uri);
            String fileName = "";
            if (path.lastIndexOf('/') != -1)
            {
                fileName = path.substring(path.lastIndexOf('/')+1);
            }
            if (requestCode == PICK_IMAGE_PROFILE)
            {
                profilePicturePath = path;
                profilePictureName = fileName;
            }
            else if (requestCode == PICK_IMAGE_SHOP)
            {
                shopPicturePath = path;
                shopPictureName = fileName;
            }
        }
        else if (requestCode == OPEN_CAMERA_PROFILE)
        {
            if (resultCode == Activity.RESULT_CANCELED)
            {
                return;
            }
            ImageView placeholder = getView().findViewById(R.id.register_placeholder_icon_profile);
            placeholder.setVisibility(View.GONE);
            profilePictureUri = data.getData();
            String path = GetFilePathFromDevice.getPath(getContext(), profilePictureUri);
            profilePictureName = profilePictureUri.getLastPathSegment();
            ImageView profile = getView().findViewById(R.id.register_selected_image);
            //profile.setImageURI(profilePictureUri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap bmpSample = BitmapFactory.decodeFile(path, options);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 1, out);
            byte[] byteArray = out.toByteArray();

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            profile.setImageBitmap(Bitmap.createScaledBitmap(bmp, 700,
                    700, false));
        }
        else if (requestCode == OPEN_CAMERA_SHOP)
        {
            if (data == null) return;
            ImageView placeholder = getView().findViewById(R.id.register_placeholder_icon_shop);
            placeholder.setVisibility(View.GONE);
            shopPictureUri = data.getData();
            shopPictureName = shopPictureUri.getLastPathSegment();
            ImageView profile = getView().findViewById(R.id.register_shop_picture);
            //profile.setImageURI(shopPictureUri);

            String path = GetFilePathFromDevice.getPath(getContext(), shopPictureUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap bmpSample = BitmapFactory.decodeFile(path, options);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 1, out);
            byte[] byteArray = out.toByteArray();

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            profile.setImageBitmap(Bitmap.createScaledBitmap(bmp, 700,
                    700, false));
        }
    }

    void uploadProfilePicture()
    {
        Uri file;
        StorageReference fileRef;
        UploadTask uploadTask;
        if (profilePictureUri == null)
        {
            file = Uri.fromFile(new File(profilePicturePath));
            fileRef = storageRef.child(file.getLastPathSegment());
            uploadTask = fileRef.putFile(file);
        }
        else
        {
            fileRef = storageRef.child(profilePictureUri.getLastPathSegment());
            uploadTask = fileRef.putFile(profilePictureUri);
        }
        final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("cafe", "upload profile picture success");
//                Toast.makeText(getContext(), "upload success", Toast.LENGTH_SHORT).show();
                if (type.equals("shopOwner"))
                {
                    //uploadShopData();
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.d("cafe", "upload profile picture error : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void initUpdateButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.profile_progress_bar);
        Button updateButton = getView().findViewById(R.id.profile_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                // remove old user document and change all customerName in order collection
                user.setPictureName(profilePictureName);
                EditText username = getView().findViewById(R.id.profile_username);
                String usernameStr = username.getText().toString();
                user.setUsername(usernameStr);
                fbStore.collection("user").document(usernameStr).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadProfilePicture();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("cafe", "add user in firestore error : " + e.getMessage());
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
