package com.myshopnet.logs;

public enum LogType {
    EMPLOYEE_REGISTERED, CUSTOMER_REGISTERED,
    SALE, RESTOCK, PURCHASE, // PURCHASE = קניית מלאי ע"י החנות
    LOGIN, LOGOUT, SERVER_LISTEN,
    MESSAGE_RECEIVED,
    REQUEST_RECIEVED
}
