package com.hmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
@JsonSerialize(include =JsonSerialize.Inclusion.NON_NULL )
//保证序列化时json的时候，如果是null的对象，Key也会消失
public class ServiceResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;
    private ServiceResponse(int status){
        this.status=status;
    }
    private ServiceResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServiceResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    private ServiceResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    @JsonIgnore
//    使之不在json序列化
    public boolean isSucess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    public static<T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static<T> ServiceResponse<T> createBySuccessMessage(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static<T> ServiceResponse<T> createBySuccess(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static<T> ServiceResponse<T> createBySuccess(String msg,T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static<T> ServiceResponse<T> createByError(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static<T> ServiceResponse<T> createByErrorMessage(String errorMassage){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),errorMassage);
    }
    public static<T> ServiceResponse<T> createByErrorCodeMessgae(int errorCode,String errorMessage){
        return new ServiceResponse<T>(errorCode,errorMessage);
    }
}
