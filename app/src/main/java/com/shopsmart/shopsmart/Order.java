package com.shopsmart.shopsmart;


import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Order extends RealmObject {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    @Required private ObjectId cust_id;
    @Required private Date date;
    private RealmList<ProductItem> items;
    private double subtotal;
    private double tax;

    public Order() {}
    public Order(ObjectId cust_id, Date date, double subtotal, double tax) {
        this.cust_id = cust_id;
        this.date = date;
        this.subtotal = subtotal;
        this.tax = tax;
    }

    public ObjectId getId() {
        return _id;
    }
    public ObjectId getCust_id() {
        return cust_id;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<ProductItem> getItems() {
        return items;
    }
    public void setItems(RealmList<ProductItem> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
}
