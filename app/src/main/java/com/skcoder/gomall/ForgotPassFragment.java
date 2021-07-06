package com.skcoder.gomall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassFragment extends Fragment {
    ImageView forgotPasswordBackBtn;
    FrameLayout frameLayout;
    EditText forgotPasswordEmail;
    Button forgotPasswordBtn;
    ProgressBar forgotPasswordProgressBar;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_pass, container, false);

        forgotPasswordBackBtn = view.findViewById(R.id.forgot_password_back_btn);
        frameLayout = getActivity().findViewById(R.id.framelayout_register);
        forgotPasswordEmail = view.findViewById(R.id.forgotPasswordEmail);
        forgotPasswordBtn = view.findViewById(R.id.forgotPasswordBtn);
        forgotPasswordProgressBar = view.findViewById(R.id.forgotPasswordProgressBar);

        mAuth = FirebaseAuth.getInstance();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgotPasswordBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {
        String email = forgotPasswordEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            forgotPasswordEmail.setError("Required");
            forgotPasswordEmail.requestFocus();
        }else{
            forgotPasswordProgressBar.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Email send successfully. Check your mail.", Toast.LENGTH_SHORT).show();

                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
                                fragmentTransaction.replace(frameLayout.getId(), new SignInFragment());
                                fragmentTransaction.commit();
                            }else {
                                forgotPasswordProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setFragment() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), new SignInFragment());
        fragmentTransaction.commit();
    }
}