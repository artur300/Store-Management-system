package com.myshopnet.models;

import com.google.gson.annotations.SerializedName;

public enum CustomerType {
    @SerializedName("New Customer")
    NEW_CUSTOMER,
    @SerializedName("VIP Customer")
    VIP_CUSTOMER,
    @SerializedName("Returning Customer")
    RETURNING_CUSTOMER,
}
