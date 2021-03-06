package com.cskaoyan.shopping.utils;


import com.cskaoyan.mall.commons.exception.ExceptionUtil;
import com.cskaoyan.mall.commons.result.AbstractResponse;
import com.cskaoyan.mall.constant.ShoppingRetCode;

public class ExceptionProcessorUtils {

    public static void wrapperHandlerException(AbstractResponse response, Exception e){
        try {
            ExceptionUtil.handlerException4biz(response,e);
        } catch (Exception ex) {
            response.setCode(ShoppingRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(ShoppingRetCode.SYSTEM_ERROR.getMessage());
        }
    }
}
