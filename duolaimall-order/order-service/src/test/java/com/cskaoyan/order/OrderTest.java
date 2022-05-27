package com.cskaoyan.order;

import com.cskaoyan.mall.commons.exception.BizException;
import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.order.api.ProductFeignClient;
import com.cskaoyan.order.api.UserFeignClient;
import com.cskaoyan.user.dto.QueryMemberRequest;
import com.cskaoyan.user.dto.QueryMemberResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderTest {


    @Autowired
    ProductFeignClient productFeignClient;

    public void testCallRemoteService() {

    }

    @Autowired
    UserFeignClient userFeignClient;

    @Test
    public void testCallRemoteUserService() {
        QueryMemberRequest queryRequest = new QueryMemberRequest(71L);
        QueryMemberResponse queryResponse = userFeignClient.queryMemberById(queryRequest);
        System.out.println(queryResponse);
    }
}
