package com.nuguseiyou.model.ext;

import java.io.Serializable;

public class InvestTopList implements Serializable {
    private static final long serialVersionUID = -4192928090930683913L;

    private String phone;

    private double scores;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }

    public InvestTopList(String phone, double scores) {
        this.phone = phone;
        this.scores = scores;
    }

    public InvestTopList() {
    }
}
