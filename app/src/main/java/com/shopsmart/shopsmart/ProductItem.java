package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class ProductItem extends RealmObject implements Serializable {
    @Required private ObjectId product_id;
    private int quantity;

    public ProductItem() {}
    public ProductItem(ObjectId product_id, int quantity) {
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public ObjectId getProductId() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}