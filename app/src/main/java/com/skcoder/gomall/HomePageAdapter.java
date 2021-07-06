package com.skcoder.gomall;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends RecyclerView.Adapter {
    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastposition = -1;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;

            case 1:
                return HomePageModel.STRIP_BANNER_IMAGE;

            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;

            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {

            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);

            case HomePageModel.STRIP_BANNER_IMAGE:
                View stripBannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripBannerViewHolder(stripBannerView);

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductScrollViewHolder(horizontalProductView);

            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gride_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SlideModel> list = homePageModelList.get(position).getImageSliderList();
                ((BannerSliderViewHolder) holder).setImageSliderImage(list);
                break;

            case HomePageModel.STRIP_BANNER_IMAGE:
                String resource = homePageModelList.get(position).getResource();
                ((StripBannerViewHolder) holder).setStripAd(resource);
                break;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String bgcolor = homePageModelList.get(position).getBackgroundColor();
                String title = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductScrollViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, title, bgcolor, viewAllProductList);
                break;

            case HomePageModel.GRID_PRODUCT_VIEW:
                String glBackgroundColor = homePageModelList.get(position).getBackgroundColor();
                String gridTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductScrollModelList, gridTitle, glBackgroundColor);
                break;

            default:
                return;
        }

        if (lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }


    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        ImageSlider bannerSlider;
        final List<SlideModel> bannerSliderImageList = new ArrayList<>();

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSlider = (ImageSlider) itemView.findViewById(R.id.image_slider);
        }

        private void setImageSliderImage(List<SlideModel> bannerSliderImageList1) {
            bannerSlider.setImageList(bannerSliderImageList1, ScaleTypes.FIT);
        }
    }

    public class StripBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView stripImageView;

        public StripBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripImageView = itemView.findViewById(R.id.strip_ad_image);
        }

        private void setStripAd(String resouce) {
            Glide.with(itemView.getContext()).load(resouce).apply(new RequestOptions().placeholder(R.drawable.loading_spinner)).into(stripImageView);
        }
    }

    public class HorizontalProductScrollViewHolder extends RecyclerView.ViewHolder {

        private TextView horizontalLayoutTitle;
        private Button horizontalViewAllBtn;
        private RecyclerView horizontalScrollRecyclerView;
        private ConstraintLayout container;

        public HorizontalProductScrollViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalLayoutTitle = itemView.findViewById(R.id.DealsTitle);
            horizontalViewAllBtn = itemView.findViewById(R.id.horizontal_view_all_btn);
            horizontalScrollRecyclerView = itemView.findViewById(R.id.horizontal_scroll_recyclerView);
            horizontalScrollRecyclerView.setRecycledViewPool(recycledViewPool);
            container = itemView.findViewById(R.id.container);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String bgcolor, List<WishlistModel> viewAllProductList) {

            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(bgcolor)));
            horizontalLayoutTitle.setText(title);

            for (HorizontalProductScrollModel model : horizontalProductScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {

                    firebaseFirestore.collection("PRODUCTS")
                            .document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductPrice(task.getResult().getString("product_price"));

                                WishlistModel wishlistModel = viewAllProductList
                                        .get(horizontalProductScrollModelList.indexOf(model));
                                try {
                                    wishlistModel.setTotalRatings((long)task.getResult().get("total_ratings"));
                                    wishlistModel.setRating(task.getResult().getString("average_rating"));
                                    wishlistModel.setProductTitle(task.getResult().getString("product_title"));
                                    wishlistModel.setProductPrice(task.getResult().getString("product_price"));
                                    wishlistModel.setProductImage(task.getResult().getString("product_image_1"));
                                    wishlistModel.setFreeCoupens((long)task.getResult().get("free_coupens"));
                                    wishlistModel.setCuttedPrice(task.getResult().getString("cutted_price"));
                                    wishlistModel.setCOD(task.getResult().getBoolean("COD"));
                                    wishlistModel.setInStock((Long)task.getResult().get("stock_quantity") > 0);
                                }catch (Exception e){
                                    Toast.makeText(itemView.getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }




                                if (horizontalProductScrollModelList.indexOf(model) == horizontalProductScrollModelList.size() - 1) {
                                    if (horizontalScrollRecyclerView.getAdapter() != null) {
                                        horizontalScrollRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }

                            } else {
                                ///do nothing
                            }
                        }
                    });

                }
            }

            if (horizontalProductScrollModelList.size() > 8) {
                horizontalViewAllBtn.setVisibility(View.VISIBLE);
                horizontalViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 0);
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            } else {
                horizontalViewAllBtn.setVisibility(View.INVISIBLE);
            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            horizontalScrollRecyclerView.setLayoutManager(linearLayoutManager);
            horizontalScrollRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {
        private TextView gridTitle;
        private Button gridViewAllBtn;
        private GridLayout gridProductLayout;
        private ConstraintLayout containerGridLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridTitle = itemView.findViewById(R.id.grid_layout_title);
            gridViewAllBtn = itemView.findViewById(R.id.grid_layout_viewall_btn);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);
            containerGridLayout = itemView.findViewById(R.id.container_grid_layout);
        }

        private void setGridProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String grideTitle, String glBackgroundColor) {

            containerGridLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(glBackgroundColor)));
            gridTitle.setText(grideTitle);


            for (HorizontalProductScrollModel model : horizontalProductScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {

                    firebaseFirestore.collection("PRODUCTS")
                            .document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductPrice(task.getResult().getString("product_price"));


                                if (horizontalProductScrollModelList.indexOf(model) == horizontalProductScrollModelList.size() - 1) {
                                    setGridData(grideTitle, horizontalProductScrollModelList);

                                    if (!grideTitle.equals("")) {
                                        gridViewAllBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                                                Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                viewAllIntent.putExtra("layout_code", 1);
                                                viewAllIntent.putExtra("title", grideTitle);
                                                itemView.getContext().startActivity(viewAllIntent);
                                            }
                                        });
                                    }

                                }

                            } else {
                                ///do nothing
                            }
                        }
                    });

                }
            }

            setGridData(grideTitle, horizontalProductScrollModelList);


        }

        private void setGridData(String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {

            for (int x = 0; x < 4; x++) {

                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_title);
                TextView productDesc = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_desc);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_price);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.loading_spinner)).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                productDesc.setText(horizontalProductScrollModelList.get(x).getProductDesc());
                productPrice.setText("Rs." + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");

                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));

                if (!title.equals("")) {
                    int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(finalX).getProductID());
                            itemView.getContext().startActivity(productDetailsIntent);
                        }
                    });
                } else {
                    productPrice.setVisibility(View.GONE);
                }
            }


        }

    }
}
