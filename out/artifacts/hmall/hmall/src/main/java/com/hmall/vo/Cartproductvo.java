package com.hmall.vo;

import java.math.BigDecimal;

public class Cartproductvo {


    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;//购物车中数量
    private String productname;
    private String productsubtitle;
    private String productmainImage;
    private BigDecimal productprice;
    private  Integer productstatus;
    private Integer productstock;
    private Integer productchecked;//商品是否在选中状态

    private String limitQuantity;//限制数量的一个返回结果
    private BigDecimal productTotalPrice;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductsubtitle() {
        return productsubtitle;
    }

    public void setProductsubtitle(String productsubtitle) {
        this.productsubtitle = productsubtitle;
    }

    public String getProductmainImage() {
        return productmainImage;
    }

    public void setProductmainImage(String productmainImage) {
        this.productmainImage = productmainImage;
    }

    public BigDecimal getProductprice() {
        return productprice;
    }

    public void setProductprice(BigDecimal productprice) {
        this.productprice = productprice;
    }

    public Integer getProductstatus() {
        return productstatus;
    }

    public void setProductstatus(Integer productstatus) {
        this.productstatus = productstatus;
    }

    public Integer getProductstock() {
        return productstock;
    }

    public void setProductstock(Integer productstock) {
        this.productstock = productstock;
    }

    public Integer getProductchecked() {
        return productchecked;
    }

    public void setProductchecked(Integer productchecked) {
        this.productchecked = productchecked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
}
