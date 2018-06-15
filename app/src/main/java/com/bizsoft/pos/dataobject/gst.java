package com.bizsoft.pos.dataobject;

/**
 * Created by shri on 14/7/17.
 */

public class gst {

    String totalPurchaseGST;
    String totalBillGST;
    String status;
    String amount;

    public String getTotalPurchaseGST() {
        return totalPurchaseGST;
    }

    public void setTotalPurchaseGST(String totalPurchaseGST) {
        this.totalPurchaseGST = totalPurchaseGST;
    }

    public String getTotalBillGST() {
        return totalBillGST;
    }

    public void setTotalBillGST(String totalBillGST) {
        this.totalBillGST = totalBillGST;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
