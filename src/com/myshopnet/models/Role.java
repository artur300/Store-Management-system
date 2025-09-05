package com.myshopnet.models;

import com.google.gson.annotations.SerializedName;

public enum Role {
    @SerializedName("EMPLOYEE")
    EMPLOYEE,
    @SerializedName("ADMIN")
    ADMIN,
    @SerializedName("CUSTOMER")
    CUSTOMER,
    @SerializedName("NEW_CUSTOMER")
    NEW_CUSTOMER,
    @SerializedName("VIP_CUSTOMER")
    VIP_CUSTOMER,
    @SerializedName("RETURNING_CUSTOMER")
    RETURNING_CUSTOMER
}

