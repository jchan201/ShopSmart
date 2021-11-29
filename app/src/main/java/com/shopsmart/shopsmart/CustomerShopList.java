package com.shopsmart.shopsmart;

public class CustomerShopList {
    private String shopName;
    private String shopId;
    private int idx;
    private String shopEmail;
    private String shopPhone;

    public CustomerShopList(String shopName, String shopId, int idx, String shopEmail, String shopPhone) {
        this.shopName = shopName;
        this.shopId = shopId;
        this.idx = idx;
        this.shopEmail = shopEmail;
        this.shopPhone = shopPhone;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }
}
