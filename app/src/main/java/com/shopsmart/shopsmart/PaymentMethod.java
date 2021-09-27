package com.shopsmart.shopsmart;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class PaymentMethod extends RealmObject {
    // these may need to be hashed
    @Required private String cardNumber;
    @Required private String expiry;
    @Required private String securityCode;
    private Address billingAddress;

    public PaymentMethod() {
        cardNumber = "";
        expiry = "";
        securityCode = "";
        billingAddress = null;
    }
    public PaymentMethod(String cardNumber, String expiry, String securityCode, Address billingAddress) {
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.securityCode = securityCode;
        this.billingAddress = billingAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
}
