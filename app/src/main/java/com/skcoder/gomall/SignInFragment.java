package com.skcoder.gomall;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInFragment extends Fragment {
    private static final int RC_SIGN_IN = 101;
    TextView dontHaveAccount, forgotPasswordSignin, continueWithoutSigninTv;
    FrameLayout frameLayout;
    Button signinButton;
    LinearLayout googleSigninImageView;
    LoginButton facebookSigninImageView;

    public static boolean disableContinueBtn = false;

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    EditText signinEmail, signinPassword;
    ProgressBar progressBarSignin;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        dontHaveAccount = view.findViewById(R.id.dont_have_account);
        frameLayout = getActivity().findViewById(R.id.framelayout_register);
        signinButton = view.findViewById(R.id.signin_button);
        forgotPasswordSignin = view.findViewById(R.id.forgot_password_singin);
        continueWithoutSigninTv = view.findViewById(R.id.continue_signin_btn_tv);

        googleSigninImageView = view.findViewById(R.id.google_signin_iv);
        facebookSigninImageView = view.findViewById(R.id.facebook_signin_iv);
        facebookSigninImageView.setPermissions("email", "public_profile");

        signinEmail = view.findViewById(R.id.signinEmail);
        signinPassword = view.findViewById(R.id.signinPassword);
        progressBarSignin = view.findViewById(R.id.progressBarSingin);

        if (disableContinueBtn){
            continueWithoutSigninTv.setVisibility(View.GONE);
        }else {
            continueWithoutSigninTv.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SiginUpFragment());
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        forgotPasswordSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ForgotPassFragment());
            }
        });

        continueWithoutSigninTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        googleSigninImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSignin.setVisibility(View.VISIBLE);
                signIn();
            }
        });
        facebookSigninImageView.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBarSignin.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Login success.", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressBarSignin.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Facebook Login Cancel.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                progressBarSignin.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error :"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "You SignIn with "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            progressBarSignin.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                progressBarSignin.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "You SignIn with "+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            progressBarSignin.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(), "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }

    private void checkValidation() {
        String signinEm, signinPass;
        signinEm = signinEmail.getText().toString().trim();
        signinPass = signinPassword.getText().toString().trim();

        if (TextUtils.isEmpty(signinEm)){
            signinEmail.setError("Required!");
            signinEmail.requestFocus();
        }else if (TextUtils.isEmpty(signinPass)){
            signinPassword.setError("Required!");
            signinPassword.requestFocus();
        }else if (signinPass.length() <=8 ){
            Toast.makeText(getContext(), "Password must be greater than 8", Toast.LENGTH_SHORT).show();
        }else {
            progressBarSignin.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(signinEm, signinPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if (disableContinueBtn){
                                disableContinueBtn = false;
                                }else {
                                    startActivity(new Intent(getContext(), HomeActivity.class));
                                }
                                getActivity().finish();
                            }else{
                                progressBarSignin.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Error :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarSignin.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}