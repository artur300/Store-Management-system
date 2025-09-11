package com.myshopnet.models;

import com.google.gson.annotations.SerializedName;

public enum Role {
    @SerializedName("EMPLOYEE")
    EMPLOYEE,
    @SerializedName("ADMIN")
    ADMIN,
    @SerializedName("CUSTOMER")
    CUSTOMER
}

