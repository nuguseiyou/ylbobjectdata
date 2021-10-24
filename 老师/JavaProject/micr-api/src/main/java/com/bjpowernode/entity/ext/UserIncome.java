package com.bjpowernode.entity.ext;

import com.bjpowernode.entity.Income;

//用户的收益信息
public class UserIncome extends Income {

    private String proudctName;

    public String getProudctName() {
        return proudctName;
    }

    public void setProudctName(String proudctName) {
        this.proudctName = proudctName;
    }
}
