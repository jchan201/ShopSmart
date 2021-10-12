package com.shopsmart.shopsmart;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AppUser extends RealmObject {
    @PrimaryKey @Required private ObjectId _id = new ObjectId();
    @Required private String userType;
    @Required private String firstName;
    private String middleInitial;
    @Required private String lastName;
    private int age;
    @Required private String email;
    private String phone = "";
    @Required private Date birthdate;
    private final RealmList<Address> addresses = new RealmList<>();
    private final RealmList<PaymentMethod> paymentMethods = new RealmList<>();
    private final RealmList<BankInformation> bankInfo = new RealmList<>();
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
    public AppUser(String userType, String firstName, String middleInitial, String lastName,
                   int age, String email, String phone, Date birthdate) {
        this.userType = userType;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
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

    public String getMiddleInitial() {
        return middleInitial;
    }
    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
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

    public String getBirthdateString(){
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        String strDate = formatter.format(birthdate);
        return strDate;
    }

    public RealmList<Address> getAddresses() {
        return addresses;
    }
    public void addAddress(Address address) {
        addresses.add(address);
    }
    public void removeAddress(int pos) {
        addresses.remove(pos);
    }

    public RealmList<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        paymentMethods.add(paymentMethod);
    }
    public void removePaymentMethod(int pos) {
        paymentMethods.remove(pos);
    }

    public RealmList<ProductItem> getShoppingCart() {
        return shoppingCart;
    }
    public void addShoppingItem(ProductItem productItem) {
        shoppingCart.add(productItem);
    }
    public void removeShoppingItem(int pos) {
        shoppingCart.remove(pos);
    }

    public RealmList<ObjectId> getOrders() {
        return orders;
    }
    public void addOrder(ObjectId order) {
        orders.add(order);
    }
    public void removeOrder(int pos) {
        orders.remove(pos);
    }

    public RealmList<ObjectId> getShops() {
        return shops;
    }
    public void addShop(ObjectId shop) {
        shops.add(shop);
    }
    public void removeShop(int pos) {
        shops.remove(pos);
    }
}