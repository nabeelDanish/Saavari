package com.savaari.savaari_driver.entity;

public class Payment {

    public static final int CASH = 0, CREDIT_CARD = 1;

    // Main Attributes
    int paymentID;
    double amountPaid;
    double change;
    long timestamp;
    int paymentMode;

    // Main Constructor
    public Payment() {
        paymentID = -1;
        amountPaid = 0;
        change = 0;
        timestamp = 0;
        paymentMode = com.savaari.savaari_driver.entity.Payment.CASH;
    }

    // -----------------------------------------------------------------------
    //                      GETTERS and SETTERS
    // -----------------------------------------------------------------------
    public void setPaymentID(int paymentID) { this.paymentID = paymentID; }
    public int getPaymentID() { return paymentID; }
    public double getAmountPaid() {
        return amountPaid;
    }
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }
    public double getChange() {
        return change;
    }
    public void setChange(double change) {
        this.change = change;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }
}
