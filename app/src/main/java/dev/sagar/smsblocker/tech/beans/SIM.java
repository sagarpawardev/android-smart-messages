package dev.sagar.smsblocker.tech.beans;

/**
 * Created by sagarpawar on 01/01/18.
 */

public class SIM {
    private String operator;
    private int id;
    private String number;
    private int slotNo;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }
}
