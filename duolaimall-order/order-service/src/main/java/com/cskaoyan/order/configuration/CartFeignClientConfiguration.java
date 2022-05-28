package com.cskaoyan.order.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @author Jingdian Li
 * @since 2022/05/28 08:21
 */

public class CartFeignClientConfiguration {
    @Bean
    public Logger.Level level() {
        // 让Feign打印所有请求的细节
        return Logger.Level.FULL;
    }
}
