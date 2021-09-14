package com.shopsmart.shopsmart;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class PaymentMethod extends RealmObject {
    // these may need to be hashed
    private long cardNumber;
    private int expiry;
    private int securityCode;
    private Address billingAddress;

    public PaymentMethod() {
        cardNumber = -1;
        expiry = -1;
        securityCode = -1;
        billingAddress = null;
    }
    public PaymentMethod(long cardNumber, int expiry, int securityCode, Address billingAddress) {
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.securityCode = securityCode;
        this.billingAddress = billingAddress;
    }

    public long getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getExpiry() {
        return expiry;
    }
    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    public int getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(int securityCode) {
        this.securityCode = securityCode;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
}
