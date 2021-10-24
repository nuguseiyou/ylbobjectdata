package com.nuguseiyou.model.ext;

import com.nuguseiyou.model.Income;

import java.io.Serializable;

public class UserIncome extends Income implements Serializable {

    private static final long serialVersionUID = 5647254872117644453L;

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
