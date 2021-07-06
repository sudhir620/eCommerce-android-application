package com.skcoder.gomall;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.List;

public class HomePageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_BANNER_IMAGE = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    private int type;

    //// banner slider
    private List<SlideModel> imageSliderList;

    public HomePageModel(int type, List<SlideModel> imageSliderList) {
        this.type = type;
        this.imageSliderList = imageSliderList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SlideModel> getImageSliderList() {
        return imageSliderList;
    }

    public void setImageSliderList(List<SlideModel> imageSliderList) {
        this.imageSliderList = imageSliderList;
    }

    //// banner slider


    //// strip ad banner
    private String resource;
    public HomePageModel(int type, String resource) {
        this.type = type;
        this.resource = resource;
    }
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    //// strip ad banner

    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private String backgroundColor;

    //// Horizontal product scroll layout
    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String backgroundColor, List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.backgroundColor = backgroundColor;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }
    //// Horizontal product scroll layout

    //// Grid product layout

    public HomePageModel(int type, List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String backgroundColor) {
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.backgroundColor = backgroundColor;
    }

    //// Grid product layout

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

}
