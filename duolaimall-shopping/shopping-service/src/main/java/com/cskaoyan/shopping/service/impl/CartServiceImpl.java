package com.cskaoyan.shopping.service.impl;

import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.mall.dto.ClearCartItemRequest;
import com.cskaoyan.mall.dto.ClearCartItemResponse;
import com.cskaoyan.shopping.dto.*;
import com.cskaoyan.shopping.service.ICartService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Jingdian Li
 * @since 2022/05/27 10:14
 */

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    RedissonClient redissonClient;

    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        Long userId = request.getUserId();
        CartListByIdResponse cartListByIdResponse = new CartListByIdResponse();
        try {
            RMap<String, CartProductDto> map = redissonClient.getMap(userId.toString());
            List<CartProductDto> cartProductDtoList = new ArrayList<>();
            if (map == null) {
                //若购物车为空
                cartProductDtoList = null;
                cartListByIdResponse.setCartProductDtos(cartProductDtoList);
            } else {
                //若购物车不为空，拿到购物车中所有产品的productId,即key的集合。
                Set<String> keySet = map.keySet();
                //根据所有的key值，得到所有对应的产品对象集合
                for (String item : keySet) {
                    CartProductDto cartProductDto = map.get(item);
                    cartProductDtoList.add(cartProductDto);
                }
                cartListByIdResponse.setCartProductDtos(cartProductDtoList);
            }
            cartListByIdResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
            cartListByIdResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            cartListByIdResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            cartListByIdResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        return cartListByIdResponse;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        return null;
    }

    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
        return null;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        return null;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        return null;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        return null;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }
}
