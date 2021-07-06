package com.skcoder.gomall;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.skcoder.gomall.SignInFragment.disableContinueBtn;

public class SiginUpFragment extends Fragment {
    TextView alreadyHaveAccount;
    FrameLayout frameLayout;
    Button signupButton;
    ImageView signupBackButton;

    EditText signupEmail, signupName, signupPassword, signupConfirmPassword;

    ProgressBar signupProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sigin_up, container, false);

        alreadyHaveAccount = view.findViewById(R.id.already_have_account);
        frameLayout = getActivity().findViewById(R.id.framelayout_register);
        signupBackButton = view.findViewById(R.id.signup_back_button);
        signupButton = view.findViewById(R.id.signup_button);

        signupProgressBar = view.findViewById(R.id.progressBarSignup);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signupEmail = view.findViewById(R.id.signupEmail);
        signupName = view.findViewById(R.id.signupName);
        signupPassword = view.findViewById(R.id.signupPassword);
        signupConfirmPassword = view.findViewById(R.id.signupConfirmPassword);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        signupBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
                fragmentTransaction.replace(frameLayout.getId(), new SignInFragment());
                fragmentTransaction.commit();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {
        String Name, Email, Password, ConfirmPassword;
        Name = signupName.getText().toString().trim();
        Email = signupEmail.getText().toString().trim();
        Password = signupPassword.getText().toString().trim();
        ConfirmPassword = signupConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(Email)) {
            signupEmail.setError("Required!");
            signupEmail.requestFocus();
        } else if (TextUtils.isEmpty(Name)) {
            signupName.setError("Required!");
            signupName.requestFocus();
        } else if (TextUtils.isEmpty(Password)) {
            signupPassword.setError("Required!");
            signupPassword.requestFocus();
        } else if (TextUtils.isEmpty(ConfirmPassword)) {
            signupConfirmPassword.setError("Required!");
            signupConfirmPassword.requestFocus();
        } else if (Password.length() <= 8) {
            signupPassword.setError("Password Length must be grater than 8");
            signupConfirmPassword.setError("Password Length must be grater than 8");
            signupPassword.requestFocus();
        } else if (!Password.equals(ConfirmPassword)) {
            Toast.makeText(getContext(), "Password is not match", Toast.LENGTH_SHORT).show();
        } else {
            signupProgressBar.setVisibility(View.VISIBLE);
            sendData(Email, Password);
        }
    }

    private void sendData(String Email, String Pass) {
        mAuth.createUserWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullname", signupName.getText().toString());
                            userData.put("email", signupEmail.getText().toString());
                            userData.put("profile", "");

                            firebaseFirestore.collection("USERS").document(mAuth.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA");
                                                //// MAPS
                                                Map<String, Object> wishlistMap = new HashMap<>();
                                                wishlistMap.put("list_size", (long) 0);

                                                Map<String, Object> ratingsMap = new HashMap<>();
                                                ratingsMap.put("list_size", (long) 0);

                                                Map<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("list_size", (long) 0);

                                                Map<String, Object> myAddressesMap = new HashMap<>();
                                                myAddressesMap.put("list_size", (long) 0);

                                                Map<String, Object> notificationsMap = new HashMap<>();
                                                notificationsMap.put("list_size", (long) 0);
                                                //// MAPS

                                                List<String> documentNames = new ArrayList<>();
                                                documentNames.add("MY_WISHLIST");
                                                documentNames.add("MY_RATINGS");
                                                documentNames.add("MY_CART");
                                                documentNames.add("MY_ADDRESSES");
                                                documentNames.add("MY_NOTIFICATIONS");

                                                List<Map<String, Object>> documentFields = new ArrayList<>();
                                                documentFields.add(wishlistMap);
                                                documentFields.add(ratingsMap);
                                                documentFields.add(cartMap);
                                                documentFields.add(myAddressesMap);
                                                documentFields.add(notificationsMap);

                                                for (int x =0 ; x< documentNames.size(); x++){
                                                    int finalX = x;
                                                    userDataReference.document(documentNames.get(x))
                                                            .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){

                                                                if (finalX == documentNames.size() - 1){
                                                                    startActivity(new Intent(getContext(), HomeActivity.class));
                                                                    disableContinueBtn = false;
                                                                    getActivity().finish();
                                                                }

                                                            }else {
                                                                signupProgressBar.setVisibility(View.GONE);
                                                                Toast.makeText(getContext(), "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }else {
                                                signupProgressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            signupProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signupProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error :" +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFragment(Fragment fm) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fm);
        fragmentTransaction.commit();
    }
}