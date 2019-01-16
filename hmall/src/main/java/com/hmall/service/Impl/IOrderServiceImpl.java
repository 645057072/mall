package com.hmall.service.Impl;


import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.*;
import com.hmall.pojo.*;
import com.hmall.service.IOrderService;
import com.hmall.unit.BigDecimalUtil;
import com.hmall.unit.DateTimeUtil;
import com.hmall.unit.FTPUtil;
import com.hmall.unit.PropertieUitl;
import com.hmall.vo.Orderitemvo;
import com.hmall.vo.Orderproductvo;
import com.hmall.vo.Ordervo;
import com.hmall.vo.Shippingvo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {
    private static Logger logger= LoggerFactory.getLogger(IOrderService.class);
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderitemMapper orderitemMapper;

    @Autowired
    private PayinfoMapper payinfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;
    public ServiceResponse create(Integer userId,Integer shippingId){
        List<Cart> cartList=cartMapper.selectCartByuserId(userId);

        ServiceResponse serviceResponse=this.getCartOrderItem(userId,cartList);
        if (!serviceResponse.isSucess()){
            return serviceResponse;
        }
        List<Orderitem>  orderitemList= (List<Orderitem>) serviceResponse.getData();
        BigDecimal payment=this.getOrderTotalPrice(orderitemList);

//        生成订单
        Order order=this.assembleOrder(userId,shippingId,payment);
        if (order==null){
            return ServiceResponse.createByErrorMessage("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderitemList)){
            return ServiceResponse.createByErrorMessage("生成订单为空");
        }
        for (Orderitem orderitem:orderitemList){
            orderitem.setOrderNo(order.getOrderNo());
        }
//        mybaits批量加入
        orderitemMapper.batchinsert(orderitemList);
        //生成成功检验库存
        this.reduceProductStock(orderitemList);
//        清空购物车
        this.cloneCart(cartList);
       Ordervo ordervo=assembleOrdervo(order,orderitemList);
        return ServiceResponse.createBySuccess(ordervo);
    }

    public ServiceResponse<String> cancel(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("该用户此订单不存在");
        }
        if (order.getStatus()!=Const.OrderStatusenum.NO_PAY.getCode()){
            return ServiceResponse.createByErrorMessage("订单已付款，不能取消订单");
        }
        Order updateOrder=new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusenum.CANCLELLED.getCode());
        int row=orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row>0){
            return ServiceResponse.createBySuccess("订单取消成功");
        }
        return ServiceResponse.createByErrorMessage("订单取消失败");
    }

    public ServiceResponse getOrderCartProduct(Integer userId){
        Orderproductvo orderproductvo=new Orderproductvo();
//        从购物车中获取数据
        List<Cart> cartList=cartMapper.selectByCheckedUserId(userId);
        ServiceResponse serviceResponse=this.getCartOrderItem(userId,cartList);
        if (!serviceResponse.isSucess()){
            return serviceResponse;
        }
        List<Orderitem> orderitemList= (List<Orderitem>) serviceResponse.getData();
        List<Orderitemvo> orderitemvoList=Lists.newArrayList();
        BigDecimal payment=new BigDecimal("0");
        for (Orderitem orderitem:orderitemList){
           payment=BigDecimalUtil.add(payment.doubleValue(),orderitem.getTotalPrice().doubleValue());
            orderitemvoList.add(assembleOrderitemvo(orderitem));
        }
        orderproductvo.setOrderitemvolist(orderitemvoList);
        orderproductvo.setProductTotalPrice(payment);
        orderproductvo.setImageHost(PropertieUitl.getProperty("ftp.server.http.prefix"));
        return ServiceResponse.createBySuccess(orderproductvo);

    }

    public ServiceResponse<Ordervo> getOrderdetail(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if (order!=null){
            List<Orderitem> orderitemLsit=orderitemMapper.getByOrderNoUserId(orderNo,userId);
            Ordervo ordervo=assembleOrdervo(order,orderitemLsit);
            return ServiceResponse.createBySuccess(ordervo);
        }
        return ServiceResponse.createByErrorMessage("没有找到该订单");
    }

    public ServiceResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectByUserId(userId);
        List<Ordervo> ordervoList=assembleOrderVoList(orderList,userId);
        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(ordervoList);
        return ServiceResponse.createBySuccess(pageResult);

    }
    private List<Ordervo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<Ordervo> ordervoList=Lists.newArrayList();
        for (Order order:orderList){
            List<Orderitem> orderitemList=Lists.newArrayList();
            if(userId==null){
                //todo.管理员查询，不需要用户ID
            }else {
           orderitemList=orderitemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            Ordervo ordervo=assembleOrdervo(order,orderitemList);
            ordervoList.add(ordervo);
        }
        return ordervoList;
    }
    private Ordervo assembleOrdervo(Order order, List<Orderitem> orderitemList){
        Ordervo ordervo=new Ordervo();
        ordervo.setOrderNo(order.getOrderNo());
        ordervo.setPayment(order.getPayment());
        ordervo.setPaymentType(order.getPaymentType());
        ordervo.setPaymentTypeDesc(Const.PayPlatformEnum.codeof(order.getPaymentType()).getValue());
        ordervo.setPostage(order.getPostage());
        ordervo.setStatus(order.getStatus());
        ordervo.setStatusDesc(Const.OrderStatusenum.codeof(order.getStatus()).getValue());
        ordervo.setPayment(order.getPayment());
        ordervo.setShippingId(order.getShippingId());
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping!=null){
            ordervo.setReceiverName(shipping.getReceiverName());
            ordervo.setShippingvo(assembleShippingvo(shipping));
        }
       ordervo.setPaymentTime(DateTimeUtil.dateTostr(order.getPaymentTime()));
        ordervo.setSendTime(DateTimeUtil.dateTostr(order.getSendTime()));
        ordervo.setEndTime(DateTimeUtil.dateTostr(order.getEndTime()));
        ordervo.setCreateTime(DateTimeUtil.dateTostr(order.getCreateTime()));
        ordervo.setUpdateTime(DateTimeUtil.dateTostr(order.getUpdateTime()));
        ordervo.setImageHost(PropertieUitl.getProperty("ftp.server.http.prefix"));
        List<Orderitemvo> orderitemvoList = Lists.newArrayList();
        for(Orderitem orderitem:orderitemList){
            Orderitemvo orderitemvo=assembleOrderitemvo(orderitem);
            orderitemvoList.add(orderitemvo);
        }
        return ordervo;
    }

    private Orderitemvo assembleOrderitemvo(Orderitem orderitem){
        Orderitemvo orderitemvo=new Orderitemvo();
        orderitemvo.setOrderNo(orderitem.getOrderNo());
        orderitemvo.setProductId(orderitem.getProductId());
        orderitemvo.setProductName(orderitem.getProductName());
        orderitemvo.setProductImage(orderitem.getProductImage());
        orderitemvo.setQuantity(orderitem.getQuantity());
        orderitemvo.setCurrentUnitPrice(orderitem.getCurrentUnitPrice());
        orderitemvo.setTotalPrice(orderitem.getTotalPrice());
        orderitemvo.setCreateTime(DateTimeUtil.dateTostr(orderitem.getCreateTime()));
        return orderitemvo;
    }
    private Shippingvo assembleShippingvo(Shipping shipping){
        Shippingvo shippingvo=new Shippingvo();
        shippingvo.setReceiverName(shipping.getReceiverName());
        shippingvo.setReceiverPhone(shipping.getReceiverPhone());
        shippingvo.setReceiverMobile(shipping.getReceiverMobile());
        shippingvo.setReceiverProvince(shipping.getReceiverProvince());
        shippingvo.setReceiverAddress(shipping.getReceiverAddress());
        shippingvo.setReceiverCity(shipping.getReceiverCity());
        shippingvo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingvo.setReceiverZip(shipping.getReceiverZip());
        return shippingvo;
    }
    private void cloneCart( List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    private void reduceProductStock(List<Orderitem> orderitemList) {
        for (Orderitem orderitem : orderitemList) {
            Product product = productMapper.selectByPrimaryKey(orderitem.getProductId());
            product.setStock(product.getStock()-orderitem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order=new Order();
        long orderNo=this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusenum.NO_PAY.getCode());
        order.setPostage(0);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        int rowCount=orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;

    }
    private Long generateOrderNo(){
        Long currentTime=System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
    private BigDecimal getOrderTotalPrice(List<Orderitem>  orderitemList){
            BigDecimal payment=new BigDecimal("0");
            for(Orderitem orderitem:orderitemList){
                payment= BigDecimalUtil.add(payment.doubleValue(),orderitem.getTotalPrice().doubleValue());
            }
            return payment;
    }
    private ServiceResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<Orderitem> orderitemList=Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)){
            return ServiceResponse.createByErrorMessage("购物车为空");
        }
        //检验购物车中数量，包括商品的状态和数量
        for(Cart cartItem:cartList){
            Orderitem orderitem=new Orderitem();
            Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            //检验产品是否在线销售
            if(Const.ProductStutasenum.ON_SALES.getCode()!=product.getStatus()){
                return ServiceResponse.createByErrorMessage("产品"+product.getName()+"已下架" );
            }
            if(orderitem.getQuantity()>product.getStock()){
                return ServiceResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }
            orderitem.setUserId(userId);
            orderitem.setProductId(product.getId());
            orderitem.setProductName(product.getName());
            orderitem.setProductImage(product.getMainImage());
            orderitem.setCurrentUnitPrice(product.getPrice());
            orderitem.setQuantity(orderitem.getQuantity());
            orderitem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),orderitem.getQuantity()));
            orderitemList.add(orderitem);

        }
        return ServiceResponse.createBySuccess(orderitemList);
    }

    public ServiceResponse pay(Integer userId, Long orderNo, String path){
        Map<String,String> resultMap= Maps.newHashMap();
        Order order= orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if(order==null){
            return ServiceResponse.createByErrorMessage("用户没有查询到该订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));//将订单插入到map中，使用K,V进行显示

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("hmall扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<Orderitem> orderItemList=orderitemMapper.getByOrderNoUserId(orderNo,userId);
        for(Orderitem orderitem:orderItemList){
            GoodsDetail goods=GoodsDetail.newInstance(orderitem.getProductId().toString(),orderitem.getProductName(),
                    BigDecimalUtil.mul(orderitem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderitem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertieUitl.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);

        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder=new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                String qrPath=String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName=String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                // 需要修改为运行机器上的路径
                File targetFile=new File(path,qrFileName);
                try {
                    FTPUtil.uploadfile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.info("上传二维码数据异常",e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl=PropertieUitl.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUrl",qrUrl);
                return ServiceResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
              return ServiceResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServiceResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }

    }
    private void dumpResponse(AlipayResponse response) {
        if (!(null == response)) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServiceResponse alicallback(Map<String,String> params){
      Long orderNo=Long.parseLong(params.get("out_trade_no"));
      String tradeNo=params.get("trade_no");
      String tradeStatus=params.get("trade_status");
      Order order=orderMapper.selectByOrderNo(orderNo);
      if(orderNo==null){
          return ServiceResponse.createByErrorMessage("非商城订单，回调异常");
      }
      if(order.getStatus()>= Const.OrderStatusenum.PAID.getCode()){
            return ServiceResponse.createBySuccess("订单重复调用");
      }
      if(Const.alipaycallback.RESPONSE_SUCCESS.equals(tradeStatus)){
          order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
          order.setStatus(Const.OrderStatusenum.PAID.getCode());
          orderMapper.updateByPrimaryKeySelective(order);
      }
        Payinfo payinfo=new Payinfo();
      payinfo.setUserId(order.getUserId());
      payinfo.setOrderNo(order.getOrderNo());
      payinfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
      payinfo.setPlatformNumber(tradeNo);
      payinfo.setPlatformStatus(tradeStatus);
      payinfoMapper.insert(payinfo);
      return ServiceResponse.createBySuccess();
    }

    public ServiceResponse queryOrderPayStatus(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if (order==null){
            return ServiceResponse.createByErrorMessage("没有该订单");
        }
        if (order.getStatus()>=Const.OrderStatusenum.PAID.getCode()){
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }
}
