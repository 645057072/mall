package com.hmall.service.Impl;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.CartMapper;
import com.hmall.dao.ProductMapper;
import com.hmall.pojo.Cart;
import com.hmall.pojo.Product;
import com.hmall.service.ICartService;
import com.hmall.unit.BigDecimalUtil;
import com.hmall.unit.PropertieUitl;
import com.hmall.vo.Cartproductvo;
import com.hmall.vo.Cartvo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class ICartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    public ServiceResponse<Cartvo> add(Integer userId,Integer productId,Integer count){
        if(userId==null||productId==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart==null){
//            这个产品不在购物车中，需要新增一个产品记录在购物车中
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
//            这个产品在购物车中已有记录，增加一个产品信息在购物车中
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServiceResponse<Cartvo> update(Integer userId,Integer productId,Integer count){
        if (userId==null||productId==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    public ServiceResponse<Cartvo> delete(Integer userId,String productIds){
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        cartMapper.deleteByuserIdProductIds(userId,productList);
        return this.list(userId);
    }

    public ServiceResponse<Cartvo> list(Integer userId){
        Cartvo cartvo=this.getCaritvolimit(userId);
        return ServiceResponse.createBySuccess(cartvo);
    }

    public ServiceResponse<Cartvo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedproductId(userId,productId,checked);
        return this.list(userId);

    }

    public ServiceResponse<Integer> getCountProductCount(Integer userId){
        if(userId==null){
            return ServiceResponse.createBySuccess(0);
        }
        return  ServiceResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }
    private Cartvo getCaritvolimit(Integer userId){
        Cartvo cartvo=new Cartvo();
        List<Cart> cartlist=cartMapper.selectCartByuserId(userId);
        List<Cartproductvo> cartproductvoList= Lists.newArrayList();//将cartproductvo添加到cartvo中

        BigDecimal cartTotalPrice=new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartlist)){
            for(Cart cartItem:cartlist){
                Cartproductvo cartproductvo=new Cartproductvo();
                cartproductvo.setId(cartItem.getId());
                cartproductvo.setUserId(cartItem.getUserId());
                cartproductvo.setProductId(cartItem.getProductId());

                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartproductvo.setProductmainImage(product.getMainImage());
                    cartproductvo.setProductname(product.getName());
                    cartproductvo.setProductsubtitle(product.getSubtitle());
                    cartproductvo.setProductstatus(product.getStatus());
                    cartproductvo.setProductprice(product.getPrice());
                    cartproductvo.setProductstock(product.getStock());

                    int buyLimitcount=0;
                    if(product.getStock()>=cartItem.getQuantity()){
                        //库存大于购买数量，限制数量成功
                        buyLimitcount=cartItem.getQuantity();
                        cartproductvo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCEESS);
                    }else {
                        buyLimitcount=product.getStock();
                        cartproductvo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitcount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartproductvo.setQuantity(buyLimitcount);
                    //计算总价
                    cartproductvo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartproductvo.getQuantity()));
                    cartproductvo.setProductchecked(cartItem.getChecked());
                 }
                 if(cartItem.getChecked()==Const.Cart.CHECKED){
                     cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartproductvo.getProductTotalPrice().doubleValue());
                 }
                 cartproductvoList.add(cartproductvo);//将cartproductvo添加到cartproductvoList
            }
        }
        cartvo.setCartTotalPrice(cartTotalPrice);
        cartvo.setCartproductvoList(cartproductvoList);
        cartvo.setAllchecked(this.getAllCheckedStatus(userId));
        cartvo.setImgeHost(PropertieUitl.getProperty("ftp.server.http.prefix"));

        return cartvo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if (userId ==null){
            return false;
        }
        return cartMapper.selectAllCheckedStatusByUserId(userId)==0;
    }
}
