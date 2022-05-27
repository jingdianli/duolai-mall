package com.cskaoyan.order.biz.handler;

import com.cskaoyan.mall.commons.exception.BizException;
import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.order.api.UserFeignClient;
import com.cskaoyan.order.biz.context.CreateOrderContext;
import com.cskaoyan.order.biz.context.TransHandlerContext;
import com.cskaoyan.user.dto.QueryMemberRequest;
import com.cskaoyan.user.dto.QueryMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateHandler extends AbstractTransHandler {
    @Autowired
    UserFeignClient userFeignClient;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        log.info("begin ValidateHandler :context:" + context);
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        QueryMemberRequest queryMemberRequest = new QueryMemberRequest();
        queryMemberRequest.setUserId(createOrderContext.getUserId());
        QueryMemberResponse response = userFeignClient.queryMemberById(queryMemberRequest);
        if(OrderRetCode.SUCCESS.getCode().equals(response.getCode())){
            createOrderContext.setBuyerNickName(response.getUsername());
        }else{
            throw new BizException(response.getCode(), response.getMsg());
        }
        return true;
    }
}
