package com.hmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cartvo {
    List<Cartproductvo> cartproductvoList;
    private BigDecimal cartTotalPrice;
    private boolean allchecked;//是否已都勾选
    private String imgeHost;

    public List<Cartproductvo> getCartproductvoList() {
        return cartproductvoList;
    }

    public void setCartproductvoList(List<Cartproductvo> cartproductvoList) {
        this.cartproductvoList = cartproductvoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllchecked() {
        return allchecked;
    }

    public void setAllchecked(boolean allchecked) {
        this.allchecked = allchecked;
    }

    public String getImgeHost() {
        return imgeHost;
    }

    public void setImgeHost(String imgeHost) {
        this.imgeHost = imgeHost;
    }
}
