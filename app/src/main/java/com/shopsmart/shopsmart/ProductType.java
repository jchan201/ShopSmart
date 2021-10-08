package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ProductType extends RealmObject implements Serializable {
    @PrimaryKey @Required private final ObjectId _id = new ObjectId();
    @Required private String name;

    public ProductType() {
        name = "";
    }
    public ProductType(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}