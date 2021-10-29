package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Shop extends RealmObject implements Serializable {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    private String desc = "";
    @Required private String name;
    @Required private String email;
    @Required private String phone;
    private String website = "";
    private Address address;
    @Required private final RealmList<String> startTimes = new RealmList<>();
    @Required private final RealmList<String> endTimes = new RealmList<>();
    @Required private final RealmList<ObjectId> salesOrders = new RealmList<>();
    @Required private final RealmList<ObjectId> owners = new RealmList<>();

    public Shop() {}
    public Shop(String desc, String name, String email, String phone, String website,
                Address address) {
        this.desc = desc;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.address = address;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public RealmList<String> getStartTimes() {
        return startTimes;
    }
    public void addStartTime(String time) {
        startTimes.add(time);
    }
    public void setStartTime(int pos, String time) {
        startTimes.set(pos, time);
    }

    public RealmList<String> getEndTimes() {
        return endTimes;
    }
    public void addEndTime(String time) {
        endTimes.add(time);
    }
    public void setEndTime(int pos, String time) {
        endTimes.set(pos, time);
    }

    public RealmList<ObjectId> getOrders() {
        return salesOrders;
    }
    public void addOrder(ObjectId order) {
        salesOrders.add(order);
    }
    public void removeOrder(int pos) {
        salesOrders.remove(pos);
    }

    public RealmList<ObjectId> getOwners() {
        return owners;
    }
    public void addOwner(ObjectId owner) {
        owners.add(owner);
    }
    public void removeOwner(int pos) {
        owners.remove(pos);
    }
}