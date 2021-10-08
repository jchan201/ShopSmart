package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Product extends RealmObject implements Serializable {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    @Required private ObjectId shop_id;
    @Required private ObjectId type_id;
    @Required private String name;
    @Required private String desc;
    private double price;

    public Product() {}
    public Product(ObjectId shop_id, ObjectId type_id, String name, String desc, double price) {
        this.shop_id = shop_id;
        this.type_id = type_id;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public ObjectId getId() {
        return _id;
    }
    public ObjectId getShopId() {
        return shop_id;
    }
    public ObjectId getTypeId() {
        return type_id;
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
}
