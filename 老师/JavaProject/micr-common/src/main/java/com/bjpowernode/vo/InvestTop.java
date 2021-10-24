package com.bjpowernode.vo;

public class InvestTop {
    private String phone;
    private Double money;

    public InvestTop() {
    }

    public InvestTop(String phone, Double money) {
        this.phone = phone;
        this.money = money;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
