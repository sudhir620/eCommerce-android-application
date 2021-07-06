package com.skcoder.gomall;

public class ProductSpecificationModel {
    public static final int SPECIFICATION_TITLE = 0;
    public static final int SPECIFICATION_BODY = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //SPECIFICATION TITLE
    String title;

    public ProductSpecificationModel(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    //SPECIFICATION TITLE

    //SPECIFICATION BODY
    String FeatureName;
    String FreatureValue;

    public ProductSpecificationModel(int type, String featureName, String freatureValue) {
        this.type = type;
        FeatureName = featureName;
        FreatureValue = freatureValue;
    }

    public String getFeatureName() {
        return FeatureName;
    }

    public void setFeatureName(String featureName) {
        FeatureName = featureName;
    }

    public String getFreatureValue() {
        return FreatureValue;
    }

    public void setFreatureValue(String freatureValue) {
        FreatureValue = freatureValue;
    }
    //SPECIFICATION BODY

}
