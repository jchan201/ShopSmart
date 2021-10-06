package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Shop extends RealmObject {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    private String desc = "";
    @Required private String name;
    @Required private String email;
    @Required private String phone;
    private String website = "";
    private Address address;
    @Required private String daysOpen;
    @Required private String startTime;
    @Required private String endTime;
    @Required private RealmList<ObjectId> salesOrders;
    @Required private RealmList<ObjectId> owners;

    public Shop() {}
    public Shop(String desc, String name, String email, String phone, String website,
                Address address, String daysOpen, String startTime, String endTime,
                RealmList<ObjectId> salesOrders, RealmList<ObjectId> owners) {
        this.desc = desc;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.address = address;
        this.daysOpen = daysOpen;
        this.startTime = startTime;
        this.endTime = endTime;
        this.salesOrders = salesOrders;
        this.owners = owners;
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

    public String getDaysOpen() {
        return daysOpen;
    }
    public void setDaysOpen(String daysOpen) {
        this.daysOpen = daysOpen;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
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