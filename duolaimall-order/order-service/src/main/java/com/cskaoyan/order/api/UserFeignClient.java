package com.cskaoyan.order.api;

import com.cskaoyan.order.configuration.UserFeignClientConfiguration;
import com.cskaoyan.user.service.UserService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "duolai-user", configuration = UserFeignClientConfiguration.class)
public interface UserFeignClient extends UserService {

}
