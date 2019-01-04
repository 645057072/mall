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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.OrderMapper;
import com.hmall.dao.OrderitemMapper;
import com.hmall.dao.PayinfoMapper;
import com.hmall.pojo.Order;
import com.hmall.pojo.Orderitem;
import com.hmall.pojo.Payinfo;
import com.hmall.service.IOrderService;
import com.hmall.unit.BigDecimalUtil;
import com.hmall.unit.DateTimeUtil;
import com.hmall.unit.FTPUtil;
import com.hmall.unit.PropertieUitl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {
    private static Logger logger= LoggerFactory.getLogger(IOrderService.class);
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderitemMapper orderitemMapper;

    @Autowired
    private PayinfoMapper payinfoMapper;

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
