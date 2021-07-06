package com.skcoder.gomall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.skcoder.gomall.RegisterActivity.SET_SIGNUP_FRAGMENT;

public class HomeActivity extends AppCompatActivity {

    NavigationView nav;
    Toolbar toolbar;
    public static DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FrameLayout frameLayout;

    FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDER_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int REWARD_FRAGMENT = 4;
    private static final int ACCOUNT_FRAGMENT = 5;

    public static Activity homeActivity;

    public static Boolean showCart = false;
    public static boolean resetHomeActivity = false;

    private ImageView actionBarLogo;

    private Window window;
    private Dialog signinDialog;
    private TextView badgecount;

    private CircleImageView profileImage;
    private TextView fullname;
    private TextView email;
    private ImageView addProfileIcon;


    private int currentFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.ctoolbaar);
        actionBarLogo = findViewById(R.id.actionBar_logo);
        toolbar.setTitleTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        setSupportActionBar(toolbar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        mAuth = FirebaseAuth.getInstance();

        frameLayout = findViewById(R.id.home_frame_layout);

        nav = findViewById(R.id.nav_menu);
        drawerLayout = findViewById(R.id.drawer);

        nav.getMenu().getItem(0).setChecked(true);

        profileImage = nav.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullname = nav.getHeaderView(0).findViewById(R.id.main_full_name);
        email = nav.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon = nav.getHeaderView(0).findViewById(R.id.add_profile_icon);

        if (showCart) {
            homeActivity = this;
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            gotoFragment("My Cart", new CartFragment(), -2);
        } else {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(frameLayout.getId(), new HomeFragment());
            fragmentTransaction.commit();
        }

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawer = findViewById(R.id.drawer);
                if (currentUser != null) {
                    switch (item.getItemId()) {
                        case R.id.menu_home:
                            actionBarLogo.setVisibility(View.VISIBLE);
                            invalidateOptionsMenu();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            setFragment(new HomeFragment(), HOME_FRAGMENT);
                            break;
                        case R.id.menu_orders:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            gotoFragment("My Orders", new OrdersFragment(), ORDER_FRAGMENT);
                            break;
                        case R.id.menu_rewards:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            gotoFragment("My Rewards", new RewardsFragment(), REWARD_FRAGMENT);
                            break;
                        case R.id.menu_cart:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            gotoFragment("My Cart", new CartFragment(), CART_FRAGMENT);
                            break;
                        case R.id.menu_wishlist:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            gotoFragment("My wishlist", new WishlistFragment(), WISHLIST_FRAGMENT);
                            break;
                        case R.id.menu_account:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            gotoFragment("My Account", new AccountFragment(), ACCOUNT_FRAGMENT);
                            break;
                        case R.id.menu_signout:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            mAuth.signOut();
                            DBquries.clearData();
                            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(HomeActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                    signinDialog.show();
                    return false;
                }
            }
        });

        signinDialog = new Dialog(HomeActivity.this);
        signinDialog.setContentView(R.layout.sign_in_dialog);
        signinDialog.setCancelable(true);

        signinDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSigninBtn = signinDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signinDialog.findViewById(R.id.sign_up_btn);

        Intent registerIntent = new Intent(HomeActivity.this, RegisterActivity.class);

        dialogSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableContinueBtn = true;
                signinDialog.dismiss();
                SET_SIGNUP_FRAGMENT = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinDialog.dismiss();
                SET_SIGNUP_FRAGMENT = true;
                startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            nav.getMenu().getItem(nav.getMenu().size() - 1).setEnabled(false);
        } else {

            if (DBquries.email == null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBquries.fullname = task.getResult().getString("fullname");
                            DBquries.email = task.getResult().getString("email");
                            DBquries.profile = task.getResult().getString("profile");

                            fullname.setText(DBquries.fullname);
                            email.setText(DBquries.email);

                            if (DBquries.profile.equals("")) {

                                addProfileIcon.setVisibility(View.VISIBLE);
                            } else {
                                addProfileIcon.setVisibility(View.INVISIBLE);
                                Glide.with(HomeActivity.this).load(DBquries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_logo)).into(profileImage);
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Error :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                fullname.setText(DBquries.fullname);
                email.setText(DBquries.email);
                if (DBquries.profile.equals("")) {
                    profileImage.setImageResource(R.drawable.profile_logo);
                    addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(HomeActivity.this).load(DBquries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_logo)).into(profileImage);
                }
            }
            nav.getMenu().getItem(nav.getMenu().size() - 1).setEnabled(true);
        }

        if (resetHomeActivity) {
            resetHomeActivity = false;
            actionBarLogo.setVisibility(View.VISIBLE);
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            nav.getMenu().getItem(0).setChecked(true);
        }

        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBquries.checkNotifications(true, null);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                super.onBackPressed();
            } else {
                if (showCart) {
                    homeActivity = null;
                    showCart = false;
                    finish();
                } else {
                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    nav.getMenu().getItem(0).setChecked(true);
                }

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options_menu, menu);
            MenuItem cartItem = menu.findItem(R.id.cart_menu);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_cart_white);
            badgecount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null) {
                if (DBquries.cartList.size() == 0) {
                    DBquries.loadCartList(HomeActivity.this, new Dialog(HomeActivity.this), false, badgecount, new TextView(HomeActivity.this));
                } else {
                    badgecount.setVisibility(View.VISIBLE);
                    if (DBquries.cartList.size() < 99) {
                        badgecount.setText(String.valueOf(DBquries.cartList.size()));
                    } else {
                        badgecount.setText("99");
                    }
                }
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signinDialog.show();
                    } else {
                        gotoFragment("My Cart", new CartFragment(), CART_FRAGMENT);
                    }
                }
            });

            MenuItem notifyItem = menu.findItem(R.id.notification_menu);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.ic_notifications);
            TextView notifycount = notifyItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null){
                DBquries.checkNotifications(false, notifycount);

            }
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(HomeActivity.this, NotificationActivity.class);
                    startActivity(intent2);
                }
            });


        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.notification_menu:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.cart_menu:
                if (currentUser == null) {
                    signinDialog.show();
                } else {
                    gotoFragment("My Cart", new CartFragment(), CART_FRAGMENT);
                }
                break;
            case android.R.id.home:
                if (showCart) {
                    homeActivity = null;
                    showCart = false;
                    finish();
                    return true;
                }
        }

        return true;
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT) {
            nav.getMenu().getItem(3).setChecked(true);
        }
    }

    private void setFragment(Fragment fm, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            if (fragmentNo == REWARD_FRAGMENT) {
                window.setStatusBarColor(Color.parseColor("#3006BA"));
                toolbar.setBackgroundColor(Color.parseColor("#3006BA"));
            } else {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fm);
            fragmentTransaction.commit();
        }
    }

}