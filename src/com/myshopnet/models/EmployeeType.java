package com.myshopnet.models;

import com.google.gson.annotations.SerializedName;

public enum EmployeeType {
    @SerializedName("SELLER")
    SELLER,
    @SerializedName("CASHIER")
    CASHIER,
    @SerializedName("SHIFT_MANAGER")
    SHIFT_MANAGER
}
