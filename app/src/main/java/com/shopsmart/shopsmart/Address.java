package com.shopsmart.shopsmart;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class Address extends RealmObject implements Serializable {
    @Required private String address1;
    @Required private String address2;
    @Required private String country;
    @Required private String province;
    @Required private String city;
    @Required private String postalCode;

    public Address() {
        this.address1 = "";
        this.address2 = "";
        this.country = "";
        this.province = "";
        this.city = "";
        this.postalCode = "";
    }
    public Address(String address1, String address2, String country, String province, String city, String postalCode) {
        this.address1 = address1;
        this.address2 = address2;
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address) {
        this.address1 = address;
    }

    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}