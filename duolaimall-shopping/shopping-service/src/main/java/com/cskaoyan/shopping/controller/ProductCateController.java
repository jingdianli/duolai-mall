package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:42
 */

@RestController
@RequestMapping("/shopping")
public class ProductCateController {
    /**
     * 列举所有商品种类
     *
     * @param sort
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:44
     */
    @GetMapping("/categories")
    public ResponseData allProductCateList(@RequestParam(value = "sort", required = false, defaultValue = "1") String sort) {
        return null;
    }
}
