package com.skcoder.gomall;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    public static final int MANAGE_ADDRESS = 1;
    private TextView viewAllAddressesBtn;

    private CircleImageView profileImage, currentOrderImage;
    private TextView userName, userEmail, currentOrderStatus;

    private Dialog loadingDialog;
    private LinearLayout layoutContainer, recentOrdersContainer;

    private ImageView orderIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progressBar, P_S_progressBar, S_D_progressBar;

    private TextView yourRecentOrderTitle;

    private TextView addressName, address, pincode;

    private Button signoutBtn;

    private FloatingActionButton settingsBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ////// loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////// loading Dialog

        viewAllAddressesBtn = view.findViewById(R.id.view_all_addresses_btn);

        layoutContainer = view.findViewById(R.id.layout_container);
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        currentOrderImage = view.findViewById(R.id.current_order_image);
        currentOrderStatus = view.findViewById(R.id.tv_current_order_status);

        orderIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shipped_indicator);
        deliveredIndicator = view.findViewById(R.id.delivered_indicator);

        O_P_progressBar = view.findViewById(R.id.ordered_packed_progress);
        P_S_progressBar = view.findViewById(R.id.packed_shipped_progress);
        S_D_progressBar = view.findViewById(R.id.shipped_delivered_progress);

        yourRecentOrderTitle = view.findViewById(R.id.your_recent_orders_title);
        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);

        addressName = view.findViewById(R.id.address_fullname);
        address = view.findViewById(R.id.address_account);
        pincode = view.findViewById(R.id.address_pincode);

        signoutBtn = view.findViewById(R.id.signout_btn);

        settingsBtn = view.findViewById(R.id.settings_btn);


        layoutContainer.getChildAt(1).setVisibility(View.GONE);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                for (MyOrderItemModel orderItemModel : DBquries.myOrderItemModelList) {
                    if (!orderItemModel.isCancellationRequested()) {
                        if (!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.loading_spinner)).into(currentOrderImage);
                            currentOrderStatus.setText(orderItemModel.getOrderStatus());

                            switch (orderItemModel.getOrderStatus()) {
                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    break;
                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    O_P_progressBar.setProgress(100);

                                    break;
                                case "Shipped":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    O_P_progressBar.setProgress(100);
                                    P_S_progressBar.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    O_P_progressBar.setProgress(100);
                                    P_S_progressBar.setProgress(100);
                                    S_D_progressBar.setProgress(100);
                                    break;
                            }
                        }
                    }
                }


                int i = 0;
                for (MyOrderItemModel myOrderItemModel : DBquries.myOrderItemModelList) {
                    if (i < 4) {
                        if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.loading_spinner)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    } else {
                        break;
                    }
                }

                if (i == 0) {
                    yourRecentOrderTitle.setText("No Recent Orders.");
                }

                if (i < 3) {
                    for (int x = i; x < 4; x++) {
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }

                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBquries.addressesModelList.size() == 0) {
                            addressName.setText("No Address");
                            address.setText("-");
                            pincode.setText("-");
                        } else {
                            setAddress();
                        }
                    }
                });
                DBquries.loadAddresses(getContext(), loadingDialog, false);
            }
        });
        DBquries.loadOrders(getContext(), null, loadingDialog);


        viewAllAddressesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressIntent = new Intent(getContext(), MyAddressesActivity.class);
                myAddressIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(myAddressIntent);
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBquries.clearData();
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(getContext(), "Sign Out", Toast.LENGTH_SHORT).show();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfoIntent = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfoIntent.putExtra("name", userName.getText());
                updateUserInfoIntent.putExtra("email", userEmail.getText());
                updateUserInfoIntent.putExtra("photo", DBquries.profile);

                startActivity(updateUserInfoIntent);
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        userName.setText(DBquries.fullname);
        userEmail.setText(DBquries.email);
        if (!DBquries.profile.equals("")) {
            //if (!"".equalsIgnoreCase(DBquries.profile)){
            Glide.with(getContext()).load(DBquries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_logo)).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profile_logo);
        }


        if (!loadingDialog.isShowing()) {
            if (DBquries.addressesModelList.size() == 0) {
                addressName.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            } else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nametext, mobileNo;
        nametext = DBquries.addressesModelList.get(DBquries.selectedAddress).getName();
        mobileNo = DBquries.addressesModelList.get(DBquries.selectedAddress).getMobileNo();
        if (DBquries.addressesModelList.get(DBquries.selectedAddress).getAlternateMobileNo().equals("")) {
            addressName.setText(nametext + ", " + mobileNo);
        } else {
            addressName.setText(nametext + ", " + mobileNo + " or " + DBquries.addressesModelList.get(DBquries.selectedAddress).getAlternateMobileNo());
        }
        String faltNo = DBquries.addressesModelList.get(DBquries.selectedAddress).getFlatNo();
        String locality = DBquries.addressesModelList.get(DBquries.selectedAddress).getLocality();
        String landmark = DBquries.addressesModelList.get(DBquries.selectedAddress).getLandmark();
        String city = DBquries.addressesModelList.get(DBquries.selectedAddress).getCity();
        String state = DBquries.addressesModelList.get(DBquries.selectedAddress).getState();

        if (landmark.equals("")) {
            address.setText(faltNo + " " + locality + " " + city + " " + state);
        } else {
            address.setText(faltNo + " " + locality + " " + landmark + " " + city + " " + state);
        }

        pincode.setText(DBquries.addressesModelList.get(DBquries.selectedAddress).getPincode());
    }
}