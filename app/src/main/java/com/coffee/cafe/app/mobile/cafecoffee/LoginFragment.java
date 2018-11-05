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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Model.User;

public class LoginFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button skipButton = getView().findViewById(R.id.login_skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("test@mail.com", "Mr.A", "customer");
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment homeFragment = new ShopFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).commit();
            }
        });

        Button skipButton2 = getView().findViewById(R.id.login_skip_button2);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("test@mail.com", "Mr.A", "customer");
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment homeFragment = new ShopFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).commit();
            }
        });
        initLoginButton();
        initRegisterButton();
    }

    void initLoginButton()
    {
        Button loginButton = getView().findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = getView().findViewById(R.id.login_email);
                EditText password = getView().findViewById(R.id.login_password);
                final String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                fbAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            fbStore.collection("user").whereEqualTo("email", emailStr).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                for (QueryDocumentSnapshot document : task.getResult())
                                                {
                                                    User user = document.toObject(User.class);
                                                    if (user.getType().equals("customer"))
                                                    {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("User object", user);
                                                        Fragment homeFragment = new ShopFragment();
                                                        homeFragment.setArguments(bundle);
                                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                        ft.replace(R.id.main_view, homeFragment).commit();
                                                    }
                                                    else if (user.getType().equals("shopOwner"))
                                                    {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("User object", user);
                                                        Fragment homeFragment = new ShopFragment();
                                                        homeFragment.setArguments(bundle);
                                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                        ft.replace(R.id.main_view, homeFragment).commit();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                Log.d("cafe", "get user error : " + task.getException());
                                                Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Log.d("cafe", "login error : " + task.getException());
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException e)
                            {
                                Log.d("cafe", "error : " + task.getException());
                                Toast.makeText(getActivity(), "Invalid user", Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                Log.d("cafe", "error : " + task.getException() + " code : " + e.getErrorCode());
                                if (e.getErrorCode().equals("ERROR_INVALID_EMAIL"))
                                {
                                    Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_SHORT).show();
                                }
                                else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD"))
                                {
                                    Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (FirebaseNetworkException e)
                            {
                                Log.d("cafe", "error : " + task.getException());
                                Toast.makeText(getActivity(), "Network error please check your internet connection", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Log.d("cafe", "Error : " + task.getException());
                                Toast.makeText(getActivity(), "error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    void initRegisterButton()
    {
        Button registerButton = getView().findViewById(R.id.login_regis_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new RegisterFragment())
                        .addToBackStack(null).commit();
            }
        });
    }
}
