package com.skcoder.gomall;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UpdatePasswordFragment extends Fragment {

    private EditText oldPassword, newPassword, confirmNewPassword;
    private Button updateBtn;
    private Dialog loadingDialog;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        ////// loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////// loading Dialog

        email = getArguments().getString("Email");

        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);
        updateBtn = view.findViewById(R.id.update_password_btn);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        return view;
    }

    private void checkValidation() {

        String oldPass = oldPassword.getText().toString();
        String newPass = newPassword.getText().toString();
        String confirmNewPass = confirmNewPassword.getText().toString();

        if (!TextUtils.isEmpty(oldPass)){
            if (!TextUtils.isEmpty(newPass)){
                if (!TextUtils.isEmpty(confirmNewPass)){
                    if (newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                        loadingDialog.show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, oldPassword.getText().toString());
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                oldPassword.setText(null);
                                                                newPassword.setText(null);
                                                                confirmNewPassword.setText(null);
                                                                getActivity().finish();
                                                                Toast.makeText(getContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                Toast.makeText(getContext(), "Error :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        } else {
                                            loadingDialog.dismiss();
                                            Toast.makeText(getContext(), "Error :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }else {
                        Toast.makeText(getContext(), "Password doesn't matched !", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    confirmNewPassword.requestFocus();
                    confirmNewPassword.setError("Required!");
                }
            }else {
                newPassword.requestFocus();
                newPassword.setError("Required!");
            }
        }else {
            oldPassword.requestFocus();
            oldPassword.setError("Required!");
        }
    }
}