package com.cskaoyan.order.biz.handler;

import com.cskaoyan.mall.commons.exception.BizException;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.mall.dto.ClearCartItemRequest;
import com.cskaoyan.mall.dto.ClearCartItemResponse;
import com.cskaoyan.order.api.CartFeignClient;
import com.cskaoyan.order.biz.context.CreateOrderContext;
import com.cskaoyan.order.biz.context.TransHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
public class ClearCartItemHandler extends AbstractTransHandler {
    @Autowired
    CartFeignClient cartService;

    //是否采用异步方式执行
    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        log.info("begin - ClearCartItemHandler-context:" + context);
        CreateOrderContext createOrderContext = (CreateOrderContext) context;

        ClearCartItemRequest request = new ClearCartItemRequest();
        request.setUserId(createOrderContext.getUserId());
        request.setProductIds(createOrderContext.getBuyProductIds());
        log.info("carService == null" + Objects.isNull(cartService) + "{}", request);
        ClearCartItemResponse clearCartItemResponse = cartService.clearCartItemByUserID(request);
        if (!ShoppingRetCode.SUCCESS.getCode().equals(clearCartItemResponse.getCode())) {
            throw new BizException(clearCartItemResponse.getCode(), clearCartItemResponse.getMsg());
        }
        return true;
    }
}
