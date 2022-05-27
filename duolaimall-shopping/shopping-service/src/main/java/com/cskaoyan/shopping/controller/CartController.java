package com.cskaoyan.shopping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.shopping.dto.AddCartRequest;
import com.cskaoyan.shopping.dto.AddCartResponse;
import com.cskaoyan.shopping.dto.CartListByIdRequest;
import com.cskaoyan.shopping.dto.CartListByIdResponse;
import com.cskaoyan.shopping.form.CartForm;
import com.cskaoyan.shopping.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:50
 */

@RestController
@RequestMapping("/shopping")
public class CartController {
    @Autowired
    ICartService cartService;
    /**
     * 获得购物车列表
     *
     * @param request
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:56
     */
    @GetMapping("/carts")
    public ResponseData carts(HttpServletRequest request) {
        // 查询购物车商品
        String userInfo = (String) request.getHeader("user_info");
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());
        CartListByIdRequest cartListByIdRequest = new CartListByIdRequest();
        cartListByIdRequest.setUserId(uid);
        CartListByIdResponse cartList = cartService.getCartListById(cartListByIdRequest);
        if (ShoppingRetCode.SUCCESS.getCode().equals(cartList.getCode())) {
            return new ResponseUtil().setData(cartList.getCartProductDtos());
        }
        return new ResponseUtil().setErrorMsg(cartList.getMsg());
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
        AddCartRequest addCartRequest = new AddCartRequest();
        addCartRequest.setUserId(cartForm.getUserId());
        addCartRequest.setItemId(cartForm.getProductId());
        addCartRequest.setNum(cartForm.getProductNum());
        AddCartResponse addCartResponse = cartService.addToCart(addCartRequest);
        if (ShoppingRetCode.SUCCESS.getCode().equals(addCartResponse.getCode())) {
            return new ResponseUtil().setData("成功");
        }
        return new ResponseUtil().setErrorMsg(addCartResponse.getMsg());
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
