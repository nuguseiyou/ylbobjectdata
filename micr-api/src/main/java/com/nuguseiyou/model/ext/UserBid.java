package com.nuguseiyou.model.ext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 某个用户的最近投资
 */
public class UserBid implements Serializable {
    private static final long serialVersionUID = -2320845469291184902L;
    //产品名称
    private String productName;
    //投资金额
    private BigDecimal bidMoney;
    //投资日期
    private Date bidTime;
    //投资状态
    private String bidStatus;

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(BigDecimal bidMoney) {
        this.bidMoney = bidMoney;
    }

    public Date getBidTime() {
        return bidTime;
    }

    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }
}
