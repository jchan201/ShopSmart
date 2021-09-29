package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AppUser extends RealmObject implements Serializable {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    @Required private String userType;
    @Required private String firstName;
    @Required private String lastName;
    private int age;
    @Required private String email;
    private String phone = "";
    @Required private Date birthdate;
    private RealmList<Address> addresses;
    private RealmList<PaymentMethod> paymentMethods;
    private RealmList<ProductItem> shoppingCart;
    @Required private RealmList<ObjectId> orders;
    @Required private RealmList<ObjectId> shops;

    public AppUser() {
        userType = "";
        firstName = "";
        lastName = "";
        age = 0;
        email = "";
        birthdate = new Date();
    }
    public AppUser(String userType, String firstName, String lastName, int age, String email,
                   String phone, Date birthdate) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
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

    public Date getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public RealmList<Address> getAddresses() {
        return addresses;
    }
    public void addAddress(Address address) {
        addresses.add(address);
    }
    public void removeAddress(/* search params */) {
        // NEED TO IMPLEMENT
    }

    public RealmList<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        paymentMethods.add(paymentMethod);
    }
    public void removePaymentMethod(/* search params */) {
        // NEED TO IMPLEMENT
    }

    public RealmList<ProductItem> getShoppingCart() {
        return shoppingCart;
    }
    public void addShoppingItem(ProductItem productItem) {
        shoppingCart.add(productItem);
    }
    public void removeShoppingItem(/* search params */) {
        // NEED TO IMPLEMENT
    }

    public RealmList<ObjectId> getOrders() {
        return orders;
    }
    public void addOrder(ObjectId order) {
        orders.add(order);
    }
    public void removeOrder(/* search params */) {
        // NEED TO IMPLEMENT
    }

    public RealmList<ObjectId> getShops() {
        return shops;
    }
    public void addShop(ObjectId shop) {
        shops.add(shop);
    }
    public void removeShop(/* search params */) {
        // NEED TO IMPLEMENT
    }
}