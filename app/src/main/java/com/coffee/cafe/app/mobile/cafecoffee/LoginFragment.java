package com.coffee.cafe.app.mobile.cafecoffee;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import Model.Shop;
import Model.User;

public class LoginFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null)
        {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        Button skipButton = getView().findViewById(R.id.login_skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("scholarsbright15@gmail.com", "mickey", "customer");
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment homeFragment = new CustomerHomeFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).commit();
            }
        });
        initEnterPressed();
        initLoginButton();
        initRegisterButton();
        clearBackStack();
    }

    void initEnterPressed()
    {
        EditText password = getView().findViewById(R.id.login_password);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.d("cafe","Enter pressed");
                    Button loginButton = getView().findViewById(R.id.login_login_button);
                    loginButton.performClick();
                }
                return false;
            }
        });
    }

    void initLoginButton()
    {
        Button loginButton = getView().findViewById(R.id.login_login_button);
        final ProgressBar progressBar = getView().findViewById(R.id.login_progress_bar);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                EditText email = getView().findViewById(R.id.login_email);
                EditText password = getView().findViewById(R.id.login_password);
                final String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                if (emailStr.isEmpty() || passwordStr.isEmpty())
                {
                    Toast.makeText(getContext(), "some field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
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
                                                    final User user = document.toObject(User.class);
                                                    if (user.getType().equals("customer"))
                                                    {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("User object", user);
                                                        Fragment homeFragment = new CustomerHomeFragment();
                                                        homeFragment.setArguments(bundle);
                                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                        ft.replace(R.id.main_view, homeFragment).commit();
                                                    }
                                                    else if (user.getType().equals("shopOwner"))
                                                    {
                                                        fbStore.collection("shop").whereEqualTo("owner", user.getUsername()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            for (QueryDocumentSnapshot document : task.getResult())
                                                                            {
                                                                                Shop shop = document.toObject(Shop.class);
                                                                                Bundle bundle = new Bundle();
                                                                                bundle.putSerializable("User object", user);
                                                                                bundle.putSerializable("Shop object", shop);
                                                                                Fragment homeFragment = new ShopOwnerHomeFragment();
                                                                                homeFragment.setArguments(bundle);
                                                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                                                ft.replace(R.id.main_view, homeFragment).commit();
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                Log.d("cafe", "get user error : " + task.getException());
                                                Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
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

    void clearBackStack()
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
