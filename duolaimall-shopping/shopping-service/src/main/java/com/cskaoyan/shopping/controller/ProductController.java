package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.shopping.form.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:45
 */

public class ProductController {
    /**
     * 查看商品详情
     *
     * @param id 
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47 
     */
    @GetMapping("/product/{id}")
    public ResponseData product(@PathVariable long id){
        return null;
    }

    /**
     * 分⻚页查询商品列列表
     *
     * @param pageInfo 
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47 
     */
    @GetMapping("/goods")
    public ResponseData goods(PageInfo pageInfo){
        return null;
    }

    /**
     * 查询推荐商品
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47 
     */
    @GetMapping("/recommend")
    public ResponseData recommend(){
        return null;
    }
}
