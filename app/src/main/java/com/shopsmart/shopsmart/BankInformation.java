package com.shopsmart.shopsmart;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class BankInformation extends RealmObject implements Serializable {
    @Required private String accountNumber;
    @Required private String institutionNumber;
    @Required private String transitNumber;

    public BankInformation() {
        this.accountNumber = "";
        this.institutionNumber = "";
        this.transitNumber = "";
    }
    public BankInformation(String accountNumber, String institutionNumber, String transitNumber) {
        this.accountNumber = accountNumber;
        this.institutionNumber = institutionNumber;
        this.transitNumber = transitNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getInstitutionNumber() {
        return institutionNumber;
    }
    public void setInstitutionNumber(String institutionNumber) {
        this.institutionNumber = institutionNumber;
    }

    public String getTransitNumber() {
        return transitNumber;
    }
    public void setTransitNumber(String transitNumber) {
        this.transitNumber = transitNumber;
    }
}