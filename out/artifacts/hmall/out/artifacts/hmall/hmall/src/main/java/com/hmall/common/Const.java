package com.hmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER="currentuser";
    public static final String USERNAME="username";
    public static final String EMIAL="emial";
    public interface Role{
        int ROLE_CUSTOMER=0;
        int ROLE_ADMIN=1;
    }
    public interface ProductlistOrderby{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_asc","price_desc");
    }

    public interface Cart{
        int CHECKED=1;//购物车被选中状态
        int UN_CHECKED=0;//购物车未被选中状态
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCEESS="LIMIT_NUM_SUCEESS";
    }
    public enum ProductStutasenum{
        ON_SALES(1,"在线");
        private String value;
       private int code;

        ProductStutasenum( int code,String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusenum{
        CANCLELLED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        private String value;
        private int code;

        OrderStatusenum( int code,String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface alipaycallback{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";//等待买家付款
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";//超时订单关闭

        String RESPONSE_SUCCESS="SUCCESS";
        String RESPONSE_FAILED="failed";
    }
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");
        private String value;
        private int code;
        PayPlatformEnum( int code,String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
