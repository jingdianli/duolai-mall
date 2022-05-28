package com.cskaoyan.order.api;

import com.cskaoyan.mall.api.CartService;
import com.cskaoyan.order.configuration.CartFeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "duolai-shopping", configuration = CartFeignClientConfiguration.class)
public interface CartFeignClient extends CartService {

}
