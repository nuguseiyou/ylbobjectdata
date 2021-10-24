package com.nuguseiyou.model.ext;

import com.nuguseiyou.model.Bid;

import java.io.Serializable;

/**
 * 2021/9/15
 * 某个产品的最近投资排行
 */
public class PhoneBid extends Bid implements Serializable {
    private static final long serialVersionUID = 3452641895846083418L;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhoneBid(String phone) {
        this.phone = phone;
    }

    public PhoneBid() {
    }
}
