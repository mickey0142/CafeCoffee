package com.coffee.cafe.app.mobile.cafecoffee;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.User;

public class RegisterFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initRegisterButton();
        initBackButton();
    }

    void initRegisterButton()
    {
        RadioGroup radioGroup = getView().findViewById(R.id.register_type_group);
        int id = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = getView().findViewById(id);
        String type = radioButton.getText().toString();
        EditText email = getView().findViewById(R.id.register_email);
        EditText username = getView().findViewById(R.id.register_username);
        EditText password = getView().findViewById(R.id.register_password);
        String emailStr = email.getText().toString();
        final String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        if (emailStr.isEmpty() || usernameStr.isEmpty() || passwordStr.isEmpty())
        {
            Toast.makeText(getContext(), "some field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final User user = new User();
        user.setEmail(emailStr);
        user.setUsername(usernameStr);
        user.setType(type);
        fbAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser fbUser = authResult.getUser();
                        fbStore.collection("user").document(usernameStr).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendVerifyEmail(fbUser);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("cafe", "add user in firestore error : " + e.getMessage());
                                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("cafe", "create user error : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerifyEmail(FirebaseUser user){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("cafe", "send email success");
                Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
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
        Button backButton = getView().findViewById(R.id.register_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }
}
