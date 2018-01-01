package dev.sagar.smsblocker.tech.beans;

/**
 * Created by sagarpawar on 01/01/18.
 */

public class SIM {
    private String operator;
    private int subscriptionId;
    private String number;
    private int slotNo;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }
}
