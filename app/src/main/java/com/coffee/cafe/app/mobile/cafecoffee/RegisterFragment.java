package com.coffee.cafe.app.mobile.cafecoffee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import Model.Shop;
import Model.User;

public class RegisterFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTypeButton();
        initRegisterButton();
        initBackButton();
        initChooseProfilePictureButton();
        initChooseShopPictureButton();
        initProfileOpenCameraButton();
        initShopOpenCameraButton();
    }

    void initTypeButton()
    {
        final Button customerButton = getView().findViewById(R.id.register_customer_button);
        final Button shopOwnerButton = getView().findViewById(R.id.register_shop_owner_button);
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "customer";
                customerButton.setBackgroundResource(R.drawable.border_button);
                shopOwnerButton.setBackgroundResource(R.drawable.actived_button);
                LinearLayout shopOwnerLayout = getView().findViewById(R.id.register_shop_owner_linear_layout);
                shopOwnerLayout.setVisibility(View.GONE);
            }
        });
        shopOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "shopOwner";
                customerButton.setBackgroundResource(R.drawable.actived_button);
                shopOwnerButton.setBackgroundResource(R.drawable.border_button);
                LinearLayout shopOwnerLayout = getView().findViewById(R.id.register_shop_owner_linear_layout);
                shopOwnerLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    void initRegisterButton()
    {
        Button registerButton = getView().findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = getView().findViewById(R.id.register_email);
                EditText username = getView().findViewById(R.id.register_username);
                EditText password = getView().findViewById(R.id.register_password);
                EditText rePassword = getView().findViewById(R.id.register_re_password);
                String emailStr = email.getText().toString();
                final String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                String rePasswordStr = rePassword.getText().toString();
                if (emailStr.isEmpty() || usernameStr.isEmpty() || passwordStr.isEmpty())
                {
                    Toast.makeText(getContext(), "some field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (profilePictureName == null && profilePictureUri == null)
                {
                    Toast.makeText(getContext(), "please choose profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.equals("shopOwner") && shopPictureName == null && shopPictureUri == null)
                {
                    Toast.makeText(getContext(), "please choose shop picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordStr.equals(rePasswordStr))
                {
                    Toast.makeText(getContext(), "password and re password is not equal", Toast.LENGTH_SHORT).show();
                }
                final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                final User user = new User();
                user.setEmail(emailStr);
                user.setUsername(usernameStr);
                user.setType(type);
                user.setPictureName(profilePictureName);
                fbAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                final FirebaseUser fbUser = authResult.getUser();
                                sendVerifyEmail(fbUser);
                                fbStore.collection("user").document(usernameStr).set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // upload everything needed here
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
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("cafe", "create user error : " + e.getMessage());
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void sendVerifyEmail(FirebaseUser user){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("cafe", "send email success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("cafe", "send email fail : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.register_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    void initChooseProfilePictureButton()
    {
        Button chooseButton = getView().findViewById(R.id.register_choose_picture);
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

    void initChooseShopPictureButton()
    {
        Button chooseButton = getView().findViewById(R.id.register_shop_choose_picture);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("image/*");
                intent = Intent.createChooser(chooseFile, "Choose a picture");
                startActivityForResult(intent, PICK_IMAGE_SHOP);
            }
        });
    }

    void initProfileOpenCameraButton()
    {
        Button openCameraButton = getView().findViewById(R.id.register_camera_button);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "choose app"), OPEN_CAMERA_PROFILE);
            }
        });
    }

    void initShopOpenCameraButton()
    {
        Button openCameraButton = getView().findViewById(R.id.register_shop_camera_button);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "choose app"), OPEN_CAMERA_SHOP);
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
                ImageView placeholder = getView().findViewById(R.id.register_placeholder_icon_profile);
                placeholder.setVisibility(View.GONE);
            }
            else if (requestCode == PICK_IMAGE_SHOP)
            {
                ImageView shop = getView().findViewById(R.id.register_shop_picture);
                shop.setImageURI(uri);
                ImageView placeholder = getView().findViewById(R.id.register_placeholder_icon_shop);
                placeholder.setVisibility(View.GONE);
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
                    uploadShopData();
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

    void uploadShopData()
    {
        EditText shopName = getView().findViewById(R.id.register_shop_name);
        EditText location = getView().findViewById(R.id.register_location);
        final String shopNameStr = shopName.getText().toString();
        String locationStr = location.getText().toString();
        EditText username = getView().findViewById(R.id.register_username);
        String usernameStr = username.getText().toString();
        final Shop shop = new Shop();
        shop.setShopName(shopNameStr);
        shop.setLocation(locationStr);
        shop.setOwner(usernameStr);
        shop.setPictureName(shopPictureName);
        shop.setDefaultMenuPrice();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
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
                                Log.d("cafe", "location is null in register");
                                Toast.makeText(getContext(), "set shop location fail (error code = 1)", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("cafe", "get last location fail : " + e.getMessage());
                    Toast.makeText(getContext(), "set shop location fail (error code = 2)", Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
                    fbStore.collection("shop").document(shopNameStr).set(shop)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("cafe", "add shop success");
                                    //Toast.makeText(getContext(), "add shop success", Toast.LENGTH_SHORT).show();
                                    uploadShopPicture();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("cafe", "add shop error : " + e.getMessage());
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        catch (SecurityException e)
        {
            Log.d("cafe", "location permission not granted : " +e.getMessage());
            Toast.makeText(getContext(), "set shop location fail (error code = 3)" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch (NullPointerException e)
        {
            Log.d("cafe", "catch NullPointerException : " + e.getMessage());
            Toast.makeText(getContext(), "set shop location fail (error code = 4)", Toast.LENGTH_LONG).show();
        }
    }

    void uploadShopPicture()
    {
        Uri file;
        StorageReference fileRef;
        UploadTask uploadTask;
        if (shopPictureUri == null)
        {
            file = Uri.fromFile(new File(shopPicturePath));
            fileRef = storageRef.child(file.getLastPathSegment());
            uploadTask = fileRef.putFile(file);
        }
        else
        {
            fileRef = storageRef.child(shopPictureUri.getLastPathSegment());
            uploadTask = fileRef.putFile(shopPictureUri);
        }
        final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("cafe", "upload shop picture success + register success");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.GONE);Log.d("cafe", "upload shop picture error : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
