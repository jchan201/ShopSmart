package com.shopsmart.shopsmart;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class Address extends RealmObject {
    @Required private String country;
    @Required private String province;
    @Required private String city;
    @Required private String postalCode;

    public Address() {
        this.country = "";
        this.province = "";
        this.city = "";
        this.postalCode = "";
    }
    public Address(String country, String province, String city, String postalCode) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
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