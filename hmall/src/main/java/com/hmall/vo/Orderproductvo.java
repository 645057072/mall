package com.hmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class Orderproductvo {
    private List<Orderitemvo> orderitemvolist;
    private BigDecimal productTotalPrice;
    private String ImageHost;

    public List<Orderitemvo> getOrderitemvolist() {
        return orderitemvolist;
    }

    public void setOrderitemvolist(List<Orderitemvo> orderitemvolist) {
        this.orderitemvolist = orderitemvolist;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return ImageHost;
    }

    public void setImageHost(String imageHost) {
        ImageHost = imageHost;
    }
}

