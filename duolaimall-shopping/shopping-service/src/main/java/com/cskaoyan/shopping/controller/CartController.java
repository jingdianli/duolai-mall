package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.shopping.form.CartForm;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:50
 */

public class CartController {
    /**
     * 获得购物车列表
     * @param request
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:56
     */
    @GetMapping("/carts")
    public ResponseData carts(HttpServletRequest request) {
        return null;
    }

    /**
     * 添加商品到购物车
     * @param cartForm
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:57
     */
    @PostMapping("/carts")
    public ResponseData carts(@RequestBody CartForm cartForm) {
        return null;
    }

    /**
     * 更新购物车中的商品
     * @param cartForm
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:57
     */
    @PutMapping("/carts")
    public ResponseData updateCarts(@RequestBody CartForm cartForm) {
        return null;
    }

    /**
     * 删除购物车中的商品
     * @param uid
     * @param pid
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:57
     */
    @DeleteMapping("/carts/{uid}/{pid}")
    public ResponseData deleteCarts(@PathVariable("uid") long uid, @PathVariable("pid") long pid) {
        return null;
    }

    /**
     * 全选/全不选购物车商品
     * @param cartForm
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:57
     */
    @PutMapping("/items")
    public ResponseData checkCarts(@RequestBody CartForm cartForm) {
        return null;
    }

    /**
     * 删除购物车中选中的商品
     * @param id
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:57
     */
    @DeleteMapping("/items/{id}")
    public ResponseData deleteCheckCartItem(@PathVariable("id") Long id) {
        return null;
    }
}
