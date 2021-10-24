package com.bjpowernode.entity.ext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProudctNameBid implements Serializable {

    private static final long serialVersionUID = 723725482625775834L;
    private String productName;
    private BigDecimal bidMoney;
    private Date bidTime;
    private Integer bidStatus;

    public Integer getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(Integer bidStatus) {
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
