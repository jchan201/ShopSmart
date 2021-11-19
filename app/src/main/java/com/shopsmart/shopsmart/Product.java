package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Product extends RealmObject implements Serializable {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    @Required private ObjectId shop_id;
    @Required private String productType;
    @Required private String name;
    @Required private String desc;
    private double price;
    private int stock;

    public Product() {}
    public Product(ObjectId shop_id, String productType, String name, String desc, double price) {
        this.shop_id = shop_id;
        this.productType = productType;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.stock = 0;
    }

    public ObjectId getId() {
        return _id;
    }
    public ObjectId getShopId() {
        return shop_id;
    }

    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
